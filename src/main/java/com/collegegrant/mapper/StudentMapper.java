package com.collegegrant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.collegegrant.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}
