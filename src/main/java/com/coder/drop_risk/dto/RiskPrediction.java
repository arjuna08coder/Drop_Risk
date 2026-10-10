package com.coder.drop_risk.dto;

import java.util.List;

public class RiskPrediction {
    private double riskScore; // e.g., 85.5%
    private String riskLevel; // HIGH, MODERATE, LOW
    private List<String> contributingFactors; // Explainability factors
    private List<String> recommendedInterventions; // Suggested actions

    public RiskPrediction(double riskScore, String riskLevel, List<String> contributingFactors, List<String> recommendedInterventions) {
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.contributingFactors = contributingFactors;
        this.recommendedInterventions = recommendedInterventions;
    }

    // Getters and Setters
    public double getRiskScore() { return riskScore; }
    public String getRiskLevel() { return riskLevel; }
    public List<String> getContributingFactors() { return contributingFactors; }
    public List<String> getRecommendedInterventions() { return recommendedInterventions; }

}