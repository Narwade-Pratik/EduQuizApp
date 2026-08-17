package com.vinodnarwade.eduquiz.teacheractivities;

import android.os.Parcel;
import android.os.Parcelable;

public class QuestionModel implements Parcelable {
    String questionId, quizId, questionTopic, question, optionA, optionB, optionC, optionD, correctOption;
    int marks;

    public QuestionModel() {}

    public QuestionModel(String questionId, String quizId, String questionTopic, String question, String optionA, String optionB,
                         String optionC, String optionD, String correctOption, int marks) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.questionTopic = questionTopic;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.marks = marks;
    }

    protected QuestionModel(Parcel in) {
        questionId = in.readString();
        quizId = in.readString();
        questionTopic = in.readString();
        question = in.readString();
        optionA = in.readString();
        optionB = in.readString();
        optionC = in.readString();
        optionD = in.readString();
        correctOption = in.readString();
        marks = in.readInt();
    }

    public static final Creator<QuestionModel> CREATOR = new Creator<QuestionModel>() {
        @Override
        public QuestionModel createFromParcel(Parcel in) {
            return new QuestionModel(in);
        }

        @Override
        public QuestionModel[] newArray(int size) {
            return new QuestionModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(questionId);
        parcel.writeString(quizId);
        parcel.writeString(questionTopic);
        parcel.writeString(question);
        parcel.writeString(optionA);
        parcel.writeString(optionB);
        parcel.writeString(optionC);
        parcel.writeString(optionD);
        parcel.writeString(correctOption);
        parcel.writeInt(marks);
    }

    // Getters and Setters
    public String getCorrectOption() { return correctOption; }
    public void setCorrectOption(String correctOption) { this.correctOption = correctOption; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getQuestionTopic() { return questionTopic; }
    public void setQuestionTopic(String questionTopic) { this.questionTopic = questionTopic; }

    public int getMarks() { return marks; }
    public void setMarks(int marks) { this.marks = marks; }

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }

    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }
}
