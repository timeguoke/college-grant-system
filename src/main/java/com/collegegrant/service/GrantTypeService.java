package com.collegegrant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.entity.GrantType;

import java.util.List;

public interface GrantTypeService {
    /**
     *
     * 新增助学金
     *
     * @param grantType
     */
    void insert(GrantType grantType);

    /**
     * 查询助学金详情
     *
     * @return
     */
    List<GrantType> selectAvailableList();

    /**
     * 修改助学金信息
     *
     * @param grantType
     */
    void update(GrantType grantType);

    /**
     * 删除助学金
     *
     * @param id
     */
    void deleteById(Long id);

    /**
     * 助学金分页
     *
     * @param page
     * @param size
     * @param status
     * @return
     */
    Page<GrantType> list(int page, int size, Integer status);

    /**
     * 查询助学金总数
     *
     * @return
     */
    Long countGrantType();
}
