package com.grade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.grade.entity.Enrollment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface EnrollmentMapper extends BaseMapper<Enrollment> {
    List<Map<String, Object>> selectStudentGradeList(Long studentId);
    List<Map<String, Object>> selectClassGradeList(Long offerId);
}