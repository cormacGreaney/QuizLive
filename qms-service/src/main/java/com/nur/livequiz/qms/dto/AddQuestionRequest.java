package com.nur.livequiz.qms.dto;

import java.util.List;

/** Add one question to a quiz with multiple choice options. */
public class AddQuestionRequest {
    private String questionText;
    private List<String> options;
    private Integer correctOption;

    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public Integer getCorrectOption() { return correctOption; }

    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setOptions(List<String> options) { this.options = options; }
    public void setCorrectOption(Integer correctOption) { this.correctOption = correctOption; }
}
