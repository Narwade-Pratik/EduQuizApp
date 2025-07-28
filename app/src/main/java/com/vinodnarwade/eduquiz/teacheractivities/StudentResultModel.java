package com.vinodnarwade.eduquiz.teacheractivities;

public class StudentResultModel {
    String studentId;
    int score;
    long timeTakenMillis;

    public StudentResultModel() {}

    public StudentResultModel(String studentId, int score, long timeTakenMillis) {
        this.studentId = studentId;
        this.score = score;
        this.timeTakenMillis = timeTakenMillis;
    }

    public String getStudentId() {
        return studentId;
    }

    public int getScore() {
        return score;
    }

    public long getTimeTakenMillis() {
        return timeTakenMillis;
    }
}
