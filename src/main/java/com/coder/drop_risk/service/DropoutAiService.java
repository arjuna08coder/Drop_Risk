package com.coder.drop_risk.service;

import com.coder.drop_risk.dto.RiskPrediction;
import com.coder.drop_risk.model.Student;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DropoutAiService {

    public RiskPrediction predictDropoutRisk(Student student) {
        double score = 0.0;
        List<String> factors = new ArrayList<>();
        List<String> actions = new ArrayList<>();

        // 1. Attendance Signal (Weight: 40%)
        if (student.getAttendancePercentage() < 75.0) {
            score += 40;
            factors.add("Critical attendance drop (" + student.getAttendancePercentage() + "%) below required 75% threshold.");
            actions.add("Schedule mandatory academic counseling session.");
        } else if (student.getAttendancePercentage() < 85.0) {
            score += 20;
            factors.add("Moderate attendance decline (" + student.getAttendancePercentage() + "%).");
            actions.add("Send automated attendance notification to student.");
        }

        // 2. Attendance Trend Signal (Weight: 15%)
        if (student.getAttendancePercentage() < student.getPastAttendancePercentage() - 10.0) {
            score += 15;
            factors.add("Recent attendance dropped by more than 10% compared to previous term.");
        }

        // 3. Academic Performance Signal (Weight: 35%)
        if (student.getAvgMarks() < 40.0) {
            score += 35;
            factors.add("Low overall average marks (" + student.getAvgMarks() + "/100).");
            actions.add("Assign peer tutor for academic support.");
        } else if (student.getAvgMarks() < 60.0) {
            score += 15;
            factors.add("Sub-optimal academic performance (" + student.getAvgMarks() + "/100).");
        }

        // 4. Backlog Status Signal (Weight: 10%)
        if (student.getBacklogStatus() != null && !student.getBacklogStatus().equalsIgnoreCase("None")) {
            score += 10;
            factors.add("Active academic backlogs: " + student.getBacklogStatus());
            actions.add("Review backlog clearance roadmap with faculty advisor.");
        }

        // Determine Risk Level
        String riskLevel;
        if (score >= 60.0) {
            riskLevel = "HIGH";
        } else if (score >= 30.0) {
            riskLevel = "MODERATE";
        } else {
            riskLevel = "LOW";
            factors.add("Student is performing well within normal thresholds.");
            actions.add("No immediate intervention needed.");
        }

        return new RiskPrediction(score, riskLevel, factors, actions);
    }
}