package com.coder.drop_risk.model;

import jakarta.persistence.*;

@Entity
@Table(name = "intervention")
public class Intervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "intervention_id")
    private Long interventionId;

    @Column(name = "student_id", nullable = false, length = 30)
    private String studentId;

    @Column(name = "intervention_type", nullable = false)
    private String interventionType;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private String status = "Pending";

    @Column(name = "counselor_name")
    private String counselorName;

    public Intervention() {}
    public Long getInterventionId() { return interventionId; }
    public void setInterventionId(Long interventionId) { this.interventionId = interventionId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getInterventionType() { return interventionType; }
    public void setInterventionType(String interventionType) { this.interventionType = interventionType; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCounselorName() { return counselorName; }
    public void setCounselorName(String counselorName) { this.counselorName = counselorName; }
}
