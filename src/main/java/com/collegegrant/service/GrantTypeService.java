package com.collegegrant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.GrantTypeWithLevelVO;
import com.collegegrant.entity.GrantType;

public interface GrantTypeService {
    /**
     * 新增助学金
     *
     * @param grantType
     */
    void insert(GrantType grantType);

    /**
     * 分页查询可申请的助学金（带等级列表）
     *
     * @param page 页码
     * @param size 每页条数
     * @return 分页结果
     */
    Page<GrantTypeWithLevelVO> selectAvailableList(int page, int size);

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

    /**
     * 助学金详情
     * @param id
     * @return
     */
    GrantType GrantTypeDetail(Long id);
}
