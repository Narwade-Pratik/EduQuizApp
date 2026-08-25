package com.vinodnarwade.eduquiz.teacheractivities;

public class WeakAreaRow {

    private String subject;
    private String topic;
    private int correctCount;
    private int incorrectCount;
    private int unattemptedCount;

    public WeakAreaRow() {}

    public WeakAreaRow(String subject, String topic,
                       int correctCount, int incorrectCount, int unattemptedCount) {

        this.subject = subject;
        this.topic = topic;
        this.correctCount = correctCount;
        this.incorrectCount = incorrectCount;
        this.unattemptedCount = unattemptedCount;
    }

    public String getSubject() { return subject; }
    public String getTopic() { return topic; }
    public int getCorrectCount() { return correctCount; }
    public int getIncorrectCount() { return incorrectCount; }
    public int getUnattemptedCount() { return unattemptedCount; }

    public String getPerformanceLabel() {

        int attempted = correctCount + incorrectCount;

        if (attempted == 0) {
            return "No Data";
        }

        double accuracy = (correctCount * 100.0) / attempted;

        if (accuracy > 70) {
            return "Strong";
        } else if (accuracy >= 40) {
            return "Average";
        } else {
            return "Weak";
        }
    }
}