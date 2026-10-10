package com.collegegrant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.vo.GrantTypeWithLevelVO;
import com.collegegrant.pojo.entity.GrantLevel;
import com.collegegrant.pojo.entity.GrantType;
import com.collegegrant.mapper.GrantLevelMapper;
import com.collegegrant.mapper.GrantTypeMapper;
import com.collegegrant.service.GrantTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GrantTypeServiceImpl implements GrantTypeService {

    @Autowired
    private GrantTypeMapper grantTypeMapper;
    @Autowired
    private GrantLevelMapper grantLevelMapper;

    /**
     * 添加助学金
     *
     * @param grantType
     */
    @Override
    @Transactional
    public void insert(GrantType grantType) {
        // ===== 新增：参数非空校验 =====
        if (grantType.getTypeName() == null || grantType.getTypeName().trim().isEmpty()) {
            throw new RuntimeException("助学金名称不能为空");
        }
        // ===== 新增结束 =====

        //先判断该助学金是否已经存在
        String grantTypeName = grantType.getTypeName();
        LambdaQueryWrapper<GrantType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GrantType::getTypeName, grantTypeName);

        GrantType grantTypeOne = grantTypeMapper.selectOne(wrapper);
        if (grantTypeOne != null) {
            throw new RuntimeException("助学金已存在 :" + grantTypeName);
        }
        //助学金不存在进行添加
        grantTypeMapper.insert(grantType);

    }

    /**
     * 分页查询可申请的助学金（带等级列表）
     * 方案：先分页查助学金类型，再批量查等级，手动组装（避免一对多分页数量不准的问题）
     *
     * @param page 页码
     * @param size 每页条数
     * @return 分页结果
     */
    @Override
    public Page<GrantTypeWithLevelVO> selectAvailableList(int page, int size) {
        // 1. 分页查询启用的助学金类型
        Page<GrantType> typePage = new Page<>(page, size);
        LambdaQueryWrapper<GrantType> typeWrapper = new LambdaQueryWrapper<>();
        typeWrapper.eq(GrantType::getStatus, 1)
                   .orderByDesc(GrantType::getCreateTime);
        grantTypeMapper.selectPage(typePage, typeWrapper);

        List<GrantType> typeList = typePage.getRecords();
        if (typeList.isEmpty()) {
            // 没有数据，直接返回空分页
            return new Page<>(typePage.getCurrent(), typePage.getSize(), typePage.getTotal());
        }

        // 2. 提取所有 typeId
        List<Long> typeIds = typeList.stream()
                .map(GrantType::getId)
                .collect(Collectors.toList());

        // 3. 批量查询所有等级
        LambdaQueryWrapper<GrantLevel> levelWrapper = new LambdaQueryWrapper<>();
        levelWrapper.in(GrantLevel::getTypeId, typeIds)
                    .orderByAsc(GrantLevel::getSort);
        List<GrantLevel> levelList = grantLevelMapper.selectList(levelWrapper);

        // 4. 按 typeId 分组
        Map<Long, List<GrantLevel>> levelMap = levelList.stream()
                .collect(Collectors.groupingBy(GrantLevel::getTypeId));

        // 5. 手动组装 VO
        Page<GrantTypeWithLevelVO> voPage = new Page<>(typePage.getCurrent(), typePage.getSize(), typePage.getTotal());
        List<GrantTypeWithLevelVO> voList = typeList.stream().map(type -> {
            GrantTypeWithLevelVO vo = new GrantTypeWithLevelVO();
            vo.setId(type.getId());
            vo.setTypeName(type.getTypeName());
            vo.setDescription(type.getDescription());
            // 从 map 里取对应的等级列表
            List<GrantLevel> levels = levelMap.get(type.getId());
            if (levels != null) {
                List<GrantTypeWithLevelVO.GrantLevelVO> levelVOList = levels.stream().map(level -> {
                    GrantTypeWithLevelVO.GrantLevelVO levelVO = new GrantTypeWithLevelVO.GrantLevelVO();
                    levelVO.setId(level.getId());
                    levelVO.setLevelName(level.getLevelName());
                    levelVO.setAmount(level.getAmount());
                    levelVO.setMinScore(level.getMinScore());
                    levelVO.setSort(level.getSort());
                    return levelVO;
                }).collect(Collectors.toList());
                vo.setLevels(levelVOList);
            }
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 修改助学金信息
     *
     * @param grantType
     */
    @Override
    public void update(GrantType grantType) {

        grantTypeMapper.updateById(grantType);

    }

    /**
     * 删除助学金
     *
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        grantTypeMapper.deleteById(id);
        //删除助学金时顺便删除掉助学金所对应的等级
        LambdaQueryWrapper<GrantLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GrantLevel::getTypeId, id);
        grantLevelMapper.delete(wrapper);


    }

    /**
     * 助学金分页
     *
     * @param page
     * @param size
     * @param status
     * @return
     */
    @Override
    public Page<GrantType> list(int page, int size, Integer status) {
        //页码参数
        Page<GrantType> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<GrantType> wrapper = new LambdaQueryWrapper<>();
        //添加选项条件

        // status不为空时,按照状态进行展示
        if (status != null) {
            wrapper.eq(GrantType::getStatus, status);
        }
        //按照创建时间的降序进行排序
        wrapper.orderByDesc(GrantType::getCreateTime);
        Page<GrantType> result = grantTypeMapper.selectPage(pageParam, wrapper);

        return result;
    }

    /**
     * 统计助学金总数
     *
     * @return
     */
    @Override
    public Long countGrantType() {
        Long count = grantTypeMapper.selectCount(null);
        return count;
    }

    /**
     * 助学金详情
     *
     * @param id
     * @return
     */
    @Override
    public GrantType GrantTypeDetail(Long id) {
        GrantType grantType = grantTypeMapper.selectById(id);
        return grantType;
    }

}
