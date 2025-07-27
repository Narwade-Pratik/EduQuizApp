package com.vinodnarwade.eduquiz.studentactivities;

public class QuestionResultModel {
    private String question, option1, option2, option3, option4;
    private String yourAnswer, correctAnswer;
    private int marks;

    public QuestionResultModel() {
    }

    public QuestionResultModel(String question, String option1, String option2, String option3, String option4, String yourAnswer, String correctAnswer, int marks) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.yourAnswer = yourAnswer;
        this.correctAnswer = correctAnswer;
        this.marks = marks;
    }

    // Getters
    public String getQuestion() { return question; }
    public String getOption1() { return option1; }
    public String getOption2() { return option2; }
    public String getOption3() { return option3; }
    public String getOption4() { return option4; }
    public String getYourAnswer() { return yourAnswer; }
    public String getCorrectAnswer() { return correctAnswer; }
    public int getMarks() { return marks; }
}
