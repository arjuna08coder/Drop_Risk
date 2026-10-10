package com.coder.drop_risk.controller;

import com.coder.drop_risk.model.Intervention;
import com.coder.drop_risk.repository.InterventionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interventions")
public class InterventionApiController {

    @Autowired
    private InterventionRepository interventionRepository;

    // 1. Get all logged interventions
    @GetMapping
    public List<Intervention> getAllInterventions() {
        return interventionRepository.findAll();
    }

    // 2. Get all interventions for a specific student
    @GetMapping("/student/{studentId}")
    public List<Intervention> getInterventionsByStudent(@PathVariable String studentId) {
        return interventionRepository.findByStudentId(studentId);
    }

    // 3. Log a new intervention action (Counselor action)
    @PostMapping
    public Intervention createIntervention(@RequestBody Intervention intervention) {
        return interventionRepository.save(intervention);
    }

    // 4. Update intervention status (e.g., Pending -> Completed)
    @PutMapping("/{id}")
    public ResponseEntity<Intervention> updateInterventionStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return interventionRepository.findById(id)
                .map(intervention -> {
                    intervention.setStatus(status);
                    Intervention updated = interventionRepository.save(intervention);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}