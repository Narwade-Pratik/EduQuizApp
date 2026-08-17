package com.vinodnarwade.eduquiz.studentactivities;

public class StudentQuestionBankModel {

    private String teacherId;
    private String className;
    private String subject;
    private String chapter;
    private String topic;

    private int easyCount;
    private int mediumCount;
    private int hardCount;

    public StudentQuestionBankModel() {
        // Required for Firebase
    }

    public StudentQuestionBankModel(
            String teacherId,
            String className,
            String subject,
            String chapter,
            String topic,
            int easyCount,
            int mediumCount,
            int hardCount) {

        this.teacherId = teacherId;
        this.className = className;
        this.subject = subject;
        this.chapter = chapter;
        this.topic = topic;
        this.easyCount = easyCount;
        this.mediumCount = mediumCount;
        this.hardCount = hardCount;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getClassName() {
        return className;
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

    public int getEasyCount() {
        return easyCount;
    }

    public int getMediumCount() {
        return mediumCount;
    }

    public int getHardCount() {
        return hardCount;
    }
}