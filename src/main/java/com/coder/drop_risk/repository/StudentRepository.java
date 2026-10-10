package com.coder.drop_risk.repository;

import com.coder.drop_risk.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    // Built-in CRUD methods (save, findById, findAll, delete) are automatically available.
    // You can also add custom query methods here later if needed, for example:
    // List<Student> findByClassField(int classField);
}