package com.vinodnarwade.eduquiz;

public class Questions {
    private String question;
    private String correctOption;
    private String typeOfQuestion;
    private int marks;
    private String Category;

    public Questions(String question,int marks,String correctOption,String typeOfQuestion,String Category){
        this.Category = Category;
        this.question = question;
        this.typeOfQuestion = typeOfQuestion;
        this.marks = marks;
        this.correctOption = correctOption;
    }
}
