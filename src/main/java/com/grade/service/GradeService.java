package com.grade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.grade.entity.Enrollment;
import com.grade.mapper.EnrollmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class GradeService {
    @Autowired
    private EnrollmentMapper enrollmentMapper;

    public float calculateTotalScore(Float regular, Float exam) {
        float r = regular == null ? 0 : regular;
        float e = exam == null ? 0 : exam;
        return Math.round((r * 0.4f + e * 0.6f) * 10) / 10.0f;
    }

    public float calculateGradePoint(float total) {
        if (total >= 90) return 4.0f;
        if (total >= 80) return 3.0f + (total - 80) / 10;
        if (total >= 70) return 2.0f + (total - 70) / 10;
        if (total >= 60) return 1.0f + (total - 60) / 10;
        return 0.0f;
    }

    @Transactional
    public void batchSaveScores(List<Enrollment> list) {
        for (Enrollment e : list) {
            if (e.getEnrollId() == null) continue;
            float total = calculateTotalScore(e.getRegularScore(), e.getExamScore());
            e.setTotalScore(total);
            e.setGradePoint(calculateGradePoint(total));
            enrollmentMapper.updateById(e);
        }
    }

    public Map<String, Object> getClassStatistics(Long offerId) {
        List<Enrollment> list = enrollmentMapper.selectList(
                new LambdaQueryWrapper<Enrollment>()
                        .eq(Enrollment::getOfferId, offerId)
                        .eq(Enrollment::getStatus, 1)
        );
        Map<String, Object> stats = new HashMap<>();
        if (list.isEmpty()) {
            stats.put("avg", 0);
            stats.put("max", 0);
            stats.put("min", 0);
            stats.put("passRate", 0);
            stats.put("distribution", new int[]{0,0,0,0,0});
            return stats;
        }
        double sum = 0;
        double max = -1;
        double min = 101;
        int pass = 0;
        int[] dist = new int[5];
        for (Enrollment e : list) {
            float score = e.getTotalScore() != null ? e.getTotalScore() : 0;
            sum += score;
            if (score > max) max = score;
            if (score < min) min = score;
            if (score >= 60) pass++;
            if (score < 60) dist[0]++;
            else if (score < 70) dist[1]++;
            else if (score < 80) dist[2]++;
            else if (score < 90) dist[3]++;
            else dist[4]++;
        }
        stats.put("avg", Math.round(sum / list.size() * 10) / 10.0);
        stats.put("max", max);
        stats.put("min", min);
        stats.put("passRate", Math.round((pass * 100.0 / list.size()) * 10) / 10.0);
        stats.put("distribution", dist);
        return stats;
    }
}