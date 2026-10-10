package com.coder.drop_risk.service;

import com.coder.drop_risk.model.Student;
import com.coder.drop_risk.repository.StudentRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

    private final ChatClient chatClient;

    @Autowired
    private StudentRepository studentRepository;

    public AiChatService(ChatClient.Builder chatClientBuilder) {
        // Configure a system prompt defining the AI's persona as an expert student retention counselor
        this.chatClient = chatClientBuilder
                .defaultSystem("You are an expert AI assistant for a Student Dropout Risk & Intervention Management Platform. " +
                        "Your goal is to help counselors analyze student risk factors, recommend effective interventions, " +
                        "and provide empathetic, data-driven advice.")
                .build();
    }

    public String chat(String userMessage, String studentId) {
        String contextPrompt = userMessage;

        // If a student ID is provided, fetch their details and inject them into the AI's context
        if (studentId != null && !studentId.isEmpty()) {
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student != null) {
                contextPrompt = String.format(
                        "Context: The user is asking about student ID %s (%s). " +
                                "Attendance: %.1f%%, Marks: %.1f, Backlogs: %s, Fee Status: %s.\n\nUser Question: %s",
                        student.getStudentId(), student.getName(), student.getAttendancePercentage(),
                        student.getAvgMarks(), student.getBacklogStatus(), student.getFeePaymentStatus(),
                        userMessage
                );
            }
        }

        // Call the LLM using Spring AI ChatClient
        return chatClient.prompt()
                .user(contextPrompt)
                .call()
                .content();
    }
}