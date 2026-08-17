package com.vinodnarwade.eduquiz.studentactivities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vinodnarwade.eduquiz.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CustomizedQuizActivity extends AppCompatActivity {

    private Spinner spinnerSubject;
    private Spinner spinnerChapter;
    private Spinner spinnerTopic;
    private Spinner spinnerDifficulty;

    private EditText etNumberOfQuestions;

    private AppCompatButton btnGenerateQuiz;

    // Student login information
    private SharedPreferences sharedPreferences;

    private String studentClass;

    // Firebase reference
    private DatabaseReference questionBankRef;

    // Subjects available for student's class
    private final Set<String> subjects =
            new LinkedHashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Customized Quiz");

        setContentView(R.layout.activity_customized_quiz);

        initializeViews();

        setupDifficultySpinner();

        setupGenerateButton();

        // -----------------------------------------
        // Get student's class from SharedPreferences
        // -----------------------------------------

        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        studentClass =
                sharedPreferences.getString(
                        "className",
                        ""
                );

        // Check student's class
        if (studentClass.isEmpty()) {

            Toast.makeText(
                    this,
                    "Student class not found. Please login again.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // -----------------------------------------
        // Firebase QuestionBank reference
        // -----------------------------------------

        questionBankRef =
                FirebaseDatabase.getInstance()
                        .getReference("QuestionBank");

        // -----------------------------------------
        // Load subjects available for this class
        // -----------------------------------------

        loadSubjects();
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        spinnerSubject =
                findViewById(R.id.spinnerCustomSubject);

        spinnerChapter =
                findViewById(R.id.spinnerCustomChapter);

        spinnerTopic =
                findViewById(R.id.spinnerCustomTopic);

        spinnerDifficulty =
                findViewById(R.id.spinnerCustomDifficulty);

        etNumberOfQuestions =
                findViewById(R.id.etNumberOfQuestions);

        btnGenerateQuiz =
                findViewById(R.id.btnGenerateCustomQuiz);
    }

    // =========================================================
    // DIFFICULTY SPINNER
    // =========================================================

    private void setupDifficultySpinner() {

        String[] difficulties = {
                "Easy",
                "Medium",
                "Hard",
                "Customized"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        difficulties
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerDifficulty.setAdapter(adapter);
    }

    // =========================================================
    // LOAD SUBJECTS
    // =========================================================

    private void loadSubjects() {

        questionBankRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        subjects.clear();

                        /*
                         *
                         * Firebase structure:
                         *
                         * QuestionBank
                         *    |
                         *    |-- teacherId
                         *          |
                         *          |-- className
                         *                |
                         *                |-- subject
                         *
                         */

                        for (DataSnapshot teacherSnapshot :
                                snapshot.getChildren()) {

                            DataSnapshot classSnapshot =
                                    teacherSnapshot.child(studentClass);

                            // This teacher does not have
                            // a question bank for student's class
                            if (!classSnapshot.exists()) {
                                continue;
                            }

                            // Read subjects
                            for (DataSnapshot subjectSnapshot :
                                    classSnapshot.getChildren()) {

                                String subject =
                                        subjectSnapshot.getKey();

                                if (subject != null) {
                                    subjects.add(subject);
                                }
                            }
                        }

                        setupSubjectSpinner();
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Toast.makeText(
                                CustomizedQuizActivity.this,
                                "Failed to load subjects: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // =========================================================
    // SUBJECT SPINNER
    // =========================================================

    private void setupSubjectSpinner() {

        List<String> subjectList =
                new ArrayList<>();

        subjectList.add("Select Subject");

        subjectList.addAll(subjects);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        subjectList
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerSubject.setAdapter(adapter);
    }

    // =========================================================
    // GENERATE QUIZ BUTTON
    // =========================================================

    private void setupGenerateButton() {

        btnGenerateQuiz.setOnClickListener(v -> {

            String subject =
                    spinnerSubject.getSelectedItem() == null
                            ? ""
                            : spinnerSubject
                            .getSelectedItem()
                            .toString();

            String chapter =
                    spinnerChapter.getSelectedItem() == null
                            ? ""
                            : spinnerChapter
                            .getSelectedItem()
                            .toString();

            String topic =
                    spinnerTopic.getSelectedItem() == null
                            ? ""
                            : spinnerTopic
                            .getSelectedItem()
                            .toString();

            String difficulty =
                    spinnerDifficulty.getSelectedItem() == null
                            ? ""
                            : spinnerDifficulty
                            .getSelectedItem()
                            .toString();

            String numberText =
                    etNumberOfQuestions
                            .getText()
                            .toString()
                            .trim();

            // -----------------------------------------
            // Validate number of questions
            // -----------------------------------------

            if (numberText.isEmpty()) {

                Toast.makeText(
                        this,
                        "Enter number of questions",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            int numberOfQuestions;

            try {

                numberOfQuestions =
                        Integer.parseInt(numberText);

            } catch (NumberFormatException e) {

                Toast.makeText(
                        this,
                        "Enter a valid number",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (numberOfQuestions <= 0) {

                Toast.makeText(
                        this,
                        "Number of questions must be greater than 0",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // -----------------------------------------
            // Validate hierarchy selection
            // -----------------------------------------

            if (subject.equals("Select Subject")
                    || chapter.equals("Select Chapter")
                    || topic.equals("Select Topic")) {

                Toast.makeText(
                        this,
                        "Please select Subject, Chapter and Topic",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // -----------------------------------------
            // Temporary message
            // -----------------------------------------

            Toast.makeText(
                    this,
                    "Ready to generate "
                            + numberOfQuestions
                            + " questions",
                    Toast.LENGTH_SHORT
            ).show();

            /*
             * Firebase question selection
             * will be implemented next.
             */
        });
    }
}