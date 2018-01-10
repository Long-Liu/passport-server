package org.infinity.passport.controller;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.infinity.passport.domain.AppAuthority;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.dto.AppAuthorityDTO;
import org.infinity.passport.exception.FieldValidationException;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.AppAuthorityRepository;
import org.infinity.passport.service.AppAuthorityService;
import org.infinity.passport.utils.HttpHeaderCreator;
import org.infinity.passport.utils.PaginationUtils;
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
 * REST controller for managing the app authority.
 */
@RestController
@Api(tags = "应用权限")
public class AppAuthorityController {

    private static final Logger    LOGGER = LoggerFactory.getLogger(AppAuthorityController.class);

    @Autowired
    private AppAuthorityService    appAuthorityService;

    @Autowired
    private AppAuthorityRepository appAuthorityRepository;

    @Autowired
    private HttpHeaderCreator      httpHeaderCreator;

    @ApiOperation("创建应用权限")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "成功创建"), @ApiResponse(code = 400, message = "字典名已存在") })
    @RequestMapping(value = "/api/app-authority/app-authorities", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<Void> createAppAuthority(
            @ApiParam(value = "应用权限信息", required = true) @Valid @RequestBody AppAuthorityDTO appAuthorityDTO) {
        LOGGER.debug("REST create dict : {}", appAuthorityDTO);
        if (appAuthorityRepository
                .findOneByAppNameAndAuthorityName(appAuthorityDTO.getAppName(), appAuthorityDTO.getAuthorityName())
                .isPresent()) {
            throw new FieldValidationException("appAuthorityDTO", "appName+authorityName",
                    MessageFormat.format("appName: {0}, authorityName: {1}", appAuthorityDTO.getAppName(),
                            appAuthorityDTO.getAuthorityName()),
                    "error.app.name.authority.name.exist", MessageFormat.format("appName: {0}, authorityName: {1}",
                            appAuthorityDTO.getAppName(), appAuthorityDTO));
        }

        AppAuthority appAuthority = appAuthorityService.insert(appAuthorityDTO.getAppName(),
                appAuthorityDTO.getAuthorityName());
        return ResponseEntity
                .status(HttpStatus.CREATED).headers(httpHeaderCreator
                        .createSuccessHeader("notification.app.authority.created", appAuthority.getAuthorityName()))
                .build();
    }

    @ApiOperation("获取应用权限分页列表")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取") })
    @RequestMapping(value = "/api/app-authority/app-authorities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<List<AppAuthorityDTO>> getAppAuthorities(Pageable pageable,
            @ApiParam(value = "应用名称", required = false) @RequestParam(value = "appName", required = false) String appName,
            @ApiParam(value = "权限名称", required = false) @RequestParam(value = "authorityName", required = false) String authorityName)
            throws URISyntaxException {
        Page<AppAuthority> appAuthorities = appAuthorityService.findByAppNameAndAuthorityNameCombinations(pageable,
                appName, authorityName);
        List<AppAuthorityDTO> appAuthorityDTOs = appAuthorities.getContent().stream()
                .map(appAuthority -> appAuthority.asDTO()).collect(Collectors.toList());
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(appAuthorities,
                "/api/app-authority/app-authorities");
        return new ResponseEntity<>(appAuthorityDTOs, headers, HttpStatus.OK);
    }

    @ApiOperation("根据字典ID检索应用权限信息")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取"), @ApiResponse(code = 400, message = "应用权限不存在") })
    @RequestMapping(value = "/api/app-authority/app-authorities/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.DEVELOPER, Authority.USER })
    @Timed
    public ResponseEntity<AppAuthorityDTO> getAppAuthority(
            @ApiParam(value = "字典编号", required = true) @PathVariable String id) {
        LOGGER.debug("REST request to get app authority : {}", id);
        AppAuthority appAuthority = Optional.ofNullable(appAuthorityRepository.findOne(id))
                .orElseThrow(() -> new NoDataException(id));
        return new ResponseEntity<>(appAuthority.asDTO(), HttpStatus.OK);
    }

    @ApiOperation("根据应用名称检索应用权限信息")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取") })
    @RequestMapping(value = "/api/app-authority/app-name/{appName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<List<AppAuthorityDTO>> getAppAuthorities(
            @ApiParam(value = "应用名称", required = true) @PathVariable String appName) {
        LOGGER.debug("REST request to get app authorities : {}", appName);
        List<AppAuthority> appAuthorities = appAuthorityRepository.findByAppName(appName);
        if (CollectionUtils.isEmpty(appAuthorities)) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        List<AppAuthorityDTO> items = appAuthorities.stream().map(item -> item.asDTO()).collect(Collectors.toList());
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @ApiOperation("更新应用权限信息")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功更新"), @ApiResponse(code = 400, message = "应用权限不存在") })
    @RequestMapping(value = "/api/app-authority/app-authorities", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<Void> updateAppAuthority(
            @ApiParam(value = "新的应用权限信息", required = true) @Valid @RequestBody AppAuthorityDTO appAuthorityDTO) {
        Optional.ofNullable(appAuthorityRepository.findOne(appAuthorityDTO.getId()))
                .orElseThrow(() -> new NoDataException(appAuthorityDTO.getId()));
        appAuthorityService.update(appAuthorityDTO.getId(), appAuthorityDTO.getAppName(),
                appAuthorityDTO.getAuthorityName());
        return ResponseEntity
                .status(HttpStatus.OK).headers(httpHeaderCreator
                        .createSuccessHeader("notification.app.authority.updated", appAuthorityDTO.getAuthorityName()))
                .build();
    }

    @ApiOperation(value = "根据字典ID删除应用权限信息", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功删除"), @ApiResponse(code = 400, message = "应用权限不存在") })
    @RequestMapping(value = "/api/app-authority/app-authorities/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<Void> deleteAppAuthority(@ApiParam(value = "字典编号", required = true) @PathVariable String id) {
        LOGGER.debug("REST request to delete app authority: {}", id);
        AppAuthority appAuthority = Optional.ofNullable(appAuthorityRepository.findOne(id))
                .orElseThrow(() -> new NoDataException(id));
        appAuthorityRepository.delete(id);
        LOGGER.info("Deleted app authority");
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("notification.app.authority.deleted",
                appAuthority.getAuthorityName())).build();
    }
}
