package org.infinity.passport.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.User;
import org.infinity.passport.domain.UserAuthority;
import org.infinity.passport.dto.ManagedUserDTO;
import org.infinity.passport.dto.ResetKeyAndPasswordDTO;
import org.infinity.passport.dto.UserDTO;
import org.infinity.passport.exception.FieldValidationException;
import org.infinity.passport.exception.LoginUserNotExistException;
import org.infinity.passport.exception.NoAuthorityException;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.UserAuthorityRepository;
import org.infinity.passport.repository.UserRepository;
import org.infinity.passport.service.AuthorityService;
import org.infinity.passport.service.MailService;
import org.infinity.passport.service.UserService;
import org.infinity.passport.utils.HttpHeaderCreator;
import org.infinity.passport.utils.RandomUtils;
import org.infinity.passport.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * REST controller for managing the user's account.
 */
@RestController
@Api(tags = "账号管理")
public class AccountController {

    private static final Logger     LOGGER = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private UserService             userService;

    @Autowired
    private UserRepository          userRepository;

    @Autowired
    private UserAuthorityRepository userAuthorityRepository;

    @Autowired
    private AuthorityService        authorityService;

    @Autowired
    private MailService             mailService;

    @Autowired
    private HttpHeaderCreator       httpHeaderCreator;

    @ApiOperation(value = "验证当前用户是否已经登录", notes = "登录成功返回当前用户名", response = String.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取"), @ApiResponse(code = 401, message = "未授权") })
    @RequestMapping(value = "/api/account/authenticate", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity<String> isAuthenticated(HttpServletRequest request) {
        LOGGER.debug("REST request to check if the current user is authenticated");
        return new ResponseEntity<>(request.getRemoteUser(), HttpStatus.OK);
    }

    @ApiOperation(value = "获取登录的用户,用于SSO客户端调用", notes = "登录成功返回当前用户")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取"), @ApiResponse(code = 401, message = "未授权") })
    @RequestMapping(value = "/api/account/principal", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Principal> getPrincipal(Principal user) {
        LOGGER.debug("REST request to get current user if the user is authenticated");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @ApiOperation("注册新用户")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "成功创建"), @ApiResponse(code = 400, message = "账号已注册") })
    @RequestMapping(value = "/open-api/account/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> registerAccount(
            @ApiParam(value = "用户信息", required = true) @Valid @RequestBody ManagedUserDTO managedUserDTO,
            HttpServletRequest request) {
        if (userService.findOneByUserName(managedUserDTO.getUserName()).isPresent()) {
            throw new FieldValidationException("userDTO", "userName", managedUserDTO.getUserName(),
                    "error.registration.user.exists", managedUserDTO.getUserName());
        }
        if (userService.findOneByEmail(managedUserDTO.getEmail()).isPresent()) {
            throw new FieldValidationException("userDTO", "email", managedUserDTO.getEmail(),
                    "error.registration.email.exists", managedUserDTO.getEmail());
        }
        if (userService.findOneByMobileNo(managedUserDTO.getMobileNo()).isPresent()) {
            throw new FieldValidationException("userDTO", "mobileNo", managedUserDTO.getMobileNo(),
                    "error.registration.mobile.exists", managedUserDTO.getMobileNo());
        }
        User newUser = userService.insert(managedUserDTO.getUserName(), managedUserDTO.getPassword(),
                managedUserDTO.getFirstName(), managedUserDTO.getLastName(), managedUserDTO.getEmail(),
                managedUserDTO.getMobileNo(), RandomUtils.generateActivationKey(), false,
                managedUserDTO.getAvatarImageUrl(), true, null, null, null);
        String baseUrl = request.getScheme() + // "http"
                "://" + // "://"
                request.getServerName() + // "myhost"
                ":" + // ":"
                request.getServerPort() + // "80"
                request.getContextPath(); // "/myContextPath" or "" if deployed in root context

        mailService.sendActivationEmail(newUser, baseUrl);
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpHeaderCreator.createSuccessHeader("notification.registration.success")).build();
    }

