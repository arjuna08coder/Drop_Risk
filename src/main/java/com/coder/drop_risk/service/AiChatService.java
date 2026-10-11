package com.coder.drop_risk.service;

import com.coder.drop_risk.dto.ChatRequest;
import com.coder.drop_risk.model.Student;
import com.coder.drop_risk.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AiChatService {
    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);
    private static final String SYSTEM_PROMPT = "You are EduGuard, a student-support assistant. Answer general questions directly. When student data is supplied, use its current values and tailor your response to the question. Use recent turns for follow-ups and do not repeat earlier wording. For student support, offer no more than two practical steps. Separate recorded facts from suggestions; never infer causes, holds, diagnoses, or consequences absent from the data. Use plain everyday English, no Markdown, headings, or asterisks. Keep replies to 1-3 short sentences, under 60 words unless asked for more. Never invent student facts, scores, or names.";
    private final GeminiTextService gemini;
    private final StudentRepository studentRepository;

    public AiChatService(GeminiTextService gemini, StudentRepository studentRepository) {
        this.gemini = gemini;
        this.studentRepository = studentRepository;
    }

    public String chat(String userMessage, String studentId, List<ChatRequest.ChatTurn> history) {
        Student student = studentId == null || studentId.isBlank()
                ? null : studentRepository.findById(studentId).orElse(null);
        if (asksForHighestRisk(userMessage)) return highestRisk();
        if (student == null && asksForHighRiskRoster(userMessage)) return highRiskRoster();

        List<GeminiTextService.Turn> turns = new ArrayList<>();
        if (history != null) {
            history.stream().skip(Math.max(0, history.size() - 8L)).forEach(turn -> {
                if (turn == null || turn.getRole() == null || turn.getContent() == null) return;
                String role = turn.getRole();
                if (!role.equals("user") && !role.equals("assistant")) return;
                String content = turn.getContent().trim();
                if (content.length() > 1000) content = content.substring(0, 1000);
                if (!content.isEmpty()) turns.add(new GeminiTextService.Turn(role.equals("assistant") ? "model" : "user", content));
            });
        }
        String question = userMessage;
        if (student != null) {
            question = String.format("Student record: ID %s, name %s, attendance %.1f%%, previous attendance %.1f%%, average marks %.1f, backlogs %s, fee status %s, engagement %s.\nQuestion: %s",
                    student.getStudentId(), student.getName(), student.getAttendancePercentage(),
                    student.getPastAttendancePercentage(), student.getAvgMarks(), student.getBacklogStatus(),
                    student.getFeePaymentStatus(), student.getEngagementScore(), userMessage);
        }
        turns.add(new GeminiTextService.Turn("user", question));
        try {
            return gemini.generate(SYSTEM_PROMPT, turns);
        } catch (Exception exception) {
            log.warn("Gemini chat unavailable; returning local guidance: {}", exception.toString());
            return offlineGuidance(student);
        }
    }

    private boolean asksForHighestRisk(String message) {
        String text = message == null ? "" : message.toLowerCase();
        boolean asksWho = text.contains("who") || text.contains("which") || text.contains("student") || text.contains("highest") || text.contains("top");
        boolean asksRisk = text.contains("risk") || text.contains("dropout") || text.contains("dropping") || text.contains("leave school");
        return asksWho && asksRisk && (text.contains("highest") || text.contains("top"));
    }

    private boolean asksForHighRiskRoster(String message) {
        String text = message == null ? "" : message.toLowerCase();
        return (text.contains("high risk") || text.contains("high-risk")) &&
                (text.contains("who") || text.contains("which") || text.contains("list") || text.contains("show") || text.contains("how many"));
    }

    private String highestRisk() {
        List<StudentScore> scores = studentRepository.findAll().stream()
                .map(student -> new StudentScore(student, riskScore(student)))
                .sorted(Comparator.comparingInt(StudentScore::score).reversed()).toList();
        if (scores.isEmpty()) return "There are no student records to assess yet.";
        int max = scores.get(0).score();
        List<StudentScore> highest = scores.stream().takeWhile(row -> row.score() == max).toList();
        StringBuilder answer = new StringBuilder("The highest dashboard risk score is ").append(max)
                .append("/100, shared by ").append(highest.size()).append(" students:\n");
        for (StudentScore row : highest) {
            answer.append("- ").append(row.student().getStudentId());
            if (row.student().getName() != null && !row.student().getName().isBlank()) answer.append(" - ").append(row.student().getName());
            answer.append('\n');
        }
        return answer.toString().trim();
    }

    private String highRiskRoster() {
        List<Student> all = studentRepository.findAll();
        List<StudentScore> highRisk = all.stream().map(student -> new StudentScore(student, riskScore(student)))
                .filter(row -> row.score() >= 60).sorted(Comparator.comparingInt(StudentScore::score).reversed()).toList();
        StringBuilder answer = new StringBuilder("High risk means a dashboard score of 60/100 or above. ")
                .append(highRisk.size()).append(" of ").append(all.size()).append(" students meet that rule:");
        for (StudentScore row : highRisk) {
            answer.append("\n- ").append(row.student().getStudentId());
            if (row.student().getName() != null && !row.student().getName().isBlank()) answer.append(" - ").append(row.student().getName());
            answer.append(" - ").append(row.score()).append("/100");
        }
        return answer.toString();
    }

    private int riskScore(Student student) {
        int score = 0;
        if (student.getAttendancePercentage() < 60) score += 40;
        else if (student.getAttendancePercentage() < 75) score += 25;
        if (student.getAvgMarks() < 50) score += 30;
        else if (student.getAvgMarks() < 65) score += 15;
        if (student.getBacklogStatus() != null && !student.getBacklogStatus().equalsIgnoreCase("None")) score += 10;
        if (student.getFeePaymentStatus() != null && student.getFeePaymentStatus().toLowerCase().contains("overdue")) score += 10;
        if (student.getEngagementScore() != null && student.getEngagementScore() < 40) score += 20;
        return Math.min(score, 100);
    }

    private String offlineGuidance(Student student) {
        if (student == null) return "The AI assistant is temporarily unavailable. Please try again shortly.";
        String name = student.getName() == null || student.getName().isBlank() ? student.getStudentId() : student.getName();
        return String.format("The AI assistant is unavailable. Saved data shows %s has %.1f%% attendance and average marks of %.1f. Review these trends with the student and agree on one useful support step.",
                name, student.getAttendancePercentage(), student.getAvgMarks());
    }
    private record StudentScore(Student student, int score) {}
}
