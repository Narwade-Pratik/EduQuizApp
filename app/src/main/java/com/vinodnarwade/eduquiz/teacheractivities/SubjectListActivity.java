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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class SubjectListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTitle;
    private TextView tvEmptyState;
    private String studentId;
    private String studentName;
    private String teacherId;
    private String className;

    private final ArrayList<String> subjectNames = new ArrayList<>();
    private SimpleNameListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        teacherId = getIntent().getStringExtra("teacherId");
        className = getIntent().getStringExtra("className");
        studentId = getIntent().getStringExtra("studentId");
        studentName = getIntent().getStringExtra("studentName");
        tvTitle = findViewById(R.id.tvSubjectListTitle);
        recyclerView = findViewById(R.id.recyclerViewSubjectList);
        tvEmptyState = findViewById(R.id.tvSubjectListEmptyState);

        tvTitle.setText((studentName != null ? studentName + " — " : "") + (className != null ? className : "Subjects"));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SimpleNameListAdapter(subjectNames, subject -> {

            Intent intent = new Intent(this, TopicListActivity.class);
            intent.putExtra("teacherId", teacherId);
            intent.putExtra("studentId", studentId);
            intent.putExtra("studentName", studentName);
            intent.putExtra("className", className);
            intent.putExtra("subject", subject);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        loadSubjects();
    }

    private void loadSubjects() {

        DatabaseReference quizRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(teacherId)
                .child("Quizzes");

        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Set<String> uniqueSubjects = new LinkedHashSet<>();

                for (DataSnapshot quizSnap : snapshot.getChildren()) {

                    QuizModel quiz = quizSnap.getValue(QuizModel.class);

                    if (quiz == null) {
                        continue;
                    }

                    String quizClassName = quiz.getClassName();
                    String quizSubject = quiz.getSubject();

                    if (quizClassName != null
                            && quizClassName.trim().equals(className)
                            && quizSubject != null
                            && !quizSubject.trim().isEmpty()) {

                        uniqueSubjects.add(quizSubject.trim());
                    }
                }

                subjectNames.clear();
                subjectNames.addAll(uniqueSubjects);
                Collections.sort(subjectNames);

                if (subjectNames.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SubjectListActivity.this, "Failed to load subjects", Toast.LENGTH_SHORT).show();
            }
        });
    }
}