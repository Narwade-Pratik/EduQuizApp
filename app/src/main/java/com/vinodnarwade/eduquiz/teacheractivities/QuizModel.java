package com.vinodnarwade.eduquiz.teacheractivities;

public class QuizModel {
    String quizID, title, subject, createdBy;
    int numberOfQuestions;

    public QuizModel() {}

    public QuizModel(String quizID, String title, String subject, int numberOfQuestions, String createdBy) {
        this.quizID = quizID;
        this.title = title;
        this.subject = subject;
        this.numberOfQuestions = numberOfQuestions;
        this.createdBy = createdBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public String getQuizID() {
        return quizID;
    }

    public void setQuizID(String quizID) {
        this.quizID = quizID;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
