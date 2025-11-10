package com.nur.livequiz.qms.dto;

/** Add one question to a quiz (choices come later; POC keeps it simple). */
public class AddQuestionRequest {
    private String questionText;
    private Integer correctOption; // optional for now

    public String getQuestionText() { return questionText; }
    public Integer getCorrectOption() { return correctOption; }

    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setCorrectOption(Integer correctOption) { this.correctOption = correctOption; }
}
