package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class WeakAreaStudentListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmptyState;

    private String teacherId;

    private final ArrayList<StudentModel> studentList = new ArrayList<>();
    private WeakAreaStudentAdapter adapter;
    private String filterClassName;
    private String filterSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weak_area_student_list);

        teacherId = getIntent().getStringExtra("teacherId");

        if (teacherId == null || teacherId.isEmpty()) {

            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(this);

            teacherId = prefs.getString("userId", "");
        }

        recyclerView = findViewById(R.id.recyclerViewWeakAreaStudents);
        tvEmptyState = findViewById(R.id.tvWeakAreaStudentsEmptyState);
        filterClassName = getIntent().getStringExtra("className");
        filterSubject = getIntent().getStringExtra("subject");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WeakAreaStudentAdapter(studentList, student -> {

            Intent intent = new Intent(this, SubjectListActivity.class);
            intent.putExtra("teacherId", teacherId);
            intent.putExtra("className", filterClassName);
            intent.putExtra("studentId", student.getStudentId());
            intent.putExtra("studentName", student.getName());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        loadStudentsWhoAttempted();
    }

    private void loadStudentsWhoAttempted() {

        DatabaseReference quizzesRef =
                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(teacherId)
                        .child("Quizzes");

        quizzesRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Set<String> studentIds = new LinkedHashSet<>();

                        for (DataSnapshot quizSnap : snapshot.getChildren()) {

                            QuizModel quiz = quizSnap.getValue(QuizModel.class);

                            if (quiz == null) {
                                continue;
                            }

                            if (filterClassName != null
                                    && !filterClassName.trim().isEmpty()) {

                                if (quiz.getClassName() == null
                                        || !quiz.getClassName().trim().equals(filterClassName.trim())) {
                                    continue;
                                }
                            }

                            DataSnapshot attemptedBySnap =
                                    quizSnap.child("AttemptedBy");

                            for (DataSnapshot studentSnap :
                                    attemptedBySnap.getChildren()) {

                                String sid = studentSnap.getKey();

                                if (sid != null) {
                                    studentIds.add(sid);
                                }
                            }
                        }

                        if (studentIds.isEmpty()) {
                            showEmptyState();
                            return;
                        }

                        fetchStudentDetails(studentIds);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(
                                WeakAreaStudentListActivity.this,
                                "Failed to load students: " + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void fetchStudentDetails(Set<String> studentIds) {

        final int total = studentIds.size();
        final int[] fetched = {0};

        DatabaseReference usersRef =
                FirebaseDatabase.getInstance().getReference("Users");

        for (String studentId : studentIds) {

            usersRef.child(studentId).child("name")
                    .get()
                    .addOnSuccessListener(snapshot -> {

                        String name = snapshot.getValue(String.class);

                        studentList.add(
                                new StudentModel(
                                        studentId,
                                        name != null ? name : "Unknown"
                                )
                        );

                        fetched[0]++;

                        if (fetched[0] == total) {
                            onAllStudentsLoaded();
                        }
                    })
                    .addOnFailureListener(e -> {

                        fetched[0]++;

                        if (fetched[0] == total) {
                            onAllStudentsLoaded();
                        }
                    });
        }
    }

    private void onAllStudentsLoaded() {

        if (studentList.isEmpty()) {
            showEmptyState();
            return;
        }

        Collections.sort(
                studentList,
                (a, b) -> a.getName().compareToIgnoreCase(b.getName())
        );

        tvEmptyState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    private void showEmptyState() {
        tvEmptyState.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}