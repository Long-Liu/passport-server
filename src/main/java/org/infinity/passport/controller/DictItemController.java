package org.infinity.passport.controller;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.DictItem;
import org.infinity.passport.dto.DictItemDTO;
import org.infinity.passport.exception.FieldValidationException;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.DictItemRepository;
import org.infinity.passport.repository.DictRepository;
import org.infinity.passport.service.DictItemService;
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
@Api(tags = "数据字典项")
public class DictItemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictItemController.class);

    @Autowired
    private DictItemService     dictItemService;

    @Autowired
    private DictItemRepository  dictItemRepository;

    @Autowired
    private DictService         dictService;

    @Autowired
    private DictRepository      dictRepository;

    @Autowired
    private HttpHeaderCreator   httpHeaderCreator;

    @ApiOperation("创建数据字典项")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "成功创建"),
            @ApiResponse(code = 400, message = "字典code不存在或相同的字典code和字典项已存在") })
    @RequestMapping(value = "/api/dict-item/items", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<Void> createDictItem(
            @ApiParam(value = "数据字典项信息", required = true) @Valid @RequestBody DictItemDTO dictItemDTO,
            HttpServletRequest request) {
        // 判断dictCode是否存在
        if (!dictRepository.findOneByDictCode(dictItemDTO.getDictCode()).isPresent()) {
            throw new FieldValidationException("dicDTO", "dictCode", dictItemDTO.getDictCode(), "error.dict.not.exist",
                    dictItemDTO.getDictCode());
        }
        // 根据dictItemCode与dictCode查询记录是否存在
        List<DictItem> existingDictItems = dictItemRepository.findByDictCodeAndDictItemCode(dictItemDTO.getDictCode(),
                dictItemDTO.getDictItemCode());
        if (CollectionUtils.isNotEmpty(existingDictItems)) {
            throw new FieldValidationException("dictItemDTO", "dictCode+dictItemCode",
                    MessageFormat.format("dictCode: {0}, dictItemCode: {1}", dictItemDTO.getDictCode(),
                            dictItemDTO.getDictItemCode()),
                    "error.dict.item.exist", MessageFormat.format("dictCode: {0}, dictItemCode: {1}",
                            dictItemDTO.getDictCode(), dictItemDTO.getDictItemCode()));
        }
        DictItem dictItem = dictItemService.insert(dictItemDTO.getDictCode(), dictItemDTO.getDictItemCode(),
                dictItemDTO.getDictItemName(), dictItemDTO.getRemark(), dictItemDTO.getEnabled());
        return ResponseEntity.status(HttpStatus.CREATED).headers(
                httpHeaderCreator.createSuccessHeader("notification.dict.item.created", dictItem.getDictItemName()))
                .build();
    }

    @ApiOperation("获取数据字典项分页列表")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取") })
    @RequestMapping(value = "/api/dict-item/items", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<List<DictItemDTO>> getDictItems(Pageable pageable,
            @ApiParam(value = "字典代码", required = false) @RequestParam(value = "dictCode", required = false) String dictCode,
            @ApiParam(value = "字典项名称", required = false) @RequestParam(value = "dictItemName", required = false) String dictItemName)
            throws URISyntaxException {
        Page<DictItem> dictItems = dictItemService.findByDictCodeAndDictItemNameCombinations(pageable, dictCode,
                dictItemName);
        Map<String, String> dictCodeDictNameMap = dictService.findDictCodeDictNameMap();
        List<DictItemDTO> dictItemDTOs = dictItems.getContent().stream().map(dictItem -> {
            DictItemDTO dto = dictItem.asDTO();
            dto.setDictName(dictCodeDictNameMap.get(dto.getDictCode()));
            return dictItem.asDTO();
        }).collect(Collectors.toList());
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(dictItems, "/api/dict-item/items");
        return new ResponseEntity<>(dictItemDTOs, headers, HttpStatus.OK);
    }

    @ApiOperation("根据数据字典项ID检索数据字典项信息")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取"), @ApiResponse(code = 400, message = "字典项不存在") })
    @RequestMapping(value = "/api/dict-item/items/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.USER })
    @Timed
    public ResponseEntity<DictItemDTO> getDictItem(
            @ApiParam(value = "数据字典项ID", required = true) @PathVariable String id) {
        LOGGER.debug("REST request to get dict item : {}", id);
        DictItem dictItem = Optional.ofNullable(dictItemRepository.findOne(id))
                .orElseThrow(() -> new NoDataException(id));
        return new ResponseEntity<>(dictItem.asDTO(), HttpStatus.OK);
    }

    @ApiOperation("根据数据字典代码检索数据字典项信息")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取") })
    @RequestMapping(value = "/api/dict-item/dict-code/{dictCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ Authority.USER })
    @Timed
    public ResponseEntity<List<DictItemDTO>> getDictItems(
            @ApiParam(value = "字典编号", required = true) @PathVariable String dictCode) {
        LOGGER.debug("REST request to get dict item : {}", dictCode);
        // 根据dictCode查询数据字典项信息
        List<DictItem> dictItems = dictItemRepository.findByDictCode(dictCode);
        if (CollectionUtils.isEmpty(dictItems)) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        List<DictItemDTO> dictItemDTOs = dictItems.stream().map(dictItem -> dictItem.asDTO())
                .collect(Collectors.toList());
        return new ResponseEntity<>(dictItemDTOs, HttpStatus.OK);
    }

    @ApiOperation("更新数据字典项信息")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功更新"), @ApiResponse(code = 400, message = "字典项不存在") })
    @RequestMapping(value = "/api/dict-item/items", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<Void> updateDictItem(
            @ApiParam(value = "新的数据字典项信息", required = true) @Valid @RequestBody DictItemDTO dictItemDTO) {
        Optional.ofNullable(dictItemRepository.findOne(dictItemDTO.getId()))
                .orElseThrow(() -> new NoDataException(dictItemDTO.getId()));
        dictItemService.update(dictItemDTO.getId(), dictItemDTO.getDictCode(), dictItemDTO.getDictItemCode(),
                dictItemDTO.getDictItemName(), dictItemDTO.getRemark(), dictItemDTO.getEnabled());
        return ResponseEntity.status(HttpStatus.OK).headers(
                httpHeaderCreator.createSuccessHeader("notification.dict.item.updated", dictItemDTO.getDictItemName()))
                .build();
    }

    @ApiOperation(value = "根据数据字典编号与字典项编号删除数据字典信息", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功删除"), @ApiResponse(code = 400, message = "字典项不存在") })
    @RequestMapping(value = "/api/dict-item/items/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<Void> deleteDictItem(@ApiParam(value = "数据字典项ID", required = true) @PathVariable String id) {
        LOGGER.debug("REST request to delete dict item: {}", id);
        DictItem dictItem = Optional.ofNullable(dictItemRepository.findOne(id))
                .orElseThrow(() -> new NoDataException(id));
        dictItemRepository.delete(id);
        LOGGER.info("Deleted dict Item");
        return ResponseEntity.ok().headers(
                httpHeaderCreator.createSuccessHeader("notification.dict.item.deleted", dictItem.getDictItemName()))
                .build();
    }
}
