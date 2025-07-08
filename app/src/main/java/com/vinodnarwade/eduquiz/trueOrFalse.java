package com.vinodnarwade.eduquiz;

public class trueOrFalse extends  Questions {
    private String optionA, optionB;
    public trueOrFalse(String question,int marks,String correctOption,String typeOfQuestion,String Category,String optionA,String optionB){
        super(question,marks,correctOption,typeOfQuestion,Category);
        this.optionA = optionA;
        this.optionB = optionB;
    }

}
