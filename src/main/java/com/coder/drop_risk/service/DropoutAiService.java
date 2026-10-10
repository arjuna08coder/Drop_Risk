package com.coder.drop_risk.service;

import com.coder.drop_risk.dto.RiskPrediction;
import com.coder.drop_risk.model.Student;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DropoutAiService {

    private final ChatClient chatClient;

    // Spring AI automatically injects the ChatClient builder configured with your API key
    public DropoutAiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public RiskPrediction predictDropoutRisk(Student student) {
        double score = calculateBaseScore(student);
        String riskLevel = determineRiskLevel(score);

        // Construct a prompt for the LLM to get intelligent, contextual action items
        String prompt = String.format(
                "Analyze this student for dropout risk. " +
                        "Name: %s, Attendance: %.1f%% (Past: %.1f%%), Avg Marks: %.1f/100, Backlogs: %s, Fee Status: %s. " +
                        "Provide 2-3 specific, actionable intervention steps a counselor should take.",
                student.getName(), student.getAttendancePercentage(), student.getPastAttendancePercentage(),
                student.getAvgMarks(), student.getBacklogStatus(), student.getFeePaymentStatus()
        );

        // Call the LLM using Spring AI
        String aiResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        List<String> factors = new ArrayList<>();
        factors.add("Rule-based score calculated: " + score + "/100 (" + riskLevel + ")");

        List<String> actions = new ArrayList<>();
        actions.add(aiResponse); // AI-generated dynamic counselor recommendations

        return new RiskPrediction(score, riskLevel, factors, actions);
    }

    private double calculateBaseScore(Student student) {
        double score = 0.0;
        if (student.getAttendancePercentage() < 75.0) score += 40;
        else if (student.getAttendancePercentage() < 85.0) score += 20;

        if (student.getAvgMarks() < 40.0) score += 35;
        else if (student.getAvgMarks() < 60.0) score += 15;

        if (student.getBacklogStatus() != null && !student.getBacklogStatus().equalsIgnoreCase("None")) score += 10;
        return score;
    }

    private String determineRiskLevel(double score) {
        if (score >= 60.0) return "HIGH";
        if (score >= 30.0) return "MODERATE";
        return "LOW";
    }
}