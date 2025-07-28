package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vinodnarwade.eduquiz.R;
import com.vinodnarwade.eduquiz.teacheractivities.QuizModel;

import java.util.ArrayList;

public class QuizOverviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuizOverviewAdapter adapter;
    private ArrayList<QuizModel> quizList;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;

    private DatabaseReference quizRef;
    private String teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_overview);

        recyclerView = findViewById(R.id.recyclerViewQuizOverview);
        progressBar = findViewById(R.id.progressBarQuizOverviewActivity);

        quizList = new ArrayList<>();
        adapter = new QuizOverviewAdapter(this, quizList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        teacherId = sharedPreferences.getString("userId","").trim() ;
        Toast.makeText(this, "TeacherId: " + teacherId, Toast.LENGTH_SHORT).show();
        quizRef = FirebaseDatabase.getInstance().getReference("Users").child(teacherId).child("Quizzes");

        loadTeacherQuizzes();
    }

    private void loadTeacherQuizzes() {
        progressBar.setVisibility(View.VISIBLE);
        quizRef.orderByChild("createdBy").equalTo(teacherId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        quizList.clear();
                        for (DataSnapshot quizSnap : snapshot.getChildren()) {
                            QuizModel quiz = quizSnap.getValue(QuizModel.class);
                            quizList.add(quiz);
                        }
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(QuizOverviewActivity.this, "Failed to load quizzes", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
