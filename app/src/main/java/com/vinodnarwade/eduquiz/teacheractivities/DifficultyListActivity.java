package com.vinodnarwade.eduquiz.teacheractivities;

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

public class DifficultyListActivity extends AppCompatActivity {

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
    private String subject;
    private String topic;

    private final ArrayList<WeakAreaRow> rows = new ArrayList<>();
    private WeakAreaDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weak_area_detail);

        teacherId = getIntent().getStringExtra("teacherId");
        studentId = getIntent().getStringExtra("studentId");
        studentName = getIntent().getStringExtra("studentName");
        subject = getIntent().getStringExtra("subject");
        topic = getIntent().getStringExtra("topic");

        pieChart = findViewById(R.id.pieChartWeakAreaDetail);
        tvLegendCorrect = findViewById(R.id.tvLegendCorrect);
        tvLegendIncorrect = findViewById(R.id.tvLegendIncorrect);
        tvLegendUnattempted = findViewById(R.id.tvLegendUnattempted);
        tvTitle = findViewById(R.id.tvWeakAreaDetailStudentName);
        recyclerView = findViewById(R.id.recyclerViewWeakAreaDetail);
        tvEmptyState = findViewById(R.id.tvWeakAreaDetailEmptyState);

        tvTitle.setText(
                (studentName != null ? studentName : "Student")
                        + " — " + (topic != null ? topic : "")
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // No further drill-down — difficulty is the leaf level, no click listener passed.
        adapter = new WeakAreaDetailAdapter(rows);
        recyclerView.setAdapter(adapter);

        loadDifficultiesForTopic();
    }

    private void loadDifficultiesForTopic() {

        DatabaseReference ref =
                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(studentId)
                        .child("TopicPerformance")
                        .child(teacherId)
                        .child(subject)
                        .child(topic);

        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        rows.clear();

                        if (!snapshot.exists()) {
                            showEmptyState();
                            return;
                        }

                        for (DataSnapshot difficultySnap : snapshot.getChildren()) {

                            String difficultyLabel = difficultySnap.getKey();

                            Long c = difficultySnap.child("correctCount")
                                    .getValue(Long.class);

                            Long i = difficultySnap.child("incorrectCount")
                                    .getValue(Long.class);

                            Long u = difficultySnap.child("unattemptedCount")
                                    .getValue(Long.class);

                            int correct = c != null ? c.intValue() : 0;
                            int incorrect = i != null ? i.intValue() : 0;
                            int unattempted = u != null ? u.intValue() : 0;

                            rows.add(
                                    new WeakAreaRow(
                                            difficultyLabel,
                                            "",
                                            correct,
                                            incorrect,
                                            unattempted
                                    )
                            );
                        }

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
                                DifficultyListActivity.this,
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