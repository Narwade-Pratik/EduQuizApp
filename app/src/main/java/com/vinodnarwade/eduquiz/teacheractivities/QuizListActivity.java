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

public class QuizListActivity extends AppCompatActivity {

    private TextView tvTitle;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;

    private String teacherId;
    private String className;

    private final ArrayList<QuizModel> quizList = new ArrayList<>();
    private QuizSelectListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        teacherId = getIntent().getStringExtra("teacherId");
        className = getIntent().getStringExtra("className");

        tvTitle = findViewById(R.id.tvSubjectListTitle);
        recyclerView = findViewById(R.id.recyclerViewSubjectList);
        tvEmptyState = findViewById(R.id.tvSubjectListEmptyState);

        tvTitle.setText(className != null ? className : "Quizzes");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new QuizSelectListAdapter(quizList, quiz -> {

            Intent intent = new Intent(this, WeakAreaStudentListActivity.class);
            intent.putExtra("teacherId", teacherId);
            intent.putExtra("className", className);
            intent.putExtra("quizId", quiz.getQuizID());
            intent.putExtra("quizTitle", quiz.getTitle());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        loadQuizzes();
    }

    private void loadQuizzes() {

        DatabaseReference quizRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(teacherId)
                .child("Quizzes");

        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                quizList.clear();

                for (DataSnapshot quizSnap : snapshot.getChildren()) {

                    QuizModel quiz = quizSnap.getValue(QuizModel.class);

                    if (quiz == null) {
                        continue;
                    }

                    if (className != null && !className.trim().isEmpty()) {

                        if (quiz.getClassName() == null
                                || !quiz.getClassName().trim().equals(className.trim())) {
                            continue;
                        }
                    }

                    quizList.add(quiz);
                }

                if (quizList.isEmpty()) {
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
                Toast.makeText(QuizListActivity.this, "Failed to load quizzes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}