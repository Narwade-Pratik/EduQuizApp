package com.vinodnarwade.eduquiz.studentactivities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.vinodnarwade.eduquiz.R;

import java.util.Map;

public class ShowStudentResultActivity extends AppCompatActivity {

    TextView tvQuizTitle, tvSubject, tvTeacher, tvDateTime, tvScore, tvRank, tvTimeTaken;
    LinearLayout questionsContainer;

    String teacherId, quizId, studentId;
    Long timeTaken, totalMarks = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_student_result);

        // Initialize views
        tvQuizTitle = findViewById(R.id.tvShowStudentResultQuizTitle);
        tvSubject = findViewById(R.id.tvShowStudentResultSubject);
        tvTeacher = findViewById(R.id.tvShowStudentResultTeacher);
        tvDateTime = findViewById(R.id.tvShowStudentResultDateTime);
        tvScore = findViewById(R.id.tvShowStudentResultScore);
        tvRank = findViewById(R.id.tvShowStudentResultRank);
        tvTimeTaken = findViewById(R.id.tvShowStudentResultTimeTaken);
        questionsContainer = findViewById(R.id.questionsContainerShowStudentResult);

        // Get data from intent
        teacherId = getIntent().getStringExtra("teacherId");
        quizId = getIntent().getStringExtra("quizId");
        studentId = getIntent().getStringExtra("studentId");

        loadQuizResult();
    }

    private void loadQuizResult() {
        DatabaseReference quizRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(teacherId).child("Quizzes").child(quizId);

        quizRef.get().addOnSuccessListener(snapshot -> {
            String title = snapshot.child("title").getValue(String.class);
            String subject = snapshot.child("subject").getValue(String.class);
            String createdBy = snapshot.child("createdBy").getValue(String.class);
            String dateTime = snapshot.child("dateTime").getValue(String.class);

            tvQuizTitle.setText(title != null ? title : "Quiz Title");
            tvSubject.setText(subject != null ? subject : "Subject");
            tvDateTime.setText(dateTime != null ? dateTime : "Not Available");

            if (createdBy != null) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(createdBy)
                        .child("name").get().addOnSuccessListener(nameSnap -> {
                            String teacherName = nameSnap.getValue(String.class);
                            tvTeacher.setText(teacherName != null ? teacherName : "Unknown");
                        });
            } else {
                tvTeacher.setText("Unknown");
            }

            // Load result
            DatabaseReference resultRef = quizRef.child("AttemptedBy").child(studentId);
            resultRef.get().addOnSuccessListener(resultSnap -> {
                Long score = resultSnap.child("score").getValue(Long.class);
                timeTaken = resultSnap.child("timeTakenMillis").getValue(Long.class);

                if (timeTaken != null) {
                    long seconds = timeTaken / 1000;
                    long minutes = seconds / 60;
                    seconds = seconds % 60;
                    tvTimeTaken.setText("Time Taken: " + minutes + "m " + seconds + "s");
                } else {
                    tvTimeTaken.setText("Time Taken: N/A");
                }

                // Load answers map
                Map<String, Object> answersMap = (Map<String, Object>) resultSnap.child("answers").getValue();
                if (answersMap != null) {
                    final int totalQuestions = answersMap.size();
                    final long[] totalMarksArr = {0};
                    final int[] fetchedCount = {0};

                    for (String qid : answersMap.keySet()) {
                        String selected = String.valueOf(answersMap.get(qid));

                        quizRef.child("Questions").child(qid).get().addOnSuccessListener(questionSnap -> {
                            String question = questionSnap.child("question").getValue(String.class);
                            String optionA = questionSnap.child("optionA").getValue(String.class);
                            String optionB = questionSnap.child("optionB").getValue(String.class);
                            String optionC = questionSnap.child("optionC").getValue(String.class);
                            String optionD = questionSnap.child("optionD").getValue(String.class);
                            String correct = questionSnap.child("correctOption").getValue(String.class);
                            Long marks = questionSnap.child("marks").getValue(Long.class);

                            if (marks == null) marks = 0L;
                            totalMarksArr[0] += marks;

                            addQuestionResultView(question, optionA, optionB, optionC, optionD, selected, correct, marks);

                            // Once all questions are loaded, update score
                            fetchedCount[0]++;
                            if (fetchedCount[0] == totalQuestions) {
                                tvScore.setText("Score: " + (score != null ? score : 0) + "/" + totalMarksArr[0]);
                            }
                        });
                    }
                } else {
                    tvScore.setText("Score: " + (score != null ? score : 0) + "/0");
                }

                tvRank.setText("Rank: N/A");
            });
        });
    }


    private void addQuestionResultView(String question, String a, String b, String c, String d, String selected, String correct, Long marks) {
        TextView tv = new TextView(this);
        tv.setText(
                "Q: " + question + "\n" +
                        "A. " + a + "\n" +
                        "B. " + b + "\n" +
                        "C. " + c + "\n" +
                        "D. " + d + "\n" +
                        "Selected: " + selected + "\n" +
                        "Correct: " + correct + "\n" +
                        "Marks: " + (marks != null ? marks : "0")
        );
        tv.setPadding(0, 24, 0, 24);
        tv.setTextSize(16);
        questionsContainer.addView(tv);
    }
}
