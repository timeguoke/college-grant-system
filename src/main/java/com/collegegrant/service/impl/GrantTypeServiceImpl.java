package com.collegegrant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.vo.GrantTypePageVO;
import com.collegegrant.pojo.vo.GrantTypeWithLevelVO;
import com.collegegrant.pojo.dto.GrantTypeDTO;
import com.collegegrant.pojo.entity.GrantLevel;
import com.collegegrant.pojo.entity.GrantType;
import com.collegegrant.mapper.GrantLevelMapper;
import com.collegegrant.mapper.GrantTypeMapper;
import com.collegegrant.service.GrantTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GrantTypeServiceImpl implements GrantTypeService {

    @Autowired
    private GrantTypeMapper grantTypeMapper;
    @Autowired
    private GrantLevelMapper grantLevelMapper;

    /**
     * 添加助学金（同时保存等级列表）
     *
     * @param dto
     */
    @Override
    @Transactional
    public void insertWithLevels(GrantTypeDTO dto) {
        // 参数非空校验
        if (dto.getTypeName() == null || dto.getTypeName().trim().isEmpty()) {
            throw new RuntimeException("助学金名称不能为空");
        }
        String typeName = dto.getTypeName().trim();

        // 判断该助学金是否已经存在
        LambdaQueryWrapper<GrantType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GrantType::getTypeName, typeName);
        if (grantTypeMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("助学金已存在：" + typeName);
        }

        // 保存类型，主键回填
        GrantType grantType = GrantType.builder()
                .typeName(typeName)
                .description(dto.getDescription())
                // 前端不传状态时默认可申请
                .status(dto.getStatus() == null ? 1 : dto.getStatus())
                .build();
        grantTypeMapper.insert(grantType);

        // 保存等级列表
        saveNewLevels(grantType.getId(), dto.getLevels());
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
     * 修改助学金信息（同时维护等级列表：有id更新，无id新增，缺失的删除）
     *
     * @param dto
     */
    @Override
    @Transactional
    public void updateWithLevels(GrantTypeDTO dto) {
        if (dto.getId() == null) {
            throw new RuntimeException("助学金类型ID不能为空");
        }
        GrantType exist = grantTypeMapper.selectById(dto.getId());
        if (exist == null) {
            throw new RuntimeException("助学金类型不存在");
        }

        // 更新类型基本信息（null字段不参与更新，保留原值）
        GrantType grantType = new GrantType();
        grantType.setId(dto.getId());
        if (dto.getTypeName() != null && !dto.getTypeName().trim().isEmpty()) {
            grantType.setTypeName(dto.getTypeName().trim());
        }
        grantType.setDescription(dto.getDescription());
        grantType.setStatus(dto.getStatus());
        grantTypeMapper.updateById(grantType);

        // 维护等级列表：levels为null表示本次不处理等级（如启用/禁用），非null（含空列表）时全量同步
        if (dto.getLevels() == null) {
            return;
        }
        List<GrantLevel> submitLevels = dto.getLevels();
        Set<Long> submitIds = new HashSet<>();
        int sort = 0;
        for (GrantLevel level : submitLevels) {
            if (level.getLevelName() == null || level.getLevelName().trim().isEmpty()) {
                throw new RuntimeException("等级名称不能为空");
            }
            level.setTypeId(dto.getId());
            sort++;
            if (level.getSort() == null) {
                level.setSort(sort);
            }
            if (level.getId() == null) {
                // 新增的等级，insert后主键回填，需加入保留集合，避免被后面的删除逻辑误删
                level.setId(null);
                grantLevelMapper.insert(level);
                submitIds.add(level.getId());
            } else {
                // 已存在的等级，校验归属后更新
                GrantLevel dbLevel = grantLevelMapper.selectById(level.getId());
                if (dbLevel == null || !dto.getId().equals(dbLevel.getTypeId())) {
                    throw new RuntimeException("等级数据不合法");
                }
                grantLevelMapper.updateById(level);
                submitIds.add(level.getId());
            }
        }

        // 删除本次提交中已不存在的旧等级
        LambdaQueryWrapper<GrantLevel> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(GrantLevel::getTypeId, dto.getId());
        if (!submitIds.isEmpty()) {
            deleteWrapper.notIn(GrantLevel::getId, submitIds);
        }
        grantLevelMapper.delete(deleteWrapper);
    }

    /**
     * 删除助学金（同时删除其下等级）
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
     * 助学金分页（支持名称模糊查询，含等级数量）
     *
     * @param page
     * @param size
     * @param status
     * @param name
     * @return
     */
    @Override
    public Page<GrantTypePageVO> listPage(int page, int size, Integer status, String name) {
        // 分页查询类型
        Page<GrantType> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<GrantType> wrapper = new LambdaQueryWrapper<>();
        // status不为空时,按照状态进行展示
        if (status != null) {
            wrapper.eq(GrantType::getStatus, status);
        }
        // 名称模糊查询
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(GrantType::getTypeName, name.trim());
        }
        //按照创建时间的降序进行排序
        wrapper.orderByDesc(GrantType::getCreateTime);
        Page<GrantType> result = grantTypeMapper.selectPage(pageParam, wrapper);

        // 批量统计每个类型的等级数量，避免N+1查询
        List<Long> typeIds = result.getRecords().stream()
                .map(GrantType::getId)
                .collect(Collectors.toList());
        Map<Long, Long> tempCountMap = Collections.emptyMap();
        if (!typeIds.isEmpty()) {
            LambdaQueryWrapper<GrantLevel> levelWrapper = new LambdaQueryWrapper<>();
            levelWrapper.in(GrantLevel::getTypeId, typeIds);
            tempCountMap = grantLevelMapper.selectList(levelWrapper).stream()
                    .collect(Collectors.groupingBy(GrantLevel::getTypeId, Collectors.counting()));
        }
        final Map<Long, Long> countMap = tempCountMap;

        // 组装VO
        List<GrantTypePageVO> voList = result.getRecords().stream().map(type -> {
            GrantTypePageVO vo = new GrantTypePageVO();
            vo.setId(type.getId());
            vo.setTypeName(type.getTypeName());
            vo.setStatus(type.getStatus());
            vo.setDescription(type.getDescription());
            vo.setCreateTime(type.getCreateTime());
            vo.setLevelCount(countMap.getOrDefault(type.getId(), 0L).intValue());
            return vo;
        }).collect(Collectors.toList());

        Page<GrantTypePageVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 助学金全量列表（下拉选择用，按创建时间降序）
     *
     * @param status 状态过滤，null表示全部
     * @return 类型列表
     */
    @Override
    public List<GrantType> listAll(Integer status) {
        LambdaQueryWrapper<GrantType> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(GrantType::getStatus, status);
        }
        wrapper.orderByDesc(GrantType::getCreateTime);
        return grantTypeMapper.selectList(wrapper);
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
     * 助学金类型详情（带等级列表，按sort升序）
     *
     * @param id
     * @return
     */
    @Override
    public GrantTypeWithLevelVO detailWithLevels(Long id) {
        GrantType type = grantTypeMapper.selectById(id);
        if (type == null) {
            throw new RuntimeException("助学金类型不存在");
        }

        LambdaQueryWrapper<GrantLevel> levelWrapper = new LambdaQueryWrapper<>();
        levelWrapper.eq(GrantLevel::getTypeId, id)
                    .orderByAsc(GrantLevel::getSort);
        List<GrantLevel> levels = grantLevelMapper.selectList(levelWrapper);

        GrantTypeWithLevelVO vo = new GrantTypeWithLevelVO();
        vo.setId(type.getId());
        vo.setTypeName(type.getTypeName());
        vo.setDescription(type.getDescription());
        vo.setLevels(levels.stream().map(level -> {
            GrantTypeWithLevelVO.GrantLevelVO levelVO = new GrantTypeWithLevelVO.GrantLevelVO();
            levelVO.setId(level.getId());
            levelVO.setLevelName(level.getLevelName());
            levelVO.setAmount(level.getAmount());
            levelVO.setMinScore(level.getMinScore());
            levelVO.setSort(level.getSort());
            return levelVO;
        }).collect(Collectors.toList()));
        return vo;
    }

    /**
     * 新增类型时批量保存全新等级
     */
    private void saveNewLevels(Long typeId, List<GrantLevel> levels) {
        if (levels == null || levels.isEmpty()) {
            return;
        }
        int sort = 0;
        for (GrantLevel level : levels) {
            if (level.getLevelName() == null || level.getLevelName().trim().isEmpty()) {
                throw new RuntimeException("等级名称不能为空");
            }
            level.setId(null);
            level.setTypeId(typeId);
            sort++;
            if (level.getSort() == null) {
                level.setSort(sort);
            }
            grantLevelMapper.insert(level);
        }
    }
}
