package com.vinodnarwade.eduquiz.studentactivities;

public class QuestionAttemptModel {

    private String questionId;
    private String questionTopic;
    private String difficulty;

    private String selectedAnswer;
    private String correctAnswer;

    private boolean correct;

    private int marks;

    public QuestionAttemptModel() {
        // Required by Firebase
    }

    public QuestionAttemptModel(
            String questionId,
            String questionTopic,
            String difficulty,
            String selectedAnswer,
            String correctAnswer,
            boolean correct,
            int marks) {

        this.questionId = questionId;
        this.questionTopic = questionTopic;
        this.difficulty = difficulty;
        this.selectedAnswer = selectedAnswer;
        this.correctAnswer = correctAnswer;
        this.correct = correct;
        this.marks = marks;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionTopic() {
        return questionTopic;
    }

    public void setQuestionTopic(String questionTopic) {
        this.questionTopic = questionTopic;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }
}