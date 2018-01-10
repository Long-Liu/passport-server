package org.infinity.passport.controller;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.infinity.passport.domain.App;
import org.infinity.passport.domain.AppAuthority;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.dto.AppDTO;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.AppAuthorityRepository;
import org.infinity.passport.repository.AppRepository;
import org.infinity.passport.service.AppService;
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
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * REST controller for managing apps.
 */
@RestController
@Api(tags = "应用管理")
public class AppController {

    private static final Logger    LOGGER = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private AppRepository          appRepository;

    @Autowired
    private AppAuthorityRepository appAuthorityRepository;

    @Autowired
    private AppService             appService;

    @Autowired
    private HttpHeaderCreator      httpHeaderCreator;

    @ApiOperation("创建应用")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "成功创建") })
    @RequestMapping(value = "/api/app/apps", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<Void> createApp(
            @ApiParam(value = "应用信息", required = true) @Valid @RequestBody AppDTO appDTO) {
        appService.insert(appDTO.getName(), appDTO.getEnabled(), appDTO.getAuthorities());
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpHeaderCreator.createSuccessHeader("notification.app.created", appDTO.getName())).build();
    }

    @ApiOperation("获取应用列表")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取") })
    @RequestMapping(value = "/api/app/apps", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<List<AppDTO>> getApps(Pageable pageable) throws URISyntaxException {
        Page<App> apps = appRepository.findAll(pageable);
        List<AppDTO> appDTOs = apps.getContent().stream().map(auth -> auth.asDTO()).collect(Collectors.toList());
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(apps, "/api/app/apps");
        return new ResponseEntity<>(appDTOs, headers, HttpStatus.OK);
    }

    @ApiOperation("获取所有应用")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取") })
    @RequestMapping(value = "/api/app/apps/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<List<AppDTO>> getAllApps() {
        List<AppDTO> appDTOs = appRepository.findAll().stream().map(app -> app.asDTO()).collect(Collectors.toList());
        return new ResponseEntity<>(appDTOs, HttpStatus.OK);
    }

    @ApiOperation("根据应用名称检索应用信息")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取"), @ApiResponse(code = 400, message = "应用信息不存在") })
    @RequestMapping(value = "/api/app/apps/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<AppDTO> getApp(@ApiParam(value = "应用名称", required = true) @PathVariable String name) {
        LOGGER.debug("REST request to get app : {}", name);
        App app = appRepository.findOne(name);
        List<AppAuthority> appAuthorities = appAuthorityRepository.findByAppName(name);
        Set<String> authorities = appAuthorities.stream().map(item -> item.getAuthorityName())
                .collect(Collectors.toSet());
        return new ResponseEntity<>(new AppDTO(name, app.getEnabled(), authorities), HttpStatus.OK);
    }

    @ApiOperation("更新应用信息")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功更新"), @ApiResponse(code = 400, message = "应用信息不存在") })
    @RequestMapping(value = "/api/app/apps", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<Void> updateApp(
            @ApiParam(value = "新的应用信息", required = true) @Valid @RequestBody AppDTO appDTO) {
        Optional.ofNullable(appRepository.findOne(appDTO.getName()))
                .orElseThrow(() -> new NoDataException(appDTO.getName()));
        appService.update(appDTO.getName(), appDTO.getEnabled(), appDTO.getAuthorities());
        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaderCreator.createSuccessHeader("notification.app.updated", appDTO.getName())).build();
    }

    @ApiOperation(value = "根据应用名称删除应用信息", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功删除"), @ApiResponse(code = 400, message = "应用信息不存在") })
    @RequestMapping(value = "/api/app/apps/{name}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<Void> deleteAuthority(@ApiParam(value = "应用名称", required = true) @PathVariable String name) {
        LOGGER.debug("REST request to delete app: {}", name);
        Optional.ofNullable(appRepository.findOne(name)).orElseThrow(() -> new NoDataException(name));
        appRepository.delete(name);
        appAuthorityRepository.deleteByAppName(name);
        LOGGER.info("Deleted app");
        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaderCreator.createSuccessHeader("notification.app.deleted", name)).build();
    }
}
