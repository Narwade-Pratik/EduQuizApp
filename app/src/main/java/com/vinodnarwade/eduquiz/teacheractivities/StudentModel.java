package com.vinodnarwade.eduquiz.teacheractivities;

import android.os.Parcel;
import android.os.Parcelable;

public class StudentModel implements Parcelable {

    private String studentId;
    private String name;

    public StudentModel() {}

    public StudentModel(String studentId, String name) {
        this.studentId = studentId;
        this.name = name;
    }

    protected StudentModel(Parcel in) {
        studentId = in.readString();
        name = in.readString();
    }

    public static final Creator<StudentModel> CREATOR = new Creator<StudentModel>() {
        @Override
        public StudentModel createFromParcel(Parcel in) {
            return new StudentModel(in);
        }

        @Override
        public StudentModel[] newArray(int size) {
            return new StudentModel[size];
        }
    };

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(studentId);
        dest.writeString(name);
    }
}