package com.vinodnarwade.eduquiz.studentactivities;

public class StudentAttemptedQuizModel {
    String quizId, title, teacherId, subject;
    int score, totalQuestions;

    public StudentAttemptedQuizModel() {}

    public StudentAttemptedQuizModel(String quizId, String title, String teacherId, int score, int totalQuestions,String Subject) {
        this.quizId = quizId;
        this.title = title;
        this.teacherId = teacherId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.subject = Subject;
    }

    // Getters
    public String getQuizId() { return quizId; }
    public String getTitle() { return title; }
    public String getTeacherId() { return teacherId; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public String getSubject() { return subject; }
}

