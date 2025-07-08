package com.vinodnarwade.eduquiz;

public class multipleChoice extends Questions {
    private String optionA, optionB, optionC, optionD;
    public multipleChoice(String question,int marks,String correctOption,String typeOfQuestion,String Category,String optionA,String optionB,String optionC,String optionD){
        super(question,marks,correctOption,typeOfQuestion,Category);
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
    }
}
