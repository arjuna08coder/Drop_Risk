package com.coder.drop_risk.service;

import com.coder.drop_risk.dto.RiskPrediction;
import com.coder.drop_risk.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DropoutAiService {
    private static final Logger log = LoggerFactory.getLogger(DropoutAiService.class);
    private final GeminiTextService gemini;

    public DropoutAiService(GeminiTextService gemini) { this.gemini = gemini; }

    public RiskPrediction predictDropoutRisk(Student student) {
        double score = calculateBaseScore(student);
        String riskLevel = score >= 60 ? "HIGH" : score >= 30 ? "MODERATE" : "LOW";
        List<String> factors = new ArrayList<>();
        factors.add("Rule-based score calculated: " + score + "/100 (" + riskLevel + ")");
        String prompt = String.format("Student: %s. Attendance: %.1f%% (previously %.1f%%). Average marks: %.1f/100. Backlogs: %s. Fee status: %s. Give two short, practical support actions. Do not diagnose or assume causes.",
                student.getName(), student.getAttendancePercentage(), student.getPastAttendancePercentage(),
                student.getAvgMarks(), student.getBacklogStatus(), student.getFeePaymentStatus());
        String advice;
        try {
            advice = gemini.generate("You are a student-support assistant. Use respectful, plain language. Be specific to the supplied facts and stay under 45 words.",
                    List.of(new GeminiTextService.Turn("user", prompt)));
        } catch (Exception exception) {
            log.warn("Gemini risk advice unavailable: {}", exception.toString());
            advice = "Review attendance and recent marks with the student, ask what support would help, and agree on one follow-up action.";
        }
        return new RiskPrediction(score, riskLevel, factors, List.of(advice));
    }

    private double calculateBaseScore(Student student) {
        double score = 0;
        if (student.getAttendancePercentage() < 75) score += 40;
        else if (student.getAttendancePercentage() < 85) score += 20;
        if (student.getAvgMarks() < 40) score += 35;
        else if (student.getAvgMarks() < 60) score += 15;
        if (student.getBacklogStatus() != null && !student.getBacklogStatus().equalsIgnoreCase("None")) score += 10;
        return score;
    }
}
