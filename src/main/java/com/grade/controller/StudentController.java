package com.grade.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.grade.entity.Course;
import com.grade.entity.CourseOffer;
import com.grade.entity.Teacher;
import com.grade.mapper.CourseMapper;
import com.grade.mapper.CourseOfferMapper;
import com.grade.mapper.EnrollmentMapper;
import com.grade.mapper.TeacherMapper;
import com.grade.service.EnrollmentService;
import com.grade.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private CourseOfferMapper courseOfferMapper;
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private EnrollmentMapper enrollmentMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private TeacherMapper teacherMapper;

    @GetMapping("/courses")
    public Result listCourses(@RequestParam String semester) {
        List<CourseOffer> list = courseOfferMapper.selectList(
                new LambdaQueryWrapper<CourseOffer>()
                        .eq(CourseOffer::getSemester, semester)
                        .eq(CourseOffer::getStatus, 1));
        for (CourseOffer offer : list) {
            Course course = courseMapper.selectById(offer.getCourseId());
            if (course != null) {
                offer.setCourseName(course.getCourseName());
                offer.setCredit(course.getCredit());
            }
            Teacher teacher = teacherMapper.selectById(offer.getTeacherId());
            if (teacher != null) offer.setTeacherName(teacher.getName());
        }
        return Result.success(list);
    }

    @PostMapping("/select")
    public Result selectCourse(@RequestParam Long studentId, @RequestParam Long offerId) {
        try {
            enrollmentService.selectCourse(studentId, offerId);
            return Result.success("选课成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/drop")
    public Result dropCourse(@RequestParam Long studentId, @RequestParam Long offerId) {
        try {
            enrollmentService.dropCourse(studentId, offerId);
            return Result.success("退课成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/grades")
    public Result getGrades(@RequestParam Long studentId) {
        List<Map<String, Object>> list = enrollmentMapper.selectStudentGradeList(studentId);
        return Result.success(list);
    }
}