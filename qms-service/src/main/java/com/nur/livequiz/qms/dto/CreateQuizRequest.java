package com.nur.livequiz.qms.dto;

/**
 * Minimal payload for creating a quiz.
 * Participants don't need accounts; admins will create via UI later.
 */
public class CreateQuizRequest {
    private String title;
    private String description;

    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
}
