package com.coder.drop_risk.repository;

import com.coder.drop_risk.model.Intervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterventionRepository extends JpaRepository<Intervention, Long> {

    // Custom query method to find all interventions for a specific student
    List<Intervention> findByStudentId(String studentId);
}