package com.coder.drop_risk.dto;

public class ChatRequest {
    private String message;
    private String studentId; // Optional: to provide context about a specific student

    // Constructors, Getters, and Setters
    public ChatRequest() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
}