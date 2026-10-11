package com.coder.drop_risk.controller;

import com.coder.drop_risk.model.Student;
import com.coder.drop_risk.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardViewController {
    private final StudentRepository students;

    public DashboardViewController(StudentRepository students) { this.students = students; }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        List<Map<String, Object>> rows = students.findAll().stream().map(this::dashboardRow)
                .sorted(Comparator.comparingInt(row -> -(Integer) row.get("riskPercent"))).toList();
        model.addAttribute("students", rows);
        return "Dashboard";
    }

    private Map<String, Object> dashboardRow(Student student) {
        int score = 0;
        List<String> factors = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        if (student.getAttendancePercentage() < 60) { score += 40; factors.add("Attendance below 60%"); recommendations.add("Create an attendance support plan"); }
        else if (student.getAttendancePercentage() < 75) { score += 25; factors.add("Attendance below 75%"); recommendations.add("Create an attendance support plan"); }
        if (student.getAvgMarks() < 50) { score += 30; factors.add("Average marks below 50%"); recommendations.add("Offer remedial classes"); }
        else if (student.getAvgMarks() < 65) { score += 15; factors.add("Academic performance needs support"); recommendations.add("Schedule an academic check-in"); }
        if (student.getEngagementScore() != null && student.getEngagementScore() < 40) { score += 20; factors.add("Low engagement score"); recommendations.add("Refer for student support"); }
        if (student.getBacklogStatus() != null && !student.getBacklogStatus().equalsIgnoreCase("None")) { score += 10; factors.add("Outstanding backlogs"); recommendations.add("Review backlog support options"); }
        if (student.getFeePaymentStatus() != null && student.getFeePaymentStatus().toLowerCase().contains("overdue")) { score += 10; factors.add("Fees need attention"); recommendations.add("Review fee assistance options"); }
        score = Math.min(score, 100);
        if (factors.isEmpty()) factors.add("No immediate risk factor identified");
        if (recommendations.isEmpty()) recommendations.add("Continue regular monitoring");
        String tier = score >= 60 ? "High" : score >= 30 ? "Medium" : "Low";
        return Map.of("id", student.getStudentId(), "studentId", student.getStudentId(), "term", safe(student.getTerm()),
                "className", "Class " + student.getClassField(), "attendance", student.getAttendancePercentage(),
                "marks", student.getAvgMarks(), "tier", tier, "riskPercent", score,
                "factors", factors, "recommendations", recommendations);
    }
    private static String safe(String value) { return value == null ? "" : value; }
}


