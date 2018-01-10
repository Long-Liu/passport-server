package org.infinity.passport.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.User;
import org.infinity.passport.domain.UserAuthority;
import org.infinity.passport.dto.ManagedUserDTO;
import org.infinity.passport.dto.UserDTO;
import org.infinity.passport.exception.FieldValidationException;
import org.infinity.passport.exception.NoAuthorityException;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.UserAuthorityRepository;
import org.infinity.passport.repository.UserRepository;
import org.infinity.passport.security.AjaxLogoutSuccessHandler;
import org.infinity.passport.service.MailService;
import org.infinity.passport.service.UserService;
import org.infinity.passport.utils.HttpHeaderCreator;
import org.infinity.passport.utils.PaginationUtils;
import org.infinity.passport.utils.RandomUtils;
import org.infinity.passport.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * REST controller for managing users.
 */
@RestController
@Api(tags = "用户管理")
public class UserController {

    private static final Logger      LOGGER           = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService              userService;

    @Autowired
    private UserRepository           userRepository;

    @Autowired
    private UserAuthorityRepository  userAuthorityRepository;

    @Autowired
    private MailService              mailService;

    @Autowired
    private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

    @Autowired
    private HttpHeaderCreator        httpHeaderCreator;

    private static final String      DEFAULT_PASSWORD = "123456";

