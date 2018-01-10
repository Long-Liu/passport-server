package org.infinity.passport.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.domain.AdminMenu;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.dto.AdminMenuDTO;
import org.infinity.passport.exception.FieldValidationException;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.AdminMenuRepository;
import org.infinity.passport.service.AdminMenuService;
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
 * REST controller for managing the admin menu.
 */
@RestController
@Api(tags = "管理菜单")
public class AdminMenuController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminMenuController.class);

    @Autowired
    private AdminMenuService    adminMenuService;

    @Autowired
    private AdminMenuRepository adminMenuRepository;

    @Autowired
    private HttpHeaderCreator   httpHeaderCreator;

    @ApiOperation("创建菜单")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "成功创建") })
    @RequestMapping(value = "/api/admin-menu/menus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<Void> createMenu(
            @ApiParam(value = "菜单信息", required = true) @Valid @RequestBody AdminMenuDTO adminMenuDTO) {
        Optional<AdminMenu> result = adminMenuService.findOneByAppNameAndSequence(adminMenuDTO.getAppName(),
                adminMenuDTO.getSequence());
        if (result.isPresent()) {
            throw new FieldValidationException("adminMenuDTO", "appName+sequence",
                    MessageFormat.format("appName: {0}, sequence: {1}", adminMenuDTO.getAppName(),
                            adminMenuDTO.getSequence()),
                    "error.duplication", MessageFormat.format("appName: {0}, sequence: {1}", adminMenuDTO.getAppName(),
                            adminMenuDTO.getSequence()));
        }

        adminMenuService.insert(adminMenuDTO.getAppName(), adminMenuDTO.getAdminMenuName(),
                adminMenuDTO.getAdminMenuChineseText(), adminMenuDTO.getLink(), adminMenuDTO.getSequence(),
                adminMenuDTO.getParentMenuId());
        return ResponseEntity
                .status(HttpStatus.CREATED).headers(httpHeaderCreator
                        .createSuccessHeader("notification.admin.menu.created", adminMenuDTO.getAdminMenuName()))
                .build();
    }

    @ApiOperation("获取菜单信息分页列表")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取") })
    @RequestMapping(value = "/api/admin-menu/menus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<List<AdminMenuDTO>> getMenus(Pageable pageable,
            @ApiParam(value = "应用名称", required = false) @RequestParam(value = "appName", required = false) String appName)
            throws URISyntaxException {
        Page<AdminMenu> adminMenus = StringUtils.isEmpty(appName) ? adminMenuRepository.findAll(pageable)
                : adminMenuRepository.findByAppName(pageable, appName);
        List<AdminMenuDTO> adminMenuDTOs = adminMenus.getContent().stream().map(adminMenu -> adminMenu.asDTO())
                .collect(Collectors.toList());
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(adminMenus, "/api/admin-menu/menus");
        return new ResponseEntity<>(adminMenuDTOs, headers, HttpStatus.OK);
    }

    @ApiOperation("根据菜单ID查询菜单")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取"), @ApiResponse(code = 400, message = "菜单不存在") })
    @RequestMapping(value = "/api/admin-menu/menus/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<AdminMenuDTO> getMenu(@ApiParam(value = "菜单ID", required = true) @PathVariable String id) {
        AdminMenu adminMenu = Optional.ofNullable(adminMenuRepository.findOne(id))
                .orElseThrow(() -> new NoDataException(id));
        return new ResponseEntity<>(adminMenu.asDTO(), HttpStatus.OK);
    }

    @ApiOperation("查询父类菜单")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取") })
    @RequestMapping(value = "/api/admin-menu/parent-menus/{appName}/{level:[0-9]+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<List<AdminMenuDTO>> getAllParentMenu(
            @ApiParam(value = "应用名称", required = true) @PathVariable String appName,
            @ApiParam(value = "菜单级别", required = true) @PathVariable Integer level) {
        List<AdminMenuDTO> adminMenuDTOs = adminMenuRepository.findByAppNameAndLevel(appName, level).stream()
                .map(adminMenu -> adminMenu.asDTO()).collect(Collectors.toList());
        return new ResponseEntity<List<AdminMenuDTO>>(adminMenuDTOs, HttpStatus.OK);
    }

    @ApiOperation("更新菜单")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功更新"), @ApiResponse(code = 400, message = "菜单不存在") })
    @RequestMapping(value = "/api/admin-menu/menus", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<Void> updateMenu(
            @ApiParam(value = "新的菜单信息", required = true) @Valid @RequestBody AdminMenuDTO adminMenuDTO) {
        Optional.ofNullable(adminMenuRepository.findOne(adminMenuDTO.getId()))
                .orElseThrow(() -> new NoDataException(adminMenuDTO.getId()));

        adminMenuService.update(adminMenuDTO.getId(), adminMenuDTO.getAppName(), adminMenuDTO.getAdminMenuName(),
                adminMenuDTO.getAdminMenuChineseText(), adminMenuDTO.getLevel(), adminMenuDTO.getLink(),
                adminMenuDTO.getSequence(), adminMenuDTO.getParentMenuId());
        LOGGER.debug("updated admin menu : {}");
        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaderCreator
                .createSuccessHeader("notification.admin.menu.updated", adminMenuDTO.getAdminMenuName())).build();
    }

    @ApiOperation("根据应用名称和菜单ID删除管理菜单")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功删除"), @ApiResponse(code = 400, message = "菜单不存在") })
    @RequestMapping(value = "/api/admin-menu/menus/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<Void> deleteMenu(@ApiParam(value = "菜单ID", required = true) @PathVariable String id) {
        LOGGER.debug("REST request to delete menu : {}", id);
        AdminMenu adminMenu = Optional.ofNullable(adminMenuRepository.findOne(id))
                .orElseThrow(() -> new NoDataException(id));
        adminMenuRepository.delete(id);
        LOGGER.debug("delete admin menu");
        return ResponseEntity.status(HttpStatus.OK).headers(
                httpHeaderCreator.createSuccessHeader("notification.admin.menu.deleted", adminMenu.getAdminMenuName()))
                .build();
    }

    @ApiOperation(value = "导入管理菜单", notes = "输入文件格式：每行先后appName,adminMenuName,adminMenuChineseText,level,link,sequence数列，列之间使用tab分隔，行之间使用回车换行")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功导入") })
    @RequestMapping(value = "/api/admin-menu/menus/import", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<Void> importData(@ApiParam(value = "文件", required = true) @RequestPart MultipartFile file)
            throws IOException, InterruptedException {
        List<String> lines = IOUtils.readLines(file.getInputStream(), StandardCharsets.UTF_8);
        List<AdminMenu> list = new ArrayList<AdminMenu>();
        for (String line : lines) {
            if (StringUtils.isNotEmpty(line)) {
                String[] lineParts = line.split("\t");

                AdminMenu entity = new AdminMenu(lineParts[0], lineParts[1], lineParts[2],
                        Integer.parseInt(lineParts[3]), lineParts[4], Integer.parseInt(lineParts[5]), null);
                list.add(entity);
            }
        }
        adminMenuRepository.insert(list);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