    @ApiOperation("根据激活码激活账户")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功激活"), @ApiResponse(code = 400, message = "激活码不存在") })
    @RequestMapping(value = "/open-api/account/activate/{key:[0-9]+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> activateAccount(@ApiParam(value = "激活码", required = true) @PathVariable String key) {
        userService.activateRegistration(key).orElseThrow(() -> new NoDataException(key));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("获取当前用户信息")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取"), @ApiResponse(code = 400, message = "账号无权限") })
    @RequestMapping(value = "/api/account/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.USER })
    @Timed
    public ResponseEntity<UserDTO> getCurrentAccount() {
        Optional<User> user = userService.findOneByUserName(SecurityUtils.getCurrentUserName());
        List<UserAuthority> userAuthorities = userAuthorityRepository.findByUserId(user.get().getId());

        if (CollectionUtils.isEmpty(userAuthorities)) {
            throw new NoAuthorityException(SecurityUtils.getCurrentUserName());
        }
        Set<String> authorities = userAuthorities.stream().map(UserAuthority::getAuthorityName)
                .collect(Collectors.toSet());
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Signed-In", "true");
        return new ResponseEntity<UserDTO>(new UserDTO(user.get(), authorities), headers, HttpStatus.OK);
    }

    @ApiOperation("获取权限值列表")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取") })
    @RequestMapping(value = "/api/account/authority-names", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.USER })
    @Timed
    public ResponseEntity<List<String>> getAuthorityNames(
            @ApiParam(value = "是否可用,null代表全部", required = false, allowableValues = "false,true,null") @RequestParam(value = "enabled", required = false) Boolean enabled) {
        List<String> authorities = enabled == null ? authorityService.findAllAuthorityNames()
                : authorityService.findAllAuthorityNames(enabled);
        return new ResponseEntity<>(authorities, HttpStatus.OK);
    }

    @ApiOperation("更新当前用户信息")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功更新"),
            @ApiResponse(code = 400, message = "用户未登录或账号已注册"), @ApiResponse(code = 500, message = "登录用户信息已经不存在") })
    @RequestMapping(value = "/api/account/user", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.USER })
    @Timed
    public ResponseEntity<Void> updateCurrentAccount(
            @ApiParam(value = "新的用户信息", required = true) @Valid @RequestBody UserDTO userDTO) {
        User currentUser = userService.findOneByUserName(SecurityUtils.getCurrentUserName())
                .orElseThrow(() -> new LoginUserNotExistException(SecurityUtils.getCurrentUserName()));

        Optional<User> existingUser = userService.findOneByEmail(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getUserName().equalsIgnoreCase(userDTO.getUserName()))) {
            throw new FieldValidationException("userDTO", "email", userDTO.getEmail(),
                    "error.registration.email.exists", userDTO.getEmail());
        }
        existingUser = userService.findOneByMobileNo(userDTO.getMobileNo());
        if (existingUser.isPresent() && (!existingUser.get().getUserName().equalsIgnoreCase(userDTO.getUserName()))) {
            throw new FieldValidationException("userDTO", "mobileNo", userDTO.getMobileNo(),
                    "error.registration.mobile.exists", userDTO.getMobileNo());
        }
        currentUser.setFirstName(userDTO.getFirstName());
        currentUser.setLastName(userDTO.getLastName());
        currentUser.setEmail(userDTO.getEmail());
        currentUser.setMobileNo(userDTO.getMobileNo());
        currentUser.setAvatarImageUrl(userDTO.getAvatarImageUrl());
        currentUser.setModifiedBy(SecurityUtils.getCurrentUserName());
        userRepository.save(currentUser);
        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaderCreator.createSuccessHeader("notification.user.updated", userDTO.getUserName()))
                .build();
    }

    @ApiOperation("修改当前用户的密码")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功更新"), @ApiResponse(code = 400, message = "密码不正确") })
    @RequestMapping(value = "/api/account/password", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.USER })
    @Timed
    public ResponseEntity<Void> changePassword(
            @ApiParam(value = "新密码", required = true) @RequestBody String newPassword) {
        if (!userService.checkValidPasswordLength(newPassword)) {
            throw new FieldValidationException("password", "password", "error.incorrect.password.length");
        }
        userService.changePassword(SecurityUtils.getCurrentUserName(), newPassword);
        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaderCreator.createSuccessHeader("notification.password.changed")).build();
    }

    @ApiOperation("发送重置密码邮件")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功发送"), @ApiResponse(code = 400, message = "账号不存在") })
    @RequestMapping(value = "/open-api/account/reset-password/init", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity<Void> requestPasswordReset(
            @ApiParam(value = "电子邮件", required = true) @RequestBody String email, HttpServletRequest request) {
        User user = userService.requestPasswordReset(email, RandomUtils.generateResetKey()).orElseThrow(
                () -> new FieldValidationException("email", "email", email, "error.email.not.exist", email));
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath();
        mailService.sendPasswordResetMail(user, baseUrl);
        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaderCreator.createSuccessHeader("notification.password.reset.email.sent")).build();
    }

    @ApiOperation("重置密码")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功重置"),
            @ApiResponse(code = 400, message = "重置码无效或已过期") })
    @RequestMapping(value = "/open-api/account/reset-password/finish", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> finishPasswordReset(
            @ApiParam(value = "重置码及新密码信息", required = true) @Valid @RequestBody ResetKeyAndPasswordDTO resetKeyAndPasswordDTO) {
        userService.completePasswordReset(resetKeyAndPasswordDTO.getNewPassword(), resetKeyAndPasswordDTO.getKey())
                .orElseThrow(() -> new FieldValidationException("resetKeyAndPasswordDTO", "key",
                        resetKeyAndPasswordDTO.getKey(), "error.invalid.reset.key"));
        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaderCreator.createSuccessHeader("notification.password.reset")).build();

    }

    @ApiOperation("上传用户头像")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功上传") })
    @RequestMapping(value = "/api/account/avatar/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.USER })
    @Timed
    public ResponseEntity<Void> uploadAvatar(@ApiParam(value = "文件描述", required = true) @RequestPart String description,
            @ApiParam(value = "用户头像文件", required = true) @RequestPart MultipartFile file) {
        return ResponseEntity.ok(null);
    }
}
