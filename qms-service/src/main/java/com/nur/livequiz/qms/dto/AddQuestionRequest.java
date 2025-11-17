package com.nur.livequiz.qms.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddQuestionRequest {
    private String questionText;

    // New UI (preferred)
    private List<String> options = new ArrayList<>();

    // Old UI variants
    private String optionsCsv;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    private Integer correctOption;

    public String getQuestionText() { return questionText; }
    public Integer getCorrectOption() { return correctOption; }
    public String getOptionsCsv() { return optionsCsv; }

    public void setQuestionText(String questionText) { this.questionText = questionText; }
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    public void setOptions(List<String> options) { this.options = options; }
    public void setOptionsCsv(String optionsCsv) { this.optionsCsv = optionsCsv; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    public void setCorrectOption(Integer correctOption) { this.correctOption = correctOption; }

    
    public List<String> normalizedOptions() {
        if (options != null && !options.isEmpty()) {
            return options.stream().map(s -> s == null ? "" : s.trim()).filter(s -> !s.isEmpty()).toList();
        }
        List<String> fromFields = new ArrayList<>();
        if (optionA != null) fromFields.add(optionA.trim());
        if (optionB != null) fromFields.add(optionB.trim());
        if (optionC != null) fromFields.add(optionC.trim());
        if (optionD != null) fromFields.add(optionD.trim());
        fromFields = fromFields.stream().filter(s -> !s.isEmpty()).toList();
        if (!fromFields.isEmpty()) return fromFields;

        if (optionsCsv != null && !optionsCsv.isBlank()) {
            return Arrays.stream(optionsCsv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }
        return List.of();
    }
}
