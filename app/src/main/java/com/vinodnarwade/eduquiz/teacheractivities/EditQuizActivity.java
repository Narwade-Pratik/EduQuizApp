package com.vinodnarwade.eduquiz.teacheractivities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.vinodnarwade.eduquiz.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditQuizActivity extends AppCompatActivity {

    EditText etQuestionTopic, etQuestion, etOptionA, etOptionB, etOptionC, etOptionD, etCorrectOption, etMarks, etDifficulty;
    Button btnNext, btnPrevious, btnSubmitAll,btnDeleteQuestion,btnAddQuestion,btnUpdateQuestion;
    ArrayList<QuestionModel> questionList;
    int currentIndex = 0;
    String quizId, userId;
    DatabaseReference questionRef,quizRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_quiz);  // same layout as ReviewQuiz

        // 1️⃣ Initialize Views
        etQuestionTopic = findViewById(R.id.etaddquestionquestiontopic);
        etQuestion = findViewById(R.id.eteditquestionquestion);
        etOptionA = findViewById(R.id.eteditquestionoptiona);
        etOptionB = findViewById(R.id.eteditquestionoptionb);
        etOptionC = findViewById(R.id.eteditquestionoptionc);
        etOptionD = findViewById(R.id.eteditquestionoptiond);
        etCorrectOption = findViewById(R.id.eteditquestioncorrectoption);
        etMarks = findViewById(R.id.eteditquestionmarks);
        etDifficulty = findViewById(R.id.eteditquestiondifficulty);

        btnNext = findViewById(R.id.btneditquestionnext);
        btnPrevious = findViewById(R.id.btneditquestionprevious);
        btnSubmitAll = findViewById(R.id.btneditquestionsubmitquiz);
        btnDeleteQuestion = findViewById(R.id.btneditquestiondelete);
        btnAddQuestion = findViewById(R.id.btneditquestionadd);
        btnUpdateQuestion = findViewById(R.id.btneditquestionupdate);


        // 2️⃣ Get Intent Data
        quizId = getIntent().getStringExtra("quizId");
        userId = getIntent().getStringExtra("userId");
        questionList = new ArrayList<>();

        // 3️⃣ Setup Firebase Reference
        questionRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("Quizzes")
                .child(quizId)
                .child("Questions");

        quizRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("Quizzes");

        // 4️⃣ Fetch questions from Firebase
        fetchQuestionsFromFirebase();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex < questionList.size() - 1) {
                    currentIndex++;
                    showQuestion(currentIndex);
                } else {
                    Toast.makeText(EditQuizActivity.this, "Last Question", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex > 0) {
                    currentIndex--;
                    showQuestion(currentIndex);
                } else {
                    Toast.makeText(EditQuizActivity.this, "First Question", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDeleteQuestion.setOnClickListener(v -> {
            if (questionList.isEmpty()) {
                Toast.makeText(this, "No question to delete", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Delete Question")
                    .setMessage("Are you sure you want to delete this question?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteCurrentQuestion();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        btnUpdateQuestion.setOnClickListener(v -> {
            if (questionList.isEmpty()) {
                Toast.makeText(this, "No question to update", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get current question
            QuestionModel currentQuestion = questionList.get(currentIndex);
            String questionId = currentQuestion.getQuestionId();

            // Get updated data from UI
            String updatedQuestionTopic = etQuestionTopic.getText().toString().trim();
            String updatedQuestion = etQuestion.getText().toString().trim();
            String updatedA = etOptionA.getText().toString().trim();
            String updatedB = etOptionB.getText().toString().trim();
            String updatedC = etOptionC.getText().toString().trim();
            String updatedD = etOptionD.getText().toString().trim();
            String updatedCorrect = etCorrectOption.getText().toString().trim();
            String updatedMarks = etMarks.getText().toString().trim();
            String updatedDifficulty = etDifficulty.getText().toString().trim();

            if (updatedQuestionTopic.isEmpty() || updatedQuestion.isEmpty() || updatedA.isEmpty() || updatedB.isEmpty() ||
                    updatedC.isEmpty() || updatedD.isEmpty() || updatedCorrect.isEmpty() || updatedMarks.isEmpty() || updatedDifficulty.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create updated question map
            Map<String, Object> updatedMap = new HashMap<>();
            updatedMap.put("questionTopic", updatedQuestionTopic);
            updatedMap.put("question", updatedQuestion);
            updatedMap.put("optionA", updatedA);
            updatedMap.put("optionB", updatedB);
            updatedMap.put("optionC", updatedC);
            updatedMap.put("optionD", updatedD);
            updatedMap.put("correctOption", updatedCorrect);
            updatedMap.put("marks", Integer.parseInt(updatedMarks));
            updatedMap.put("difficulty", updatedDifficulty);



            // Update in Firebase
            DatabaseReference questionRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(userId)
                    .child("Quizzes")
                    .child(quizId)
                    .child("Questions")
                    .child(questionId);

            questionRef.updateChildren(updatedMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Update local list also
                    currentQuestion.setQuestionTopic(updatedQuestionTopic);
                    currentQuestion.setQuestion(updatedQuestion);
                    currentQuestion.setOptionA(updatedA);
                    currentQuestion.setOptionB(updatedB);
                    currentQuestion.setOptionC(updatedC);
                    currentQuestion.setOptionD(updatedD);
                    currentQuestion.setCorrectOption(updatedCorrect);
                    currentQuestion.setMarks(Integer.parseInt(updatedMarks));

                    Toast.makeText(this, "Question updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to update question", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnAddQuestion.setOnClickListener(v -> {
            String questionTopic = etQuestionTopic.getText().toString().trim();
            String question = etQuestion.getText().toString().trim();
            String optionA = etOptionA.getText().toString().trim();
            String optionB = etOptionB.getText().toString().trim();
            String optionC = etOptionC.getText().toString().trim();
            String optionD = etOptionD.getText().toString().trim();
            String correctOption = etCorrectOption.getText().toString().trim();
            String marks = etMarks.getText().toString().trim();
            String difficulty = etDifficulty.getText().toString().trim();

            if (questionTopic.isEmpty() ||question.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty()
                    || optionD.isEmpty() || correctOption.isEmpty() || marks.isEmpty() || difficulty.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference questionsRef = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(userId)
                    .child("Quizzes")
                    .child(quizId)
                    .child("Questions");

            //int newIndex = questionList.size() + 1; // Q1, Q2, ..., Qn+1
            String newQuestionId = questionsRef.push().getKey();
            QuestionModel newQuestion = new QuestionModel(newQuestionId,quizId,questionTopic,question, optionA, optionB, optionC, optionD, correctOption, Integer.parseInt(marks), difficulty);

            questionRef.child(newQuestionId).setValue(newQuestion)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Question added", Toast.LENGTH_SHORT).show();
                        if(currentIndex<questionList.size()-1){
                            questionList.add(currentIndex+1,newQuestion);
                        }
                        else{
                            questionList.add(newQuestion);
                        }
                        currentIndex++;
                        showQuestion(currentIndex);
                        clearFields();

                        // Update totalQuestions count
                        quizRef.child(quizId).child("numberOfQuestions").setValue(questionList.size());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to add question", Toast.LENGTH_SHORT).show();
                    });
        });

        btnSubmitAll.setOnClickListener(v -> {
            if (questionList.isEmpty()) {
                Toast.makeText(this, "No questions to save", Toast.LENGTH_SHORT).show();
                return;
            }

            // Optional: Save current visible question edits before bulk save
            saveCurrentQuestion();

            // Reference to questions node
            DatabaseReference questionsRef = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(userId)
                    .child("Quizzes")
                    .child(quizId)
                    .child("Questions");

            // Keep track of success/fail
            final int totalQuestions = questionList.size();
            final int[] successCount = {0};
            final int[] failCount = {0};

            for (QuestionModel question : questionList) {
                String qId = question.getQuestionId();
                if (qId == null) continue; // just in case

                // Update each question node
                questionsRef.child(qId).setValue(question)
                        .addOnSuccessListener(aVoid -> {
                            successCount[0]++;
                            if (successCount[0] + failCount[0] == totalQuestions) {
                                Toast.makeText(this, "All changes saved successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            failCount[0]++;
                            if (successCount[0] + failCount[0] == totalQuestions) {
                                Toast.makeText(this, "Failed to save some changes", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void fetchQuestionsFromFirebase() {
        questionRef.get().addOnSuccessListener(snapshot -> {
            questionList.clear();
            for (DataSnapshot snap : snapshot.getChildren()) {
                QuestionModel model = snap.getValue(QuestionModel.class);
                model.setQuestionId(snap.getKey());
                questionList.add(model);
            }

            if (!questionList.isEmpty()) {
                currentIndex = 0;
                showQuestion(currentIndex);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load questions", Toast.LENGTH_SHORT).show();
        });
    }


    private void showQuestion(int index) {
        QuestionModel question = questionList.get(index);
        etQuestionTopic.setText(question.getQuestionTopic());
        etQuestion.setText(question.getQuestion());
        etOptionA.setText(question.getOptionA());
        etOptionB.setText(question.getOptionB());
        etOptionC.setText(question.getOptionC());
        etOptionD.setText(question.getOptionD());
        etCorrectOption.setText(question.getCorrectOption());
        etMarks.setText(String.valueOf(question.getMarks()));
        etDifficulty.setText(question.getDifficulty());
        if (index == questionList.size() - 1) {
            btnSubmitAll.setVisibility(View.VISIBLE);
        } else {
            btnSubmitAll.setVisibility(View.GONE);
        }
    }

    private void saveCurrentQuestion() {
        QuestionModel model = questionList.get(currentIndex);
        model.setQuestionTopic(etQuestionTopic.getText().toString());
        model.setQuestion(etQuestion.getText().toString());
        model.setOptionA(etOptionA.getText().toString());
        model.setOptionB(etOptionB.getText().toString());
        model.setOptionC(etOptionC.getText().toString());
        model.setOptionD(etOptionD.getText().toString());
        model.setCorrectOption(etCorrectOption.getText().toString());
        model.setMarks(Integer.parseInt(etMarks.getText().toString().trim()));
        model.setDifficulty(etDifficulty.getText().toString());
    }

    private void deleteCurrentQuestion() {
        if (questionList.isEmpty()) {
            Toast.makeText(this, "No questions to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        QuestionModel questionToDelete = questionList.get(currentIndex);
        String questionId = questionToDelete.getQuestionId();

        DatabaseReference questionRef = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userId)
                .child("Quizzes")
                .child(quizId)
                .child("Questions")
                .child(questionId);

        questionRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Remove from local list
                questionList.remove(currentIndex);

                // Update totalQuestions in DB
                DatabaseReference totalQRef = FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(userId)
                        .child("Quizzes")
                        .child(quizId)
                        .child("numberOfQuestions");

                totalQRef.setValue(questionList.size());

                Toast.makeText(this, "Question deleted", Toast.LENGTH_SHORT).show();

                // Adjust index and show next question or clear fields
                if (questionList.isEmpty()) {
                    currentIndex = -1;
                    clearFields();
                    Toast.makeText(this, "No more questions", Toast.LENGTH_SHORT).show();
                } else {
                    if (currentIndex >= questionList.size()) {
                        currentIndex = questionList.size() - 1;
                    }
                    showQuestion(currentIndex);
                }
                //have doubt >= or <=
            } else {
                Toast.makeText(this, "Failed to delete question", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void clearFields() {
        etQuestionTopic.setText("");
        etQuestion.setText("");
        etOptionA.setText("");
        etOptionB.setText("");
        etOptionC.setText("");
        etOptionD.setText("");
        etCorrectOption.setText("");
        etMarks.setText("");
        etDifficulty.setText("");
    }
}
