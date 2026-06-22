package com.grade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("enrollment")
public class Enrollment {
    @TableId(type = IdType.AUTO)
    private Long enrollId;
    private Long studentId;
    private Long offerId;
    private Float regularScore;
    private Float examScore;
    private Float totalScore;
    private Float gradePoint;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}