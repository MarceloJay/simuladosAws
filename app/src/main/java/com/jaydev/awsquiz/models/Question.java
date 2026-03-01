package com.jaydev.awsquiz.models;

import java.util.List;

public class Question {
    private String question;
    private List<String> options;
    private List<Integer> correctAnswers; // índices das respostas corretas (permite múltiplas)

    public Question(String question, List<String> options, List<Integer> correctAnswers) {
        this.question = question;
        this.options = options;
        this.correctAnswers = correctAnswers;
    }

    public String getQuestion() { return question; }
    public List<String> getOptions() { return options; }
    public List<Integer> getCorrectAnswers() { return correctAnswers; }
}
