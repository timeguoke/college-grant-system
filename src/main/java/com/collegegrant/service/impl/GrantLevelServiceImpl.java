package com.collegegrant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.collegegrant.pojo.entity.GrantLevel;
import com.collegegrant.pojo.entity.GrantType;
import com.collegegrant.mapper.GrantLevelMapper;
import com.collegegrant.mapper.GrantTypeMapper;
import com.collegegrant.service.GrantLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 助学金等级Service实现类
 */
@Service
public class GrantLevelServiceImpl implements GrantLevelService {

    @Autowired
    private GrantLevelMapper grantLevelMapper;
    @Autowired
    private GrantTypeMapper grantTypeMapper;

    /**
     * 根据助学金类型ID查询等级列表（按sort升序）
     */
    @Override
    public List<GrantLevel> listByTypeId(Long typeId) {
        LambdaQueryWrapper<GrantLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GrantLevel::getTypeId, typeId)
               .orderByAsc(GrantLevel::getSort);
        return grantLevelMapper.selectList(wrapper);
    }

    /**
     * 根据ID查询等级详情
     */
    @Override
    public GrantLevel getById(Long id) {
        return grantLevelMapper.selectById(id);
    }

    /**
     * 新增等级
     */
    @Override
    public void insert(Long typeId, GrantLevel grantLevel) {
        // ===== 新增：参数校验 =====
        // 1. 校验助学金类型是否存在
        GrantType grantType = grantTypeMapper.selectById(typeId);
        if (grantType == null) {
            throw new RuntimeException("助学金类型不存在");
        }
        // 2. 校验等级名称不能为空
        if (grantLevel.getLevelName() == null || grantLevel.getLevelName().trim().isEmpty()) {
            throw new RuntimeException("等级名称不能为空");
        }
        // 3. 校验金额不能小于0
        if (grantLevel.getAmount() == null || grantLevel.getAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new RuntimeException("金额不能小于0");
        }
        // 4. 校验分数线不能小于0
        if (grantLevel.getMinScore() == null || grantLevel.getMinScore().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new RuntimeException("分数线不能小于0");
        }
        // 5. 校验同一类型下等级名称不能重复
        LambdaQueryWrapper<GrantLevel> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(GrantLevel::getTypeId, typeId)
                   .eq(GrantLevel::getLevelName, grantLevel.getLevelName());
        GrantLevel existing = grantLevelMapper.selectOne(nameWrapper);
        if (existing != null) {
            throw new RuntimeException("该助学金下已存在相同名称的等级：" + grantLevel.getLevelName());
        }
        // 6. sort默认值处理
        if (grantLevel.getSort() == null) {
            grantLevel.setSort(1);
        }
        // ===== 新增结束 =====

        grantLevel.setTypeId(typeId);
        grantLevelMapper.insert(grantLevel);
    }

    /**
     * 修改等级
     */
    @Override
    public void update(GrantLevel grantLevel) {
        // ===== 新增：参数校验 =====
        // 1. 校验等级是否存在
        GrantLevel oldLevel = grantLevelMapper.selectById(grantLevel.getId());
        if (oldLevel == null) {
            throw new RuntimeException("等级不存在");
        }
        // 2. 如果改了等级名称，校验不能重复
        if (grantLevel.getLevelName() != null && !grantLevel.getLevelName().equals(oldLevel.getLevelName())) {
            LambdaQueryWrapper<GrantLevel> nameWrapper = new LambdaQueryWrapper<>();
            nameWrapper.eq(GrantLevel::getTypeId, oldLevel.getTypeId())
                       .eq(GrantLevel::getLevelName, grantLevel.getLevelName());
            GrantLevel existing = grantLevelMapper.selectOne(nameWrapper);
            if (existing != null) {
                throw new RuntimeException("该助学金下已存在相同名称的等级：" + grantLevel.getLevelName());
            }
        }
        // ===== 新增结束 =====

        grantLevelMapper.updateById(grantLevel);
    }

    /**
     * 删除等级
     */
    @Override
    public void deleteById(Long id) {
        // ===== 新增：参数校验 =====
        GrantLevel grantLevel = grantLevelMapper.selectById(id);
        if (grantLevel == null) {
            throw new RuntimeException("等级不存在");
        }
        // ===== 新增结束 =====

        grantLevelMapper.deleteById(id);
    }
}
