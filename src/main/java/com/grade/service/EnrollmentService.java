package com.grade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.grade.entity.CourseOffer;
import com.grade.entity.Enrollment;
import com.grade.mapper.CourseOfferMapper;
import com.grade.mapper.EnrollmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentService {
    @Autowired
    private EnrollmentMapper enrollmentMapper;
    @Autowired
    private CourseOfferMapper courseOfferMapper;

    /**
     * 学生选课（并发安全版本）
     */
    @Transactional
    public boolean selectCourse(Long studentId, Long offerId) {
        // 1. 前置校验：是否已选课
        LambdaQueryWrapper<Enrollment> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(Enrollment::getStudentId, studentId)
                .eq(Enrollment::getOfferId, offerId);
        if (enrollmentMapper.selectCount(checkWrapper) > 0) {
            throw new RuntimeException("您已选过此课程");
        }

        // 2. 原子更新容量：只有已选人数 < 总容量时，才执行+1
        LambdaUpdateWrapper<CourseOffer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CourseOffer::getOfferId, offerId)
                // 两个字段对比用 apply 手写 SQL，避免类型错误
                .apply("enrolled_count < capacity")
                .setSql("enrolled_count = enrolled_count + 1");

        int affectedRows = courseOfferMapper.update(null, updateWrapper);
        if (affectedRows == 0) {
            throw new RuntimeException("选课人数已满");
        }

        // 3. 插入选课记录（数据库唯一索引兜底重复选课）
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setOfferId(offerId);
        enrollment.setStatus(1);
        enrollmentMapper.insert(enrollment);

        return true;
    }

    /**
     * 学生退课（并发安全版本）
     */
    @Transactional
    public boolean dropCourse(Long studentId, Long offerId) {
        // 1. 校验选课记录是否存在且有效
        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enrollment::getStudentId, studentId)
                .eq(Enrollment::getOfferId, offerId);
        Enrollment enrollment = enrollmentMapper.selectOne(wrapper);
        if (enrollment == null) {
            throw new RuntimeException("未找到选课记录");
        }
        if (enrollment.getStatus() != 1) {
            throw new RuntimeException("该课程已退课或状态异常");
        }

        // 2. 更新选课状态为已退课
        enrollment.setStatus(2);
        enrollmentMapper.updateById(enrollment);

        // 3. 原子扣减已选人数，避免负数
        LambdaUpdateWrapper<CourseOffer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CourseOffer::getOfferId, offerId)
                .gt(CourseOffer::getEnrolledCount, 0)
                .setSql("enrolled_count = enrolled_count - 1");
        courseOfferMapper.update(null, updateWrapper);

        return true;
    }
}