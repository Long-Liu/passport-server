package org.infinity.passport.service;

import java.util.Map;

import org.infinity.passport.domain.Dict;

public interface DictService {

    Dict insert(String dictCode, String dictName, String remark, Boolean enabled);

    void update(String id, String dictCode, String dictName, String remark, Boolean enabled);

    Map<String, String> findDictCodeDictNameMap();

}