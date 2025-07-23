package com.vinodnarwade.eduquiz.teacheractivities;

public class QuizModel {
    String quizID, title, subject, createdBy;
    int numberOfQuestions;
    String scheduledDate;
    long scheduledTimestamp;
    public QuizModel() {}

    public QuizModel(String quizID, String title, String subject, int numberOfQuestions, String createdBy,String scheduledDate,long scheduledTimestamp) {
        this.quizID = quizID;
        this.title = title;
        this.subject = subject;
        this.numberOfQuestions = numberOfQuestions;
        this.createdBy = createdBy;
        this.scheduledDate = scheduledDate;
        this.scheduledTimestamp = scheduledTimestamp;
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

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public long getScheduledTimestamp() {
        return scheduledTimestamp;
    }

    public void setScheduledTimestamp(long scheduledTimestamp) {
        this.scheduledTimestamp = scheduledTimestamp;
    }


}