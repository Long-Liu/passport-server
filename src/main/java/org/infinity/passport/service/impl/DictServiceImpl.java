package org.infinity.passport.service.impl;

import java.util.Map;
import java.util.stream.Collectors;

import org.infinity.passport.domain.Dict;
import org.infinity.passport.repository.DictRepository;
import org.infinity.passport.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DictServiceImpl implements DictService {

    @Autowired
    private DictRepository dictRepository;

    @Override
    public Map<String, String> findDictCodeDictNameMap() {
        return dictRepository.findAll().stream().collect(Collectors.toMap(Dict::getDictCode, Dict::getDictName));
    }

    @Override
    public Dict insert(String dictCode, String dictName, String remark, Boolean enabled) {
        return dictRepository.save(new Dict(dictCode, dictName, remark, enabled));
    }

    @Override
    public void update(String id, String dictCode, String dictName, String remark, Boolean enabled) {
        Dict entity = dictRepository.findOne(id);
        if (entity != null) {
            entity.setDictCode(dictCode);
            entity.setDictName(dictName);
            entity.setRemark(remark);
            entity.setEnabled(enabled);
            dictRepository.save(entity);
        }
    }
}