package com.vinodnarwade.eduquiz.studentactivities;

import java.util.ArrayList;
import java.util.HashMap;

public class MyQuizModel {

    private String quizType;
    private String customQuizId;
    private String studentId;
    private int totalMarks;
    private String subject;
    private String chapter;
    private String topic;
    private String difficulty;


    private int score;
    private int numberOfQuestions;

    private long timeTakenMillis;

    private ArrayList<String> questionIds;

    private HashMap<String, String> answers;


    // =========================================================
    // EMPTY CONSTRUCTOR
    // Required by Firebase
    // =========================================================

    public MyQuizModel() {
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public String getQuizType() {
        return quizType;
    }

    public String getCustomQuizId() {
        return customQuizId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getSubject() {
        return subject;
    }

    public String getChapter() {
        return chapter;
    }

    public String getTopic() {
        return topic;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getScore() {
        return score;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public long getTimeTakenMillis() {
        return timeTakenMillis;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public ArrayList<String> getQuestionIds() {
        return questionIds;
    }

    public HashMap<String, String> getAnswers() {
        return answers;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    public void setQuizType(String quizType) {
        this.quizType = quizType;
    }

    public void setCustomQuizId(String customQuizId) {
        this.customQuizId = customQuizId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public void setTimeTakenMillis(long timeTakenMillis) {
        this.timeTakenMillis = timeTakenMillis;
    }

    public void setQuestionIds(ArrayList<String> questionIds) {
        this.questionIds = questionIds;
    }

    public void setAnswers(
            HashMap<String, String> answers) {

        this.answers = answers;
    }
}