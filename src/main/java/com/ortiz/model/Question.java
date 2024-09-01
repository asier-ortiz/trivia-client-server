package com.ortiz.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {
    private Integer id;
    private String question;
    private String category;
    private ArrayList<Answer> answers;

    public Question(Integer id, String question, String category) {
        this.id = id;
        this.question = question;
        this.category = category;
        this.answers = new ArrayList<>();
    }

    public Question(String question, String category) {
        this.question = question;
        this.category = category;
        this.answers = new ArrayList<>();
    }

    public Question() {
        this.answers = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", category='" + category + '\'' +
                ", answers=" + answers +
                '}';
    }
}