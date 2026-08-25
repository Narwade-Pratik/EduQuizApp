package com.vinodnarwade.eduquiz.teacheractivities;

public class StudentModel {

    private String studentId;
    private String name;

    public StudentModel() {}

    public StudentModel(String studentId, String name) {
        this.studentId = studentId;
        this.name = name;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}