package com.grade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.grade.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}