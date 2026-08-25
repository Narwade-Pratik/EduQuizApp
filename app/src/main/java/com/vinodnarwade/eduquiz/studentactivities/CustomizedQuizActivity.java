package com.vinodnarwade.eduquiz.studentactivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import java.util.Collections;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vinodnarwade.eduquiz.teacheractivities.QuestionModel;

import java.util.HashMap;
import java.util.ArrayList;
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

    private String studentUserId;
    // Customized quiz variables
    private boolean isCustomizedQuiz;
    private ArrayList<QuestionModel> customQuestions;

    private String customQuizId;
    private String customSubject;
    private String customChapter;
    private String customTopic;
    private String customDifficulty;

    private final ArrayList<QuestionModel> availableQuestions =
            new ArrayList<>();

    private final ArrayList<QuestionModel> selectedQuestions =
            new ArrayList<>();

    private final ArrayList<QuestionModel> easyPool = new ArrayList<>();
    private final ArrayList<QuestionModel> mediumPool = new ArrayList<>();
    private final ArrayList<QuestionModel> hardPool = new ArrayList<>();

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

        loadStudentDetails();

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

    private void loadStudentDetails() {

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        studentUserId =
                preferences.getString("userId", "");

        studentClass =
                preferences.getString("className", "");

        if (studentUserId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Student ID not found. Please login again.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        if (studentClass.isEmpty()) {

            Toast.makeText(
                    this,
                    "Student class not found.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
        }
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

        subjectList.add("All Subject");
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

                        if (selectedSubject.equals("All Subject")) {

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

        chapterList.add("All Chapter");
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

                        if (selectedChapter.equals("All Chapter")) {

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

        topicList.add("All Topic");
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

        list.add("All Chapter");

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

        list.add("All Topic");

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

            if (subject.equals("All Subject")
                    || chapter.equals("All Chapter")
                    || topic.equals("All Topic")) {

                Toast.makeText(
                        this,
                        "Please Select Subject, Chapter and Topic",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (difficulty.equals("Customized")) {

                fetchAllDifficultiesFromQuestionBank(
                        subject,
                        chapter,
                        topic,
                        numberOfQuestions
                );

            } else {

                fetchQuestionsFromQuestionBank(
                        subject,
                        chapter,
                        topic,
                        difficulty,
                        numberOfQuestions
                );
            }
        });
    }

    private void fetchQuestionsFromQuestionBank(
            String subject,
            String chapter,
            String topic,
            String difficulty,
            int numberOfQuestions) {

        DatabaseReference questionBankRef =
                FirebaseDatabase.getInstance()
                        .getReference("QuestionBank");

        questionBankRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        availableQuestions.clear();

                        /*
                         * QuestionBank
                         *     ├── Teacher 1
                         *     ├── Teacher 2
                         *     └── Teacher 3
                         */

                        for (DataSnapshot teacherSnapshot :
                                snapshot.getChildren()) {

                            DataSnapshot classSnapshot =
                                    teacherSnapshot.child(studentClass);

                            if (!classSnapshot.exists()) {
                                continue;
                            }

                            DataSnapshot subjectSnapshot =
                                    classSnapshot.child(subject);

                            if (!subjectSnapshot.exists()) {
                                continue;
                            }

                            DataSnapshot chapterSnapshot =
                                    subjectSnapshot.child(chapter);

                            if (!chapterSnapshot.exists()) {
                                continue;
                            }

                            DataSnapshot topicSnapshot =
                                    chapterSnapshot.child(topic);

                            if (!topicSnapshot.exists()) {
                                continue;
                            }

                            DataSnapshot difficultySnapshot =
                                    topicSnapshot.child(difficulty);

                            if (!difficultySnapshot.exists()) {
                                continue;
                            }

                            for (DataSnapshot questionSnapshot :
                                    difficultySnapshot.getChildren()) {

                                QuestionModel question =
                                        questionSnapshot
                                                .getValue(QuestionModel.class);

                                if (question != null) {

                                    availableQuestions.add(question);
                                }
                            }
                        }

                        handleFetchedQuestions(
                                subject,
                                chapter,
                                topic,
                                difficulty,
                                numberOfQuestions
                        );
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Toast.makeText(
                                CustomizedQuizActivity.this,
                                "Failed to load questions: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // =========================================================
// ADAPTIVE QUIZ — FETCH ALL DIFFICULTIES
// =========================================================

    private void fetchAllDifficultiesFromQuestionBank(
            String subject,
            String chapter,
            String topic,
            int numberOfQuestions) {

        DatabaseReference questionBankRef =
                FirebaseDatabase.getInstance()
                        .getReference("QuestionBank");

        questionBankRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        easyPool.clear();
                        mediumPool.clear();
                        hardPool.clear();

                        for (DataSnapshot teacherSnapshot :
                                snapshot.getChildren()) {

                            DataSnapshot classSnapshot =
                                    teacherSnapshot.child(studentClass);

                            if (!classSnapshot.exists()) {
                                continue;
                            }

                            DataSnapshot subjectSnapshot =
                                    classSnapshot.child(subject);

                            if (!subjectSnapshot.exists()) {
                                continue;
                            }

                            DataSnapshot chapterSnapshot =
                                    subjectSnapshot.child(chapter);

                            if (!chapterSnapshot.exists()) {
                                continue;
                            }

                            DataSnapshot topicSnapshot =
                                    chapterSnapshot.child(topic);

                            if (!topicSnapshot.exists()) {
                                continue;
                            }

                            for (DataSnapshot difficultySnapshot :
                                    topicSnapshot.getChildren()) {

                                String difficultyKey =
                                        difficultySnapshot.getKey();

                                if (difficultyKey == null) {
                                    continue;
                                }

                                ArrayList<QuestionModel> targetPool;

                                switch (difficultyKey) {

                                    case "Easy":
                                        targetPool = easyPool;
                                        break;

                                    case "Medium":
                                        targetPool = mediumPool;
                                        break;

                                    case "Hard":
                                        targetPool = hardPool;
                                        break;

                                    default:
                                        continue;
                                }

                                for (DataSnapshot questionSnapshot :
                                        difficultySnapshot.getChildren()) {

                                    QuestionModel question =
                                            questionSnapshot
                                                    .getValue(QuestionModel.class);

                                    if (question != null) {
                                        targetPool.add(question);
                                    }
                                }
                            }
                        }

                        handleAdaptiveQuizGeneration(
                                subject,
                                chapter,
                                topic,
                                numberOfQuestions
                        );
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Toast.makeText(
                                CustomizedQuizActivity.this,
                                "Failed to load questions: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


// =========================================================
// ADAPTIVE QUIZ — BUILD CALIBRATION BATCH + LAUNCH
// =========================================================

    private void handleAdaptiveQuizGeneration(
            String subject,
            String chapter,
            String topic,
            int numberOfQuestions) {

        int totalAvailable =
                easyPool.size() + mediumPool.size() + hardPool.size();

        if (totalAvailable == 0) {

            Toast.makeText(
                    this,
                    "No questions available for the selected options.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (totalAvailable < numberOfQuestions) {

            Toast.makeText(
                    this,
                    "Only " + totalAvailable +
                            " questions are available across all difficulties.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Collections.shuffle(easyPool);
        Collections.shuffle(mediumPool);
        Collections.shuffle(hardPool);

        // Build a combined pool for calibration (mixed difficulty)
        ArrayList<QuestionModel> combinedPool = new ArrayList<>();
        combinedPool.addAll(easyPool);
        combinedPool.addAll(mediumPool);
        combinedPool.addAll(hardPool);
        Collections.shuffle(combinedPool);

        int calibrationCount =
                (int) Math.ceil(0.25 * numberOfQuestions);

        if (calibrationCount > numberOfQuestions) {
            calibrationCount = numberOfQuestions;
        }

        ArrayList<QuestionModel> calibrationQuestions =
                new ArrayList<>(
                        combinedPool.subList(0, calibrationCount)
                );

        // Remove calibration questions from the individual pools
        // so they aren't reused in adaptive batches later
        Set<String> usedIds = new LinkedHashSet<>();

        for (QuestionModel q : calibrationQuestions) {
            if (q.getQuestionId() != null) {
                usedIds.add(q.getQuestionId());
            }
        }

        removeUsedQuestions(easyPool, usedIds);
        removeUsedQuestions(mediumPool, usedIds);
        removeUsedQuestions(hardPool, usedIds);

        launchAdaptiveAttemptActivity(
                subject,
                chapter,
                topic,
                numberOfQuestions,
                calibrationQuestions
        );
    }


    private void removeUsedQuestions(
            ArrayList<QuestionModel> pool,
            Set<String> usedIds) {

        ArrayList<QuestionModel> toRemove = new ArrayList<>();

        for (QuestionModel q : pool) {

            if (q.getQuestionId() != null
                    && usedIds.contains(q.getQuestionId())) {

                toRemove.add(q);
            }
        }

        pool.removeAll(toRemove);
    }


// =========================================================
// ADAPTIVE QUIZ — LAUNCH ATTEMPT ACTIVITY
// =========================================================

    private void launchAdaptiveAttemptActivity(
            String subject,
            String chapter,
            String topic,
            int numberOfQuestions,
            ArrayList<QuestionModel> calibrationQuestions) {

        String customQuizId =
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("Users")
                        .child(studentUserId)
                        .child("CustomQuizzes")
                        .push()
                        .getKey();

        if (customQuizId == null) {

            Toast.makeText(
                    this,
                    "Failed to create quiz ID.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Intent intent =
                new Intent(
                        CustomizedQuizActivity.this,
                        AttemptQuizActivity.class
                );

        intent.putExtra("isCustomizedQuiz", true);
        intent.putExtra("customQuizId", customQuizId);
        intent.putExtra("customSubject", subject);
        intent.putExtra("customChapter", chapter);
        intent.putExtra("customTopic", topic);
        intent.putExtra("customDifficulty", "Customized");
        intent.putExtra("customNumberOfQuestions", numberOfQuestions);

        // Adaptive-specific extras
        intent.putExtra("isAdaptiveQuiz", true);
        intent.putExtra("adaptiveTotalQuestions", numberOfQuestions);

        intent.putParcelableArrayListExtra(
                "adaptiveCalibrationQuestions",
                calibrationQuestions
        );

        intent.putParcelableArrayListExtra(
                "adaptiveEasyPool",
                easyPool
        );

        intent.putParcelableArrayListExtra(
                "adaptiveMediumPool",
                mediumPool
        );

        intent.putParcelableArrayListExtra(
                "adaptiveHardPool",
                hardPool
        );

        // customQuestions still needed for non-adaptive code paths
        // in AttemptQuizActivity that check its emptiness — pass calibration
        // batch as the initial visible questionList
        intent.putParcelableArrayListExtra(
                "customQuestions",
                calibrationQuestions
        );

        startActivity(intent);
    }

    private void handleFetchedQuestions(
            String subject,
            String chapter,
            String topic,
            String difficulty,
            int numberOfQuestions) {

        int availableCount =
                availableQuestions.size();

        if (availableCount == 0) {

            Toast.makeText(
                    this,
                    "No questions available for the selected options.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (availableCount < numberOfQuestions) {

            Toast.makeText(
                    this,
                    "Only " + availableCount +
                            " questions are available.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        selectedQuestions.clear();

        ArrayList<QuestionModel> shuffledQuestions =
                new ArrayList<>(availableQuestions);

        Collections.shuffle(shuffledQuestions);

        selectedQuestions.addAll(
                shuffledQuestions.subList(
                        0,
                        numberOfQuestions
                )
        );

        String customQuizId =
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("Users")
                        .child(studentUserId)
                        .child("CustomQuizzes")
                        .push()
                        .getKey();

        if (customQuizId == null) {

            Toast.makeText(
                    this,
                    "Failed to create quiz ID.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Intent intent =
                new Intent(
                        CustomizedQuizActivity.this,
                        AttemptQuizActivity.class
                );

        intent.putExtra(
                "isCustomizedQuiz",
                true
        );

        intent.putExtra(
                "customQuizId",
                customQuizId
        );

        intent.putExtra(
                "customSubject",
                subject
        );

        intent.putExtra(
                "customChapter",
                chapter
        );

        intent.putExtra(
                "customTopic",
                topic
        );

        intent.putExtra(
                "customDifficulty",
                difficulty
        );

        intent.putExtra(
                "customNumberOfQuestions",
                numberOfQuestions
        );

        intent.putParcelableArrayListExtra(
                "customQuestions",
                selectedQuestions
        );

        startActivity(intent);
    }
}