package com.coder.drop_risk.controller;

import com.coder.drop_risk.dto.RiskPrediction;
import com.coder.drop_risk.model.Student;
import com.coder.drop_risk.repository.StudentRepository;
import com.coder.drop_risk.service.DropoutAiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentApiController {
    private final StudentRepository studentRepository;
    private final DropoutAiService aiService;

    public StudentApiController(StudentRepository studentRepository, DropoutAiService aiService) {
        this.studentRepository = studentRepository;
        this.aiService = aiService;
    }

    @GetMapping
    public List<Student> getAllStudents() { return studentRepository.findAll(); }

    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody StudentRequest request) {
        Student student = new Student();
        student.setStudentId(request.studentCode);
        student.setName(request.studentCode);
        student.setClassField(parseClass(request.className));
        student.setTerm(request.term);
        student.setAttendancePercentage((float) request.attendance);
        student.setPastAttendancePercentage((float) request.attendance);
        student.setAvgMarks((float) request.averageMarks);
        student.setFeePaymentStatus(request.feeStatus);
        student.setEngagementScore((float) request.engagementScore);
        student.setBacklogStatus("None");
        studentRepository.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Student added successfully", "studentId", student.getStudentId(), "studentCode", student.getStudentId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeStudent(@PathVariable String id) {
        if (!studentRepository.existsById(id)) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Student not found"));
        studentRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Student removed successfully"));
    }

    @GetMapping("/{id}/ai-risk-assessment")
    public ResponseEntity<RiskPrediction> getAiRiskAssessment(@PathVariable String id) {
        return studentRepository.findById(id).map(student -> ResponseEntity.ok(aiService.predictDropoutRisk(student))).orElse(ResponseEntity.notFound().build());
    }

    private int parseClass(String className) {
        if (className == null) return 0;
        String digits = className.replaceAll("[^0-9]", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }

    public static class StudentRequest {
        public String studentCode;
        public String className;
        public String term;
        public double attendance;
        public double averageMarks;
        public String feeStatus;
        public double engagementScore;
    }
}
