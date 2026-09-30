package com.collegegrant.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.entity.GrantType;
import com.collegegrant.mapper.GrantTypeMapper;
import com.collegegrant.service.admin.GrantTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GrantTypeServiceImpl implements GrantTypeService {

    @Autowired
    private GrantTypeMapper grantTypeMapper;

    /**
     * 添加助学金
     *
     * @param grantType
     */
    @Override
    @Transactional
    public void insert(GrantType grantType) {
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
     * 查询助学金详情
     *
     * @return
     */
    @Override
    public List<GrantType> selectAvailableList() {
        LambdaQueryWrapper<GrantType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GrantType::getStatus, 1);

        List<GrantType> grantTypes = grantTypeMapper.selectList(wrapper);

        return grantTypes;
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
    public void deleteById(Long id) {
        grantTypeMapper.deleteById(id);

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

}
