package com.coder.drop_risk.controller;

import com.coder.drop_risk.dto.RiskPrediction;
import com.coder.drop_risk.model.Student;
import com.coder.drop_risk.repository.StudentRepository;
import com.coder.drop_risk.service.DropoutAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentApiController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DropoutAiService aiService;

    // 1. Get all students
    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // 2. Get AI Risk Assessment for a specific student ID
    @GetMapping("/{id}/ai-risk-assessment")
    public ResponseEntity<RiskPrediction> getAiRiskAssessment(@PathVariable String id) {
        return studentRepository.findById(id)
                .map(student -> ResponseEntity.ok(aiService.predictDropoutRisk(student)))
                .orElse(ResponseEntity.notFound().build());
    }
}