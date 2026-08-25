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

public class WeakAreaDetailActivity extends AppCompatActivity {

    private TextView tvStudentName;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;

    private String teacherId;
    private String studentId;
    private String studentName;

    private final ArrayList<WeakAreaRow> rows = new ArrayList<>();
    private WeakAreaDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weak_area_detail);

        teacherId = getIntent().getStringExtra("teacherId");
        studentId = getIntent().getStringExtra("studentId");
        studentName = getIntent().getStringExtra("studentName");

        tvStudentName = findViewById(R.id.tvWeakAreaDetailStudentName);
        recyclerView = findViewById(R.id.recyclerViewWeakAreaDetail);
        tvEmptyState = findViewById(R.id.tvWeakAreaDetailEmptyState);

        tvStudentName.setText(studentName != null ? studentName : "Student");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeakAreaDetailAdapter(rows);
        recyclerView.setAdapter(adapter);

        loadTopicPerformance();
    }

    private void loadTopicPerformance() {

        DatabaseReference ref =
                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(studentId)
                        .child("TopicPerformance")
                        .child(teacherId);

        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        rows.clear();

                        if (!snapshot.exists()) {
                            showEmptyState();
                            return;
                        }

                        for (DataSnapshot subjectSnap : snapshot.getChildren()) {

                            String subject = subjectSnap.getKey();

                            for (DataSnapshot topicSnap : subjectSnap.getChildren()) {

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
                        }

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
                                WeakAreaDetailActivity.this,
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
}