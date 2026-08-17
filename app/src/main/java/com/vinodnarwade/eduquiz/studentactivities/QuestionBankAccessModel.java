package com.vinodnarwade.eduquiz.studentactivities;

public class QuestionBankAccessModel {

    private String teacherId;

    private String className;
    private String subject;
    private String chapter;
    private String topic;

    private boolean access;

    // Required for Firebase
    public QuestionBankAccessModel() {
    }

    public QuestionBankAccessModel(
            String teacherId,
            String className,
            String subject,
            String chapter,
            String topic,
            boolean access) {

        this.teacherId = teacherId;
        this.className = className;
        this.subject = subject;
        this.chapter = chapter;
        this.topic = topic;
        this.access = access;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }
}
