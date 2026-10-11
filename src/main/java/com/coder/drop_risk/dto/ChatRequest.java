package com.coder.drop_risk.dto;

import java.util.List;

public class ChatRequest {
    private String message;
    private String studentId;
    private List<ChatTurn> history;

    public ChatRequest() {}
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public List<ChatTurn> getHistory() { return history; }
    public void setHistory(List<ChatTurn> history) { this.history = history; }

    public static class ChatTurn {
        private String role;
        private String content;
        public ChatTurn() {}
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}