package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class QuizSchedulerActivity extends AppCompatActivity {

    private Spinner spinnerFilterClass;
    private Spinner spinnerFilterSubject;
    private Spinner spinnerFilterChapter;
    private Spinner spinnerFilterTopic;

    private RecyclerView recyclerQuestionBanks;
    private AppCompatButton btnCreateNewQuestionBank;

    private QuestionBankAdapter adapter;

    private final List<QuestionBankModel> allQuestionBanks =
            new ArrayList<>();

    private final List<QuestionBankModel> filteredQuestionBanks =
            new ArrayList<>();

    private DatabaseReference questionBankRef;

    private String userId;

    private boolean isSettingFilters = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Question Banks");

        setContentView(R.layout.activity_quiz_scheduler);

        userId = getIntent().getStringExtra("userId");

        if (userId == null || userId.isEmpty()) {

            Toast.makeText(
                    this,
                    "User ID not found. Please login again.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        initializeViews();

        setupRecyclerView();

        questionBankRef = FirebaseDatabase.getInstance()
                .getReference("QuestionBank")
                .child(userId);

        setupFilterListeners();

        loadQuestionBanks();

        btnCreateNewQuestionBank.setOnClickListener(v -> {

            Intent intent = new Intent(
                    QuizSchedulerActivity.this,
                    CreateQuestionBankActivity.class
            );

            intent.putExtra("userId", userId);

            startActivity(intent);
        });
    }

    private void initializeViews() {

        spinnerFilterClass =
                findViewById(R.id.spinnerFilterClass);

        spinnerFilterSubject =
                findViewById(R.id.spinnerFilterSubject);

        spinnerFilterChapter =
                findViewById(R.id.spinnerFilterChapter);

        spinnerFilterTopic =
                findViewById(R.id.spinnerFilterTopic);

        recyclerQuestionBanks =
                findViewById(R.id.recyclerQuestionBanks);

        btnCreateNewQuestionBank =
                findViewById(R.id.btnCreateNewQuestionBank);
    }

    private void setupRecyclerView() {

        recyclerQuestionBanks.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new QuestionBankAdapter(
                filteredQuestionBanks,
                model -> openQuestionBank(model)
        );

        recyclerQuestionBanks.setAdapter(adapter);
    }

    private void loadQuestionBanks() {

        questionBankRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        allQuestionBanks.clear();

                        readClasses(snapshot);

                        setupClassFilter();

                        applyFilters();
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Toast.makeText(
                                QuizSchedulerActivity.this,
                                "Failed to load question banks: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void readClasses(DataSnapshot teacherSnapshot) {

        for (DataSnapshot classSnapshot :
                teacherSnapshot.getChildren()) {

            String className =
                    classSnapshot.getKey();

            if (className == null) {
                continue;
            }

            readSubjects(
                    classSnapshot,
                    className
            );
        }
    }

    private void readSubjects(
            DataSnapshot classSnapshot,
            String className) {

        for (DataSnapshot subjectSnapshot :
                classSnapshot.getChildren()) {

            String subject =
                    subjectSnapshot.getKey();

            if (subject == null) {
                continue;
            }

            readChapters(
                    subjectSnapshot,
                    className,
                    subject
            );
        }
    }

    private void readChapters(
            DataSnapshot subjectSnapshot,
            String className,
            String subject) {

        for (DataSnapshot chapterSnapshot :
                subjectSnapshot.getChildren()) {

            String chapter =
                    chapterSnapshot.getKey();

            if (chapter == null) {
                continue;
            }

            readTopics(
                    chapterSnapshot,
                    className,
                    subject,
                    chapter
            );
        }
    }

    private void readTopics(
            DataSnapshot chapterSnapshot,
            String className,
            String subject,
            String chapter) {

        for (DataSnapshot topicSnapshot :
                chapterSnapshot.getChildren()) {

            String topic =
                    topicSnapshot.getKey();

            if (topic == null) {
                continue;
            }

            int easyCount =
                    (int) topicSnapshot
                            .child("Easy")
                            .getChildrenCount();

            int mediumCount =
                    (int) topicSnapshot
                            .child("Medium")
                            .getChildrenCount();

            int hardCount =
                    (int) topicSnapshot
                            .child("Hard")
                            .getChildrenCount();

            QuestionBankModel model =
                    new QuestionBankModel(
                            userId,
                            className,
                            subject,
                            chapter,
                            topic,
                            easyCount,
                            mediumCount,
                            hardCount
                    );

            allQuestionBanks.add(model);
        }
    }

    // --------------------------------------------------
    // FILTER SETUP
    // --------------------------------------------------

    private void setupFilterListeners() {

        spinnerFilterClass.setOnItemSelectedListener(
                new SimpleItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            String selected) {

                        if (isSettingFilters) {
                            return;
                        }

                        setupSubjectFilter(selected);
                    }
                }
        );

        spinnerFilterSubject.setOnItemSelectedListener(
                new SimpleItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            String selected) {

                        if (isSettingFilters) {
                            return;
                        }

                        String selectedClass =
                                getSelectedItem(
                                        spinnerFilterClass
                                );

                        setupChapterFilter(
                                selectedClass,
                                selected
                        );
                    }
                }
        );

        spinnerFilterChapter.setOnItemSelectedListener(
                new SimpleItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            String selected) {

                        if (isSettingFilters) {
                            return;
                        }

                        String selectedClass =
                                getSelectedItem(
                                        spinnerFilterClass
                                );

                        String selectedSubject =
                                getSelectedItem(
                                        spinnerFilterSubject
                                );

                        setupTopicFilter(
                                selectedClass,
                                selectedSubject,
                                selected
                        );
                    }
                }
        );

        spinnerFilterTopic.setOnItemSelectedListener(
                new SimpleItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            String selected) {

                        if (isSettingFilters) {
                            return;
                        }

                        applyFilters();
                    }
                }
        );
    }

    // --------------------------------------------------
    // CLASS FILTER
    // --------------------------------------------------

    private void setupClassFilter() {

        Set<String> classes =
                new LinkedHashSet<>();

        for (QuestionBankModel model :
                allQuestionBanks) {

            classes.add(model.getClassName());
        }

        List<String> list =
                new ArrayList<>();

        list.add("All Classes");
        list.addAll(classes);

        setSpinner(
                spinnerFilterClass,
                list
        );

        setupSubjectFilter("All Classes");
    }

    // --------------------------------------------------
    // SUBJECT FILTER
    // --------------------------------------------------

    private void setupSubjectFilter(
            String selectedClass) {

        Set<String> subjects =
                new LinkedHashSet<>();

        for (QuestionBankModel model :
                allQuestionBanks) {

            if (selectedClass.equals("All Classes")
                    || model.getClassName()
                    .equals(selectedClass)) {

                subjects.add(model.getSubject());
            }
        }

        List<String> list =
                new ArrayList<>();

        list.add("All Subjects");
        list.addAll(subjects);

        setSpinner(
                spinnerFilterSubject,
                list
        );

        setupChapterFilter(
                selectedClass,
                "All Subjects"
        );
    }

    // --------------------------------------------------
    // CHAPTER FILTER
    // --------------------------------------------------

    private void setupChapterFilter(
            String selectedClass,
            String selectedSubject) {

        Set<String> chapters =
                new LinkedHashSet<>();

        for (QuestionBankModel model :
                allQuestionBanks) {

            boolean classMatches =
                    selectedClass.equals("All Classes")
                            || model.getClassName()
                            .equals(selectedClass);

            boolean subjectMatches =
                    selectedSubject.equals("All Subjects")
                            || model.getSubject()
                            .equals(selectedSubject);

            if (classMatches && subjectMatches) {

                chapters.add(model.getChapter());
            }
        }

        List<String> list =
                new ArrayList<>();

        list.add("All Chapters");
        list.addAll(chapters);

        setSpinner(
                spinnerFilterChapter,
                list
        );

        setupTopicFilter(
                selectedClass,
                selectedSubject,
                "All Chapters"
        );
    }

    // --------------------------------------------------
    // TOPIC FILTER
    // --------------------------------------------------

    private void setupTopicFilter(
            String selectedClass,
            String selectedSubject,
            String selectedChapter) {

        Set<String> topics =
                new LinkedHashSet<>();

        for (QuestionBankModel model :
                allQuestionBanks) {

            boolean classMatches =
                    selectedClass.equals("All Classes")
                            || model.getClassName()
                            .equals(selectedClass);

            boolean subjectMatches =
                    selectedSubject.equals("All Subjects")
                            || model.getSubject()
                            .equals(selectedSubject);

            boolean chapterMatches =
                    selectedChapter.equals("All Chapters")
                            || model.getChapter()
                            .equals(selectedChapter);

            if (classMatches
                    && subjectMatches
                    && chapterMatches) {

                topics.add(model.getTopic());
            }
        }

        List<String> list =
                new ArrayList<>();

        list.add("All Topics");
        list.addAll(topics);

        setSpinner(
                spinnerFilterTopic,
                list
        );

        applyFilters();
    }

    // --------------------------------------------------
    // APPLY FILTERS TO RECYCLERVIEW
    // --------------------------------------------------

    private void applyFilters() {

        if (spinnerFilterClass == null
                || spinnerFilterSubject == null
                || spinnerFilterChapter == null
                || spinnerFilterTopic == null) {
            return;
        }

        String selectedClass =
                getSelectedItem(spinnerFilterClass);

        String selectedSubject =
                getSelectedItem(spinnerFilterSubject);

        String selectedChapter =
                getSelectedItem(spinnerFilterChapter);

        String selectedTopic =
                getSelectedItem(spinnerFilterTopic);

        filteredQuestionBanks.clear();

        for (QuestionBankModel model :
                allQuestionBanks) {

            boolean classMatches =
                    selectedClass.equals("All Classes")
                            || model.getClassName()
                            .equals(selectedClass);

            boolean subjectMatches =
                    selectedSubject.equals("All Subjects")
                            || model.getSubject()
                            .equals(selectedSubject);

            boolean chapterMatches =
                    selectedChapter.equals("All Chapters")
                            || model.getChapter()
                            .equals(selectedChapter);

            boolean topicMatches =
                    selectedTopic.equals("All Topics")
                            || model.getTopic()
                            .equals(selectedTopic);

            if (classMatches
                    && subjectMatches
                    && chapterMatches
                    && topicMatches) {

                filteredQuestionBanks.add(model);
            }
        }

        adapter.notifyDataSetChanged();
    }

    // --------------------------------------------------
    // SPINNER HELPER
    // --------------------------------------------------

    private void setSpinner(
            Spinner spinner,
            List<String> values) {

        isSettingFilters = true;

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        values
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);

        spinner.setSelection(0);

        isSettingFilters = false;
    }

    private String getSelectedItem(
            Spinner spinner) {

        if (spinner.getSelectedItem() == null) {
            return "";
        }

        return spinner
                .getSelectedItem()
                .toString();
    }

    // --------------------------------------------------
    // OPEN QUESTION BANK
    // --------------------------------------------------

    private void openQuestionBank(
            QuestionBankModel model) {

        Intent intent = new Intent(
                QuizSchedulerActivity.this,
                QuestionBankActivity.class
        );

        intent.putExtra(
                "userId",
                model.getUserId()
        );

        intent.putExtra(
                "className",
                model.getClassName()
        );

        intent.putExtra(
                "subject",
                model.getSubject()
        );

        intent.putExtra(
                "chapter",
                model.getChapter()
        );

        intent.putExtra(
                "topic",
                model.getTopic()
        );

        startActivity(intent);
    }

    // --------------------------------------------------
    // SIMPLE SPINNER LISTENER
    // --------------------------------------------------

    private abstract static class
    SimpleItemSelectedListener
            implements android.widget.AdapterView
            .OnItemSelectedListener {

        @Override
        public void onNothingSelected(
                android.widget.AdapterView<?> parent) {
        }

        @Override
        public void onItemSelected(
                android.widget.AdapterView<?> parent,
                View view,
                int position,
                long id) {

            String selected =
                    parent.getItemAtPosition(position)
                            .toString();

            onItemSelected(selected);
        }

        public abstract void onItemSelected(
                String selected);
    }
}