package com.vinodnarwade.eduquiz.teacheractivities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.vinodnarwade.eduquiz.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SendParentReportActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> statusAdapter;
    private final ArrayList<String> statusLines = new ArrayList<>();

    private ArrayList<StudentModel> students;
    private String teacherId;

    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_parent_report);

        listView = findViewById(R.id.listViewSendReportStatus);

        statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                statusLines
        );

        listView.setAdapter(statusAdapter);

        teacherId = getIntent().getStringExtra("teacherId");
        students = getIntent().getParcelableArrayListExtra("students");

        if (students == null) {
            students = new ArrayList<>();
        }

        processNextStudent();
    }


    private void processNextStudent() {

        if (currentIndex >= students.size()) {

            statusLines.add("Done. All reports processed.");
            statusAdapter.notifyDataSetChanged();
            return;
        }

        StudentModel student = students.get(currentIndex);

        statusLines.add(student.getName() + ": checking parent email...");
        statusAdapter.notifyDataSetChanged();

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(student.getStudentId())
                .child("parentEmailId")
                .get()
                .addOnSuccessListener(this::handleParentEmailFetched)
                .addOnFailureListener(e -> {
                    updateLastStatus(student.getName() + ": failed to check email.");
                    scheduleNext();
                });
    }

    private void handleParentEmailFetched(DataSnapshot snapshot) {

        StudentModel student = students.get(currentIndex);
        String parentEmail = snapshot.getValue(String.class);

        if (parentEmail == null || parentEmail.trim().isEmpty()) {

            updateLastStatus(student.getName() + ": skipped (no parent email saved).");
            scheduleNext();
            return;
        }

        loadPerformanceAndSend(student, parentEmail.trim());
    }

    private void loadPerformanceAndSend(StudentModel student, String parentEmail) {

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(student.getStudentId())
                .child("TopicPerformance")
                .child(teacherId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    Map<String, String> params =
                            buildTemplateParams(student.getName(), parentEmail, snapshot);

                    EmailJsHelper.sendEmail(
                            params,
                            (success, message) -> {

                                if (success) {
                                    updateLastStatus(student.getName() + ": email sent.");
                                } else {
                                    updateLastStatus(student.getName() + ": failed (" + message + ").");
                                }

                                scheduleNext();
                            }
                    );
                })
                .addOnFailureListener(e -> {
                    updateLastStatus(student.getName() + ": failed to load performance data.");
                    scheduleNext();
                });
    }

    private Map<String, String> buildTemplateParams(
            String studentName,
            String parentEmail,
            DataSnapshot snapshot) {

        int totalCorrect = 0;
        int totalIncorrect = 0;
        int totalUnattempted = 0;

        StringBuilder summaryBuilder = new StringBuilder();
        StringBuilder weakTopicsBuilder = new StringBuilder();

        if (snapshot.exists()) {

            for (DataSnapshot subjectSnap : snapshot.getChildren()) {

                String subject = subjectSnap.getKey();
                summaryBuilder.append(subject).append(":\n");

                for (DataSnapshot topicSnap : subjectSnap.getChildren()) {

                    String topic = topicSnap.getKey();

                    int correct = 0, incorrect = 0, unattempted = 0;

                    for (DataSnapshot diffSnap : topicSnap.getChildren()) {

                        Long c = diffSnap.child("correctCount").getValue(Long.class);
                        Long i = diffSnap.child("incorrectCount").getValue(Long.class);
                        Long u = diffSnap.child("unattemptedCount").getValue(Long.class);

                        if (c != null) correct += c;
                        if (i != null) incorrect += i;
                        if (u != null) unattempted += u;
                    }

                    totalCorrect += correct;
                    totalIncorrect += incorrect;
                    totalUnattempted += unattempted;

                    summaryBuilder.append("  - ").append(topic)
                            .append(": Correct ").append(correct)
                            .append(", Incorrect ").append(incorrect)
                            .append(", Unattempted ").append(unattempted)
                            .append("\n");

                    int attempted = correct + incorrect;

                    if (attempted > 0) {

                        double accuracy = (correct * 100.0) / attempted;

                        if (accuracy < 40) {
                            weakTopicsBuilder.append("- ").append(topic)
                                    .append(" (").append(subject).append(")\n");
                        }
                    }
                }

                summaryBuilder.append("\n");
            }
        }

        int totalQuestions = totalCorrect + totalIncorrect + totalUnattempted;
        int attemptedTotal = totalCorrect + totalIncorrect;

        double percentage =
                attemptedTotal > 0 ? (totalCorrect * 100.0 / attemptedTotal) : 0;

        String weakTopics =
                weakTopicsBuilder.length() > 0
                        ? weakTopicsBuilder.toString()
                        : "No weak topics identified yet.";

        Map<String, String> params = new HashMap<>();
        params.put("parent_email", parentEmail);
        params.put("name", "EduQuiz");
        params.put("time", "");
        params.put("message", "");
        params.put("student_name", studentName);
        params.put("total_questions", String.valueOf(totalQuestions));
        params.put("correct_answers", String.valueOf(totalCorrect));
        params.put("incorrect_answers", String.valueOf(totalIncorrect));
        params.put("score", String.valueOf(totalCorrect));
        params.put("percentage", String.format(Locale.getDefault(), "%.1f", percentage));
        params.put("time_taken", "N/A");
        params.put("performance_summary", summaryBuilder.toString());
        params.put("weak_topics", weakTopics);

        return params;
    }

    private void updateLastStatus(String text) {

        if (!statusLines.isEmpty()) {
            statusLines.set(statusLines.size() - 1, text);
        } else {
            statusLines.add(text);
        }

        statusAdapter.notifyDataSetChanged();
    }

    private void scheduleNext() {

        currentIndex++;

        // EmailJS free tier allows 1 request/sec — small delay to stay safe.
        new Handler(Looper.getMainLooper()).postDelayed(
                this::processNextStudent,
                1200
        );
    }
}