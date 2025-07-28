package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
import com.vinodnarwade.eduquiz.teacheractivities.StudentResultAdapter;
import com.vinodnarwade.eduquiz.teacheractivities.StudentResultModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QuizResultActivity extends AppCompatActivity {

    TextView tvQuizTitle, tvQuizSubject, tvTotalQuestions;
    RecyclerView recyclerView;
    StudentResultAdapter adapter;
    List<StudentResultModel> studentList = new ArrayList<>();
    String quizId,userId,quizTitle;
    SharedPreferences sharedPreferences;

    DatabaseReference quizRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        tvQuizTitle = findViewById(R.id.tvQuizResultActivityQuizTitle);
        tvQuizSubject = findViewById(R.id.tvQuizResultActivityQuizSubject);
        tvTotalQuestions = findViewById(R.id.tvQuizResultActivityTotalQuestions);
        recyclerView = findViewById(R.id.recyclerQuizResultActivityStudentResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        quizId = getIntent().getStringExtra("quizId");
        quizTitle = getIntent().getStringExtra("quizTitle");
        if (quizId == null || quizTitle == null) {
            Toast.makeText(this, "Null hai null", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Toast.makeText(this, "quizId "+quizId+" "+quizTitle, Toast.LENGTH_SHORT).show();

        adapter = new StudentResultAdapter(this, studentList);
        recyclerView.setAdapter(adapter);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = sharedPreferences.getString("userId","");

        quizRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(userId)
                .child("Quizzes")
                .child(quizId);

        Log.d("QuizRefPath", quizRef.toString());

        loadQuizDetails();
        loadStudentResults();
    }

    private void loadQuizDetails() {
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String title = snapshot.child("title").getValue(String.class);
                    String subject = snapshot.child("subject").getValue(String.class);
                    long totalQ = snapshot.child("numberOfQuestions").getValue(Long.class);

                    tvQuizTitle.setText(title);
                    tvQuizSubject.setText("Subject: " + subject);
                    tvTotalQuestions.setText("Total Questions: " + totalQ);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void loadStudentResults() {
        quizRef.child("AttemptedBy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList.clear(); // clear list before loading fresh data

                for (DataSnapshot child : snapshot.getChildren()) {
                    String studentId = child.getKey();

                    Long scoreNum = child.child("score").getValue(Long.class);
                    Long timeNum = child.child("timeTakenMillis").getValue(Long.class);

                    if (scoreNum != null && timeNum != null) {
                        studentList.add(new StudentResultModel(studentId, scoreNum.intValue(), timeNum));
                    }

                }

                // Sort by score descending, then time ascending
                Collections.sort(studentList, new Comparator<StudentResultModel>() {
                    @Override
                    public int compare(StudentResultModel s1, StudentResultModel s2) {
                        if (s2.getScore() != s1.getScore()) {
                            return Integer.compare(s2.getScore(), s1.getScore()); // higher score first
                        } else {
                            return Long.compare(s1.getTimeTakenMillis(), s2.getTimeTakenMillis()); // lower time first
                        }
                    }
                });

                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}
