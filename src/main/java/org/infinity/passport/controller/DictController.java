package org.infinity.passport.controller;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.Dict;
import org.infinity.passport.dto.DictDTO;
import org.infinity.passport.exception.FieldValidationException;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.DictRepository;
import org.infinity.passport.service.DictService;
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

@RestController
@Api(tags = "数据字典")
public class DictController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictController.class);

    @Autowired
    private DictService         dictService;

    @Autowired
    private DictRepository      dictRepository;

    @Autowired
    private HttpHeaderCreator   httpHeaderCreator;

    @ApiOperation("创建数据字典")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "成功创建"), @ApiResponse(code = 400, message = "字典名已存在") })
    @RequestMapping(value = "/api/dict/dicts", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<Void> createDict(
            @ApiParam(value = "数据字典信息", required = true) @Valid @RequestBody DictDTO dictDTO) {
        LOGGER.debug("REST create dict : {}", dictDTO);
        if (dictRepository.findOneByDictCode(dictDTO.getDictCode()).isPresent()) {
            throw new FieldValidationException("dictDTO", "dictCode", dictDTO.getDictName(), "error.dict.exists",
                    dictDTO.getDictName());
        }

        Dict dict = dictService.insert(dictDTO.getDictCode(), dictDTO.getDictName(), dictDTO.getRemark(),
                dictDTO.getEnabled());
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpHeaderCreator.createSuccessHeader("notification.dict.created", dict.getDictName()))
                .build();
    }

    @ApiOperation("获取数据字典分页列表")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取") })
    @RequestMapping(value = "/api/dict/dicts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<List<DictDTO>> getDicts(Pageable pageable,
            @ApiParam(value = "字典名称", required = false) @RequestParam(value = "dictName", required = false) String dictName)
            throws URISyntaxException {
        Page<Dict> dicts = StringUtils.isEmpty(dictName) ? dictRepository.findAll(pageable)
                : dictRepository.findByDictName(pageable, dictName);
        List<DictDTO> dictDTOs = dicts.getContent().stream().map(dict -> dict.asDTO()).collect(Collectors.toList());
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(dicts, "/api/dict/dicts");
        return new ResponseEntity<>(dictDTOs, headers, HttpStatus.OK);
    }

    @ApiOperation("根据字典ID检索数据字典信息")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取"), @ApiResponse(code = 400, message = "数据字典不存在") })
    @RequestMapping(value = "/api/dict/dicts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.DEVELOPER, Authority.USER })
    @Timed
    public ResponseEntity<DictDTO> getDict(@ApiParam(value = "字典编号", required = true) @PathVariable String id) {
        LOGGER.debug("REST request to get dict : {}", id);
        Dict dict = Optional.ofNullable(dictRepository.findOne(id)).orElseThrow(() -> new NoDataException(id));
        return new ResponseEntity<>(dict.asDTO(), HttpStatus.OK);
    }

    @ApiOperation("根据字典的状态获取数据字典")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取") })
    @RequestMapping(value = "/api/dict/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.DEVELOPER, Authority.USER })
    @Timed
    public ResponseEntity<List<DictDTO>> getDicts(
            @ApiParam(value = "是否可用,null代表全部", required = false, allowableValues = "false,true,null") @RequestParam(value = "enabled", required = false) Boolean enabled) {
        List<Dict> dicts = new ArrayList<Dict>();
        if (enabled == null) {
            dicts = dictRepository.findAll();
        } else {
            dicts = dictRepository.findByEnabled(enabled);
        }
        List<DictDTO> dictDTOs = dicts.stream().map(dict -> dict.asDTO()).collect(Collectors.toList());
        return new ResponseEntity<>(dictDTOs, HttpStatus.OK);
    }

    @ApiOperation("更新数据字典信息")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功更新"), @ApiResponse(code = 400, message = "数据字典不存在") })
    @RequestMapping(value = "/api/dict/dicts", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<Void> updateDict(
            @ApiParam(value = "新的数据字典信息", required = true) @Valid @RequestBody DictDTO dictDTO) {
        Optional.ofNullable(dictRepository.findOne(dictDTO.getId()))
                .orElseThrow(() -> new NoDataException(dictDTO.getId()));
        dictService.update(dictDTO.getId(), dictDTO.getDictCode(), dictDTO.getDictName(), dictDTO.getRemark(),
                dictDTO.getEnabled());
        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaderCreator.createSuccessHeader("notification.dict.updated", dictDTO.getDictName()))
                .build();
    }

    @ApiOperation(value = "根据字典ID删除数据字典信息", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功删除"), @ApiResponse(code = 400, message = "数据字典不存在") })
    @RequestMapping(value = "/api/dict/dicts/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<Void> deleteDict(@ApiParam(value = "字典编号", required = true) @PathVariable String id) {
        LOGGER.debug("REST request to delete dict: {}", id);
        Dict dict = Optional.ofNullable(dictRepository.findOne(id)).orElseThrow(() -> new NoDataException(id));
        dictRepository.delete(id);
        LOGGER.info("Deleted dict");
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("notification.dict.deleted", dict.getDictName()))
                .build();
    }
}
