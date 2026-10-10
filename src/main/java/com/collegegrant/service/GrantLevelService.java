package com.collegegrant.service;

import com.collegegrant.pojo.entity.GrantLevel;

import java.util.List;

/**
 * 助学金等级Service接口
 */
public interface GrantLevelService {

    /**
     * 根据助学金类型ID查询等级列表（按sort升序）
     *
     * @param typeId 助学金类型ID
     * @return 等级列表
     */
    List<GrantLevel> listByTypeId(Long typeId);

    /**
     * 根据ID查询等级详情
     *
     * @param id 等级ID
     * @return 等级信息
     */
    GrantLevel getById(Long id);

    /**
     * 新增等级
     *
     * @param typeId     助学金类型ID
     * @param grantLevel 等级信息
     */
    void insert(Long typeId, GrantLevel grantLevel);

    /**
     * 修改等级
     *
     * @param grantLevel 等级信息
     */
    void update(GrantLevel grantLevel);

    /**
     * 删除等级
     *
     * @param id 等级ID
     */
    void deleteById(Long id);
}
