package com.vinodnarwade.eduquiz.teacheractivities;

public class QuizModel {
    String quizID, title, subject, createdBy, className;
    int numberOfQuestions;
    String scheduledFirstDate, scheduledSecondDate;
    long scheduledTimestampFirst, scheduledTimestampSecond;

    public QuizModel() {
    }

    private int durationInMinutes;

    public QuizModel(String quizId, String title, String subjectName, int noOfQuestions,
                     String userId, String scheduledFirstDate, long scheduledTimestampFirst,
                     String scheduledSecondDate, long scheduledTimestampSecond,
                     int durationInMinutes, String className) {
        this.quizID = quizId;
        this.title = title;
        this.subject = subjectName;
        this.numberOfQuestions = noOfQuestions;
        this.createdBy = userId;
        this.scheduledFirstDate = scheduledFirstDate;
        this.scheduledTimestampFirst = scheduledTimestampFirst;
        this.scheduledSecondDate = scheduledSecondDate;
        this.scheduledTimestampSecond = scheduledTimestampSecond;
        this.durationInMinutes = durationInMinutes;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
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

    public String getScheduledFirstDate() {
        return scheduledFirstDate;
    }

    public void setScheduledFirstDate(String scheduledFirstDate) {
        this.scheduledFirstDate = scheduledFirstDate;
    }

    public String getScheduledSecondDate() {
        return scheduledSecondDate;
    }

    public void setScheduledSecondDate(String scheduledSecondDate) {
        this.scheduledSecondDate = scheduledSecondDate;
    }

    public long getScheduledTimestampFirst() {
        return scheduledTimestampFirst;
    }

    public void setScheduledTimestampFirst(long scheduledTimestampFirst) {
        this.scheduledTimestampFirst = scheduledTimestampFirst;
    }

    public long getScheduledTimestampSecond() {
        return scheduledTimestampSecond;
    }

    public void setScheduledTimestampSecond(long scheduledTimestampSecond) {
        this.scheduledTimestampSecond = scheduledTimestampSecond;
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