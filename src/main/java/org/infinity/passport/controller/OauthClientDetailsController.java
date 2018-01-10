//package org.infinity.passport.controller;
//
//import java.net.URISyntaxException;
//import java.security.SecureRandom;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//import javax.validation.Valid;
//
//import org.apache.commons.lang3.StringUtils;
//import org.infinity.commons.utilities.IdWorker;
//import org.infinity.passport.domain.Authority;
//import org.infinity.passport.domain.OauthClientDetails;
//import org.infinity.passport.dto.OauthClientDetailsDTO;
//import org.infinity.passport.exception.FieldValidationException;
//import org.infinity.passport.exception.NoDataException;
//import org.infinity.passport.service.OAuth2ClientDetailsService;
//import org.infinity.passport.utils.HttpHeaderCreator;
//import org.infinity.passport.utils.PaginationUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.annotation.Secured;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.codahale.metrics.annotation.Timed;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import io.swagger.annotations.ApiResponse;
//import io.swagger.annotations.ApiResponses;
//
//@RestController
//@Api(tags = "单点登录客户端信息")
//public class OauthClientDetailsController {
//
//    private static final Logger       LOGGER = LoggerFactory.getLogger(OauthClientDetailsController.class);
//
//    @Autowired
//    private OAuth2ClientDetailsService oauthClientDetailsService;
//
//    @Autowired
//    private HttpHeaderCreator         httpHeaderCreator;
//
//    @ApiOperation("创建单点登录客户端信息")
//    @ApiResponses(value = { @ApiResponse(code = 201, message = "成功创建"), @ApiResponse(code = 400, message = "字典名已存在") })
//    @RequestMapping(value = "/api/oauth-client/details", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Secured(Authority.ADMIN)
//    @Timed
//    public ResponseEntity<Void> createClientDetail(
//            @ApiParam(value = "单点登录客户端信息", required = true) @Valid @RequestBody OauthClientDetailsDTO oauthClientDetailsDTO) {
//        LOGGER.debug("REST create oauth client detail: {}", oauthClientDetailsDTO);
//        if (oauthClientDetailsDTO.getClientId() != null) {
//            if (oauthClientDetailsService.findById(oauthClientDetailsDTO.getClientId()) != null) {
//                throw new FieldValidationException("oauthClientDetailsDTO", "clientId",
//                        oauthClientDetailsDTO.getClientId(), "error.oauth.client.detail.id.exists",
//                        oauthClientDetailsDTO.getClientId());
//            }
//        }
//
//        String clientId = StringUtils.isNotEmpty(oauthClientDetailsDTO.getClientId())
//                ? oauthClientDetailsDTO.getClientId()
//                : "" + new IdWorker(new SecureRandom().nextInt(10), new SecureRandom().nextInt(5)).nextId();
//        String clientSecret = StringUtils.isNotEmpty(oauthClientDetailsDTO.getClientSecret())
//                ? oauthClientDetailsDTO.getClientSecret()
//                : UUID.randomUUID().toString().replace("-", "");
//        oauthClientDetailsService.insert(clientId, oauthClientDetailsDTO.getResourceIds(), clientSecret,
//                oauthClientDetailsDTO.getScope(), oauthClientDetailsDTO.getAuthorizedGrantTypes(),
//                oauthClientDetailsDTO.getWebServerRedirectUri(), oauthClientDetailsDTO.getAuthorities(),
//                oauthClientDetailsDTO.getAccessTokenValidity(), oauthClientDetailsDTO.getRefreshTokenValidity(),
//                oauthClientDetailsDTO.getAdditionalInformation(), oauthClientDetailsDTO.getAutoapprove().toString());
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .headers(httpHeaderCreator.createSuccessHeader("notification.oauth.client.detail.created", clientId))
//                .build();
//    }
//
//    @ApiOperation("获取单点登录客户端信息分页列表")
//    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取") })
//    @RequestMapping(value = "/api/oauth-client/details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Secured(Authority.ADMIN)
//    @Timed
//    public ResponseEntity<List<OauthClientDetailsDTO>> getClientDetails(Pageable pageable,
//            @ApiParam(value = "客户端ID", required = false) @RequestParam(value = "clientDetailId", required = false) String clientId)
//            throws URISyntaxException {
//        Page<OauthClientDetails> details = oauthClientDetailsService.paginate(pageable, clientId);
//        List<OauthClientDetailsDTO> oauthClientDetailsDTOs = details.getContent().stream()
//                .map(oauthClientDetails -> new OauthClientDetailsDTO(oauthClientDetails)).collect(Collectors.toList());
//        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(details, "/api/oauth-client/details");
//        return new ResponseEntity<>(oauthClientDetailsDTOs, headers, HttpStatus.OK);
//    }
//
//    @ApiOperation("根据客户端ID检索单点登录客户端信息")
//    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取"),
//            @ApiResponse(code = 400, message = "单点登录客户端信息不存在") })
//    @RequestMapping(value = "/api/oauth-client/details/{clientId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Secured({ Authority.ADMIN })
//    @Timed
//    public ResponseEntity<OauthClientDetailsDTO> getClientDetail(
//            @ApiParam(value = "客户端ID", required = true) @PathVariable String clientId) {
//        LOGGER.debug("REST request to get oauth client detail : {}", clientId);
//        OauthClientDetails oauthClientDetails = oauthClientDetailsService.findById(clientId);
//        if (oauthClientDetails == null) {
//            throw new NoDataException(clientId);
//        }
//        return new ResponseEntity<>(new OauthClientDetailsDTO(oauthClientDetails), HttpStatus.OK);
//    }
//
//    @ApiOperation("更新单点登录客户端信息")
//    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功更新"),
//            @ApiResponse(code = 400, message = "单点登录客户端信息不存在") })
//    @RequestMapping(value = "/api/oauth-client/details", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Secured(Authority.ADMIN)
//    @Timed
//    public ResponseEntity<Void> updateClientDetail(
//            @ApiParam(value = "新的单点登录客户端信息", required = true) @Valid @RequestBody OauthClientDetailsDTO oauthClientDetailsDTO) {
//        if (oauthClientDetailsService.findById(oauthClientDetailsDTO.getClientId()) == null) {
//            throw new NoDataException(oauthClientDetailsDTO.getClientId());
//        }
//        OauthClientDetails entity = new OauthClientDetails();
//        BeanUtils.copyProperties(oauthClientDetailsDTO, entity);
//        entity.setAutoapprove("" + oauthClientDetailsDTO.getAutoapprove());
//        oauthClientDetailsService.update(entity);
//
//        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaderCreator
//                .createSuccessHeader("notification.oauth.client.detail.updated", oauthClientDetailsDTO.getClientId()))
//                .build();
//
//    }
//
//    @ApiOperation(value = "根据客户端ID删除单点登录客户端信息", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
//    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功删除"),
//            @ApiResponse(code = 400, message = "单点登录客户端信息不存在") })
//    @RequestMapping(value = "/api/oauth-client/details/{clientId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Secured(Authority.ADMIN)
//    @Timed
//    public ResponseEntity<Void> deleteClientDetail(
//            @ApiParam(value = "客户端ID", required = true) @PathVariable String clientId) {
//        LOGGER.debug("REST request to delete oauth client detail: {}", clientId);
//        OauthClientDetails oauthClientDetails = oauthClientDetailsService.findById(clientId);
//        if (oauthClientDetails == null) {
//            throw new NoDataException(clientId);
//        }
//        oauthClientDetailsService.delete(clientId);
//        LOGGER.info("Deleted oauth client detail");
//        return ResponseEntity.ok()
//                .headers(httpHeaderCreator.createSuccessHeader("notification.oauth.client.detail.deleted", clientId))
//                .build();
//    }
//}
