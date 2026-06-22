package com.grade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("course_offer")
public class CourseOffer {
    @TableId(type = IdType.AUTO)
    private Long offerId;
    private Long courseId;
    private Long teacherId;
    private String semester;
    private String schedule;
    private String location;
    private Integer capacity;
    private Integer enrolledCount;
    private Integer status;

    @TableField(exist = false)
    private String courseName;
    @TableField(exist = false)
    private String teacherName;
    @TableField(exist = false)
    private Float credit;
}