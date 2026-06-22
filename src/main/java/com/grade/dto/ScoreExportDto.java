package com.grade.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ScoreExportDto {
    @ExcelProperty("学号")
    private String studentNo;
    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("班级")
    private String className;
    @ExcelProperty("平时成绩")
    private Float regularScore;
    @ExcelProperty("期末成绩")
    private Float examScore;
    @ExcelProperty("总评成绩")
    private Float totalScore;
    @ExcelProperty("绩点")
    private Float gradePoint;
}