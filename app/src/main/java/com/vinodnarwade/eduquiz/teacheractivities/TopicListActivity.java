package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vinodnarwade.eduquiz.R;

import java.util.ArrayList;

public class TopicListActivity extends AppCompatActivity {

    private TextView tvTitle;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private PieChartView pieChart;
    private TextView tvLegendCorrect;
    private TextView tvLegendIncorrect;
    private TextView tvLegendUnattempted;

    private String teacherId;
    private String studentId;
    private String studentName;
    private String className;
    private String subject;

    private final ArrayList<WeakAreaRow> rows = new ArrayList<>();
    private WeakAreaDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weak_area_detail);

        teacherId = getIntent().getStringExtra("teacherId");
        studentId = getIntent().getStringExtra("studentId");
        studentName = getIntent().getStringExtra("studentName");
        className = getIntent().getStringExtra("className");
        subject = getIntent().getStringExtra("subject");

        pieChart = findViewById(R.id.pieChartWeakAreaDetail);
        tvLegendCorrect = findViewById(R.id.tvLegendCorrect);
        tvLegendIncorrect = findViewById(R.id.tvLegendIncorrect);
        tvLegendUnattempted = findViewById(R.id.tvLegendUnattempted);
        tvTitle = findViewById(R.id.tvWeakAreaDetailStudentName);
        recyclerView = findViewById(R.id.recyclerViewWeakAreaDetail);
        tvEmptyState = findViewById(R.id.tvWeakAreaDetailEmptyState);

        tvTitle.setText(
                (studentName != null ? studentName : "Student")
                        + " — " + (subject != null ? subject : "")
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeakAreaDetailAdapter(rows, row -> {

            Intent intent = new Intent(this, DifficultyListActivity.class);
            intent.putExtra("teacherId", teacherId);
            intent.putExtra("studentId", studentId);
            intent.putExtra("studentName", studentName);
            intent.putExtra("subject", subject);
            intent.putExtra("topic", row.getTopic());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadTopicsForSubject();
    }

    private void loadTopicsForSubject() {

        DatabaseReference ref =
                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(studentId)
                        .child("TopicPerformance")
                        .child(teacherId)
                        .child(subject);

        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        rows.clear();

                        if (!snapshot.exists()) {
                            showEmptyState();
                            return;
                        }

                        for (DataSnapshot topicSnap : snapshot.getChildren()) {

                            String topic = topicSnap.getKey();

                            int correct = 0;
                            int incorrect = 0;
                            int unattempted = 0;

                            for (DataSnapshot difficultySnap :
                                    topicSnap.getChildren()) {

                                Long c = difficultySnap.child("correctCount")
                                        .getValue(Long.class);

                                Long i = difficultySnap.child("incorrectCount")
                                        .getValue(Long.class);

                                Long u = difficultySnap.child("unattemptedCount")
                                        .getValue(Long.class);

                                if (c != null) correct += c;
                                if (i != null) incorrect += i;
                                if (u != null) unattempted += u;
                            }

                            rows.add(
                                    new WeakAreaRow(
                                            subject,
                                            topic,
                                            correct,
                                            incorrect,
                                            unattempted
                                    )
                            );
                        }

                        // ===== NAYA BLOCK — YEH ADD KARNA THA =====

                        int totalCorrect = 0;
                        int totalIncorrect = 0;
                        int totalUnattempted = 0;

                        for (WeakAreaRow row : rows) {
                            totalCorrect += row.getCorrectCount();
                            totalIncorrect += row.getIncorrectCount();
                            totalUnattempted += row.getUnattemptedCount();
                        }

                        pieChart.setData(totalCorrect, totalIncorrect, totalUnattempted);

                        int grandTotal = totalCorrect + totalIncorrect + totalUnattempted;

                        tvLegendCorrect.setText(
                                "Correct (" + percentOf(totalCorrect, grandTotal) + "%)"
                        );

                        tvLegendIncorrect.setText(
                                "Incorrect (" + percentOf(totalIncorrect, grandTotal) + "%)"
                        );

                        tvLegendUnattempted.setText(
                                "Unattempted (" + percentOf(totalUnattempted, grandTotal) + "%)"
                        );

                        // ===== NAYA BLOCK KHATAM =====

                        if (rows.isEmpty()) {
                            showEmptyState();
                        } else {
                            tvEmptyState.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(
                                TopicListActivity.this,
                                "Failed to load data: " + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void showEmptyState() {
        tvEmptyState.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private int percentOf(int part, int total) {
        return total > 0 ? Math.round((part * 100f) / total) : 0;
    }
}