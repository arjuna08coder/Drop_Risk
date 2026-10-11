package com.coder.drop_risk.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "student")
public class Student {
    @Id
    @Column(name = "student_id", length = 30)
    private String studentId;
    private String name;
    @Column(name = "class")
    private int classField;
    private String term;
    @Column(name = "attendance_percentage")
    private float attendancePercentage;
    @Column(name = "past_attendance_percentage")
    private float pastAttendancePercentage;
    private String grade;
    @Column(name = "avg_marks")
    private float avgMarks;
    @Column(name = "backlog_status")
    private String backlogStatus;
    @Column(name = "fee_payment_status")
    private String feePaymentStatus;
    @Column(name = "engagement_score")
    private Float engagementScore;

    public Student() {}
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getClassField() { return classField; }
    public void setClassField(int classField) { this.classField = classField; }
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public float getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(float attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    public float getPastAttendancePercentage() { return pastAttendancePercentage; }
    public void setPastAttendancePercentage(float pastAttendancePercentage) { this.pastAttendancePercentage = pastAttendancePercentage; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public float getAvgMarks() { return avgMarks; }
    public void setAvgMarks(float avgMarks) { this.avgMarks = avgMarks; }
    public String getBacklogStatus() { return backlogStatus; }
    public void setBacklogStatus(String backlogStatus) { this.backlogStatus = backlogStatus; }
    public String getFeePaymentStatus() { return feePaymentStatus; }
    public void setFeePaymentStatus(String feePaymentStatus) { this.feePaymentStatus = feePaymentStatus; }
    public Float getEngagementScore() { return engagementScore; }
    public void setEngagementScore(Float engagementScore) { this.engagementScore = engagementScore; }
}
