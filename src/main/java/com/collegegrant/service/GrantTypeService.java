package com.collegegrant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.dto.GrantTypeDTO;
import com.collegegrant.pojo.entity.GrantType;
import com.collegegrant.pojo.vo.GrantTypePageVO;
import com.collegegrant.pojo.vo.GrantTypeWithLevelVO;

import java.util.List;

public interface GrantTypeService {

    /**
     * 新增助学金（同时保存等级列表）
     *
     * @param dto 类型+等级信息
     */
    void insertWithLevels(GrantTypeDTO dto);

    /**
     * 分页查询可申请的助学金（带等级列表）
     *
     * @param page 页码
     * @param size 每页条数
     * @return 分页结果
     */
    Page<GrantTypeWithLevelVO> selectAvailableList(int page, int size);

    /**
     * 修改助学金信息（同时维护等级列表）
     *
     * @param dto 类型+等级信息
     */
    void updateWithLevels(GrantTypeDTO dto);

    /**
     * 删除助学金（同时删除其下等级）
     *
     * @param id
     */
    void deleteById(Long id);

    /**
     * 助学金分页（支持名称模糊查询，含等级数量）
     *
     * @param page
     * @param size
     * @param status
     * @param name   类型名称（模糊）
     * @return
     */
    Page<GrantTypePageVO> listPage(int page, int size, Integer status, String name);

    /**
     * 助学金全量列表（下拉选择用，按创建时间降序）
     *
     * @param status 状态过滤，null表示全部
     * @return 类型列表
     */
    List<GrantType> listAll(Integer status);

    /**
     * 查询助学金总数
     *
     * @return
     */
    Long countGrantType();

    /**
     * 助学金类型详情（带等级列表）
     *
     * @param id
     * @return
     */
    GrantTypeWithLevelVO detailWithLevels(Long id);
}