    @ApiOperation(value = "创建新用户并发送激活邮件", response = String.class)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "成功创建"), @ApiResponse(code = 400, message = "账号已注册") })
    @RequestMapping(value = "/api/user/users", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<String> createUser(
            @ApiParam(value = "用户信息", required = true) @Valid @RequestBody UserDTO userDTO,
            HttpServletRequest request) {
        if (userService.findOneByUserName(userDTO.getUserName()).isPresent()) {
            throw new FieldValidationException("userDTO", "userName", userDTO.getUserName(),
                    "error.registration.user.exists", userDTO.getUserName());
        }
        if (userService.findOneByEmail(userDTO.getEmail()).isPresent()) {
            throw new FieldValidationException("userDTO", "email", userDTO.getEmail(),
                    "error.registration.email.exists", userDTO.getEmail());
        }
        if (userService.findOneByMobileNo(userDTO.getMobileNo()).isPresent()) {
            throw new FieldValidationException("userDTO", "mobileNo", userDTO.getMobileNo(),
                    "error.registration.mobile.exists", userDTO.getMobileNo());
        }

        User newUser = userService.insert(userDTO.getUserName(), DEFAULT_PASSWORD, userDTO.getFirstName(),
                userDTO.getLastName(), userDTO.getEmail().toLowerCase(), userDTO.getMobileNo(),
                RandomUtils.generateActivationKey(), userDTO.getActivated(), userDTO.getAvatarImageUrl(),
                userDTO.getEnabled(), RandomUtils.generateResetKey(), Instant.now(), userDTO.getAuthorities());
        String baseUrl = request.getScheme() + // "http"
                "://" + // "://"
                request.getServerName() + // "myhost"
                ":" + // ":"
                request.getServerPort() + // "80"
                request.getContextPath(); // "/myContextPath" or "" if
        // deployed in root context
        mailService.sendCreationEmail(newUser, baseUrl);
        HttpHeaders headers = httpHeaderCreator.createSuccessHeader("notification.user.created", userDTO.getUserName(),
                DEFAULT_PASSWORD);
        return new ResponseEntity<>(DEFAULT_PASSWORD, headers, HttpStatus.OK);
    }

    @ApiOperation("获取用户信息分页列表")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取") })
    @RequestMapping(value = "/api/user/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<List<ManagedUserDTO>> getUsers(Pageable pageable,
            @ApiParam(value = "查询条件", required = false) @RequestParam(value = "login", required = false) String login)
            throws URISyntaxException {
        Page<User> users = StringUtils.isEmpty(login) ? userRepository.findAll(pageable)
                : userService.findByLogin(pageable, login);
        List<ManagedUserDTO> userDTOs = users.getContent().stream().map(user -> new ManagedUserDTO(user, null))
                .collect(Collectors.toList());
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(users, "/api/user/users");
        return new ResponseEntity<>(userDTOs, headers, HttpStatus.OK);
    }

    @ApiOperation("根据用户名检索用户信息")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取"),
            @ApiResponse(code = 400, message = "用户不存在或账号无权限") })
    @RequestMapping(value = "/api/user/users/{userName:[_'.@a-z0-9-]+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<ManagedUserDTO> getUser(
            @ApiParam(value = "用户名", required = true) @PathVariable String userName) {
        LOGGER.debug("REST request to get User : {}", userName);
        User user = userService.findOneByUserName(userName).orElseThrow(() -> new NoDataException(userName));
        List<UserAuthority> userAuthorities = userAuthorityRepository.findByUserId(user.getId());
        if (userAuthorities == null) {
            throw new NoAuthorityException(userName);
        }
        Set<String> authorities = userAuthorities.stream().map(item -> item.getAuthorityName())
                .collect(Collectors.toSet());
        return new ResponseEntity<>(new ManagedUserDTO(user, authorities), HttpStatus.OK);
    }

    @ApiOperation("更新用户信息")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功更新"), @ApiResponse(code = 400, message = "用户不存在"),
            @ApiResponse(code = 400, message = "账号已注册"), @ApiResponse(code = 400, message = "用户不存在"),
            @ApiResponse(code = 400, message = "已激活用户无法变成未激活状态") })
    @RequestMapping(value = "/api/user/users", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<Void> updateUser(
            @ApiParam(value = "新的用户信息", required = true) @Valid @RequestBody UserDTO userDTO,
            HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Optional<User> existingUser = userService.findOneByUserName(userDTO.getUserName());

        if (!existingUser.isPresent()) {
            throw new NoDataException(userDTO.getUserName());
        }
        existingUser = userService.findOneByEmail(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getUserName().equalsIgnoreCase(userDTO.getUserName()))) {
            throw new FieldValidationException("userDTO", "email", userDTO.getEmail(),
                    "error.registration.email.exists", userDTO.getEmail());
        }
        existingUser = userService.findOneByMobileNo(userDTO.getMobileNo());
        if (existingUser.isPresent() && (!existingUser.get().getUserName().equalsIgnoreCase(userDTO.getUserName()))) {
            throw new FieldValidationException("userDTO", "mobileNo", userDTO.getMobileNo(),
                    "error.registration.mobile.exists", userDTO.getMobileNo());
        }
        if (existingUser.isPresent() && !Boolean.TRUE.equals(userDTO.getActivated())
                && Boolean.TRUE.equals(existingUser.get().getActivated())) {
            throw new FieldValidationException("userDTO", "activated", "error.change.active.to.inactive");
        }

        userService.update(userDTO.getUserName().toLowerCase(), userDTO.getFirstName(), userDTO.getLastName(),
                userDTO.getEmail().toLowerCase(), userDTO.getMobileNo(), SecurityUtils.getCurrentUserName(),
                userDTO.getActivated(), userDTO.getAvatarImageUrl(), userDTO.getEnabled(), userDTO.getAuthorities());
        //
        if (userDTO.getUserName().equals(SecurityUtils.getCurrentUserName())) {
            // Remove access token from Redis if authorities of current user
            // were changed
            ajaxLogoutSuccessHandler.onLogoutSuccess(request, response, null);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaderCreator.createSuccessHeader("notification.user.updated", userDTO.getUserName()))
                .build();
    }

    @ApiOperation(value = "根据用户名删除用户", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功删除"), @ApiResponse(code = 400, message = "用户不存在") })
    @RequestMapping(value = "/api/user/users/{userName:[_'.@a-z0-9-]+}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<Void> deleteUser(@ApiParam(value = "用户名", required = true) @PathVariable String userName) {
        LOGGER.debug("REST request to delete User: {}", userName);
        User user = userService.findOneByUserName(userName).orElseThrow(() -> new NoDataException(userName));
        userRepository.delete(user.getId());
        userAuthorityRepository.deleteByUserId(user.getId());
        LOGGER.info("Deleted user");
        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaderCreator.createSuccessHeader("notification.user.deleted", userName)).build();
    }

    @ApiOperation("根据用户名重置密码")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功重置"),
            @ApiResponse(code = 400, message = "用户不存在或账号无权限") })
    @RequestMapping(value = "/api/user/users/{userName:[_'.@a-z0-9-]+}", method = RequestMethod.PUT, produces = MediaType.TEXT_PLAIN_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<String> resetPassword(
            @ApiParam(value = "用户名", required = true) @PathVariable String userName) {
        LOGGER.debug("REST reset the password of User : {}", userName);
        userService.changePassword(userName, DEFAULT_PASSWORD);
        HttpHeaders headers = httpHeaderCreator.createSuccessHeader("notification.password.reset.to.default",
                DEFAULT_PASSWORD);
        return new ResponseEntity<>(DEFAULT_PASSWORD, headers, HttpStatus.OK);
    }
}
