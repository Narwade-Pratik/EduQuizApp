package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import com.vinodnarwade.eduquiz.R;

public class CreateQuizActivity extends AppCompatActivity {

    EditText quizTitle,noOfQue,subjectName;
    Button createQuiz;
    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference quizRef;
    SharedPreferences sharedPreferences;
    String userId,userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);
        quizTitle = findViewById(R.id.etcreatequizquiztitile);
        noOfQue = findViewById(R.id.etcreatequiznoofquestions);
        subjectName = findViewById(R.id.etcreatequizsubjectname);
        createQuiz = findViewById(R.id.btncreatequizcreatequiz);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        quizRef = database.getReference("Quizzes");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = sharedPreferences.getString("userId", null);
        userName = sharedPreferences.getString("userName", null);

        createQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = quizTitle.getText().toString();
                char[] digitsArray = noOfQue.getText().toString().toCharArray();
                String subjectNameIs = subjectName.getText().toString();

                if(title.isEmpty()){
                    quizTitle.setError("Please enter a quiz title");
                }
                else if(!checkIsAllDigit(digitsArray)){
                    noOfQue.setError("Enter valid Number");
                }
                else if(subjectNameIs.isEmpty()){
                    subjectName.setError("Enter Subject Name");
                }
                else{
                    //String currentUID = auth.getCurrentUser().getUid();
                    String quizID = quizRef.push().getKey();
                    int noOfQ = Integer.parseInt(noOfQue.getText().toString().trim());
                    if (quizID == null) {
                        Toast.makeText(CreateQuizActivity.this, "Missing quiz ID!", Toast.LENGTH_SHORT).show();
                        return; // Prevent further crash
                    }
                    if (userId == null) {
                        Toast.makeText(CreateQuizActivity.this, "Missig user Id!", Toast.LENGTH_SHORT).show();
                        return; // Prevent further crash
                    }
                    if (noOfQ == 0) {
                        Toast.makeText(CreateQuizActivity.this, "Missing noOfQ!", Toast.LENGTH_SHORT).show();
                        return; // Prevent further crash
                    }

                    QuizModel quiz = new QuizModel(quizID, title, subjectNameIs, noOfQ, userId);
                    quizRef.child(quizID).setValue(quiz).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateQuizActivity.this, "Abb "+ " * " +userId+" * "+userName, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreateQuizActivity.this, AddQuestionActivity.class);
                            intent.putExtra("quizID", quizID);
                            intent.putExtra("noOfQuestions", noOfQ);
                            intent.putExtra("teacherUID", userId);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(CreateQuizActivity.this, "Failed to create quiz", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public boolean checkIsAllDigit(char[] arr){
        for(int i=0;i<arr.length;i++){
            if(!Character.isDigit(arr[i])){
                return false;
            }
        }
        return true;
    }
}