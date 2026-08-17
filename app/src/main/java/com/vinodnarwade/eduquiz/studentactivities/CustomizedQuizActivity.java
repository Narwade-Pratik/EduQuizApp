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

    private SharedPreferences sharedPreferences;

    private String studentClass;

    private DatabaseReference questionBankRef;

    private final Set<String> subjects =
            new LinkedHashSet<>();

    private final Set<String> chapters =
            new LinkedHashSet<>();

    private final Set<String> topics =
            new LinkedHashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Customized Quiz");

        setContentView(R.layout.activity_customized_quiz);

        initializeViews();

        setupDifficultySpinner();

        setupGenerateButton();

        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        studentClass =
                sharedPreferences.getString(
                        "className",
                        ""
                );

        if (studentClass.isEmpty()) {

            Toast.makeText(
                    this,
                    "Student class not found. Please login again.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        questionBankRef =
                FirebaseDatabase.getInstance()
                        .getReference("QuestionBank");

        loadSubjects();

        setupSubjectListener();

        setupChapterListener();
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
    // DIFFICULTY
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

                        for (DataSnapshot teacherSnapshot :
                                snapshot.getChildren()) {

                            DataSnapshot classSnapshot =
                                    teacherSnapshot.child(studentClass);

                            if (!classSnapshot.exists()) {
                                continue;
                            }

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
    // SUBJECT LISTENER
    // =========================================================

    private void setupSubjectListener() {

        spinnerSubject.setOnItemSelectedListener(
                new android.widget.AdapterView
                        .OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id) {

                        String selectedSubject =
                                parent.getItemAtPosition(position)
                                        .toString();

                        if (selectedSubject.equals("Select Subject")) {

                            clearChapterSpinner();
                            clearTopicSpinner();

                            return;
                        }

                        loadChapters(selectedSubject);
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {
                    }
                }
        );
    }

    // =========================================================
    // LOAD CHAPTERS
    // =========================================================

    private void loadChapters(
            String selectedSubject) {

        chapters.clear();

        questionBankRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        for (DataSnapshot teacherSnapshot :
                                snapshot.getChildren()) {

                            DataSnapshot subjectSnapshot =
                                    teacherSnapshot
                                            .child(studentClass)
                                            .child(selectedSubject);

                            if (!subjectSnapshot.exists()) {
                                continue;
                            }

                            for (DataSnapshot chapterSnapshot :
                                    subjectSnapshot.getChildren()) {

                                String chapter =
                                        chapterSnapshot.getKey();

                                if (chapter != null) {
                                    chapters.add(chapter);
                                }
                            }
                        }

                        setupChapterSpinner();
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Toast.makeText(
                                CustomizedQuizActivity.this,
                                "Failed to load chapters: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // =========================================================
    // CHAPTER SPINNER
    // =========================================================

    private void setupChapterSpinner() {

        List<String> chapterList =
                new ArrayList<>();

        chapterList.add("Select Chapter");
        chapterList.addAll(chapters);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        chapterList
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerChapter.setAdapter(adapter);

        clearTopicSpinner();
    }

    // =========================================================
    // CHAPTER LISTENER
    // =========================================================

    private void setupChapterListener() {

        spinnerChapter.setOnItemSelectedListener(
                new android.widget.AdapterView
                        .OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id) {

                        String selectedChapter =
                                parent.getItemAtPosition(position)
                                        .toString();

                        if (selectedChapter.equals("Select Chapter")) {

                            clearTopicSpinner();

                            return;
                        }

                        String selectedSubject =
                                spinnerSubject
                                        .getSelectedItem()
                                        .toString();

                        loadTopics(
                                selectedSubject,
                                selectedChapter
                        );
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {
                    }
                }
        );
    }

    // =========================================================
    // LOAD TOPICS
    // =========================================================

    private void loadTopics(
            String selectedSubject,
            String selectedChapter) {

        topics.clear();

        questionBankRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        for (DataSnapshot teacherSnapshot :
                                snapshot.getChildren()) {

                            DataSnapshot chapterSnapshot =
                                    teacherSnapshot
                                            .child(studentClass)
                                            .child(selectedSubject)
                                            .child(selectedChapter);

                            if (!chapterSnapshot.exists()) {
                                continue;
                            }

                            for (DataSnapshot topicSnapshot :
                                    chapterSnapshot.getChildren()) {

                                String topic =
                                        topicSnapshot.getKey();

                                if (topic != null) {
                                    topics.add(topic);
                                }
                            }
                        }

                        setupTopicSpinner();
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Toast.makeText(
                                CustomizedQuizActivity.this,
                                "Failed to load topics: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // =========================================================
    // TOPIC SPINNER
    // =========================================================

    private void setupTopicSpinner() {

        List<String> topicList =
                new ArrayList<>();

        topicList.add("Select Topic");
        topicList.addAll(topics);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        topicList
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerTopic.setAdapter(adapter);
    }

    // =========================================================
    // CLEAR CHAPTER
    // =========================================================

    private void clearChapterSpinner() {

        List<String> list =
                new ArrayList<>();

        list.add("Select Chapter");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        list
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerChapter.setAdapter(adapter);
    }

    // =========================================================
    // CLEAR TOPIC
    // =========================================================

    private void clearTopicSpinner() {

        List<String> list =
                new ArrayList<>();

        list.add("Select Topic");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        list
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerTopic.setAdapter(adapter);
    }

    // =========================================================
    // GENERATE QUIZ
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

            Toast.makeText(
                    this,
                    "Ready to generate "
                            + numberOfQuestions
                            + " questions",
                    Toast.LENGTH_SHORT
            ).show();

            // Question selection will be implemented next.
        });
    }
}