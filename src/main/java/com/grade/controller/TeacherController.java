package com.grade.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.grade.dto.ScoreExportDto;
import com.grade.entity.Course;
import com.grade.entity.CourseOffer;
import com.grade.entity.Enrollment;
import com.grade.entity.Teacher;
import com.grade.mapper.CourseMapper;
import com.grade.mapper.CourseOfferMapper;
import com.grade.mapper.EnrollmentMapper;
import com.grade.mapper.TeacherMapper;
import com.grade.service.GradeService;
import com.grade.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    @Autowired
    private CourseOfferMapper courseOfferMapper;
    @Autowired
    private EnrollmentMapper enrollmentMapper;
    @Autowired
    private GradeService gradeService;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private TeacherMapper teacherMapper;

    @GetMapping("/my-courses")
    public Result getMyCourses(@RequestParam Long teacherId, @RequestParam String semester) {
        List<CourseOffer> list = courseOfferMapper.selectList(
                new LambdaQueryWrapper<CourseOffer>()
                        .eq(CourseOffer::getTeacherId, teacherId)
                        .eq(CourseOffer::getSemester, semester));
        for (CourseOffer offer : list) {
            Course course = courseMapper.selectById(offer.getCourseId());
            if (course != null) offer.setCourseName(course.getCourseName());
            Teacher teacher = teacherMapper.selectById(offer.getTeacherId());
            if (teacher != null) offer.setTeacherName(teacher.getName());
        }
        return Result.success(list);
    }

    @GetMapping("/students")
    public Result getStudentsByCourse(@RequestParam Long offerId) {
        List<Map<String, Object>> list = enrollmentMapper.selectClassGradeList(offerId);
        return Result.success(list);
    }

    @PostMapping("/grades/batch")
    public Result batchSaveGrades(@RequestBody List<Enrollment> enrollments) {
        try {
            gradeService.batchSaveScores(enrollments);
            return Result.success("成绩保存成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public Result getStatistics(@RequestParam Long offerId) {
        return Result.success(gradeService.getClassStatistics(offerId));
    }

    @GetMapping("/export")
    public void exportScores(@RequestParam Long offerId, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = enrollmentMapper.selectClassGradeList(offerId);
        List<ScoreExportDto> exportList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            ScoreExportDto dto = new ScoreExportDto();
            dto.setStudentNo((String) map.get("student_no"));
            dto.setName((String) map.get("name"));
            dto.setClassName((String) map.get("class_name"));
            dto.setRegularScore((Float) map.get("regular_score"));
            dto.setExamScore((Float) map.get("exam_score"));
            dto.setTotalScore((Float) map.get("total_score"));
            dto.setGradePoint((Float) map.get("grade_point"));
            exportList.add(dto);
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=scores.xlsx");
        EasyExcel.write(response.getOutputStream(), ScoreExportDto.class).sheet("成绩表").doWrite(exportList);
    }

    @GetMapping("/courseInfo")
    public Result getCourseInfo(@RequestParam Long offerId) {
        CourseOffer offer = courseOfferMapper.selectById(offerId);
        if (offer == null) return Result.error("开课不存在");
        Course course = courseMapper.selectById(offer.getCourseId());
        Teacher teacher = teacherMapper.selectById(offer.getTeacherId());
        Map<String, Object> info = new HashMap<>();
        info.put("courseName", course != null ? course.getCourseName() : "未知");
        info.put("teacherName", teacher != null ? teacher.getName() : "未知");
        info.put("semester", offer.getSemester());
        return Result.success(info);
    }
}