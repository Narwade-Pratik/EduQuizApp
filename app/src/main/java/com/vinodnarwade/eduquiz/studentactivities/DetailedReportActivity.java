package com.vinodnarwade.eduquiz.studentactivities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vinodnarwade.eduquiz.HomeActivity;
import com.vinodnarwade.eduquiz.R;
import com.vinodnarwade.eduquiz.teacheractivities.QuestionModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailedReportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppCompatButton btnBack;
    private TextView tvEmptyState;
    private ArrayList<String> questionIds;
    private TextView tvSubject, tvChapterTopic, tvDifficulty, tvScore, tvTimeTaken;

    private int score;
    private long timeTakenMillis;
    private boolean isCustomizedQuiz;
    private String customSubject, customChapter, customTopic, customDifficulty;

    private ArrayList<QuestionModel> questionList = new ArrayList<>();
    private HashMap<String, String> selectedAnswers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_report);

        readIntentData();
        initViews();
        loadQuestionsFromQuestionBank();

        btnBack.setOnClickListener(v -> goToHome());   // ✅ CHANGED
    }

    @Override
    public void onBackPressed() {                       // ✅ NEW METHOD
        super.onBackPressed();
        goToHome();
    }

    private void goToHome() {                            // ✅ NEW METHOD
        Intent intent = new Intent(DetailedReportActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void readIntentData() {

        score = getIntent().getIntExtra("score", 0);
        timeTakenMillis = getIntent().getLongExtra("timeTakenMillis", 0);
        isCustomizedQuiz = getIntent().getBooleanExtra("isCustomizedQuiz", false);

        customSubject = getIntent().getStringExtra("customSubject");
        customChapter = getIntent().getStringExtra("customChapter");
        customTopic = getIntent().getStringExtra("customTopic");
        customDifficulty = getIntent().getStringExtra("customDifficulty");

        questionIds = getIntent().getStringArrayListExtra("questionIds");
        if (questionIds == null) {
            questionIds = new ArrayList<>();
        }

        getIntent().setExtrasClassLoader(QuestionModel.class.getClassLoader());

        ArrayList<QuestionModel> passedQuestions =
                getIntent().getParcelableArrayListExtra("questionList");

        if (passedQuestions != null) {
            questionList = passedQuestions;
        }

        @SuppressWarnings("unchecked")
        HashMap<String, String> passedAnswers =
                (HashMap<String, String>) getIntent().getSerializableExtra("selectedAnswers");

        if (passedAnswers != null) {
            selectedAnswers = passedAnswers;
        }
    }

    private void loadQuestionsFromQuestionBank() {

        if (questionIds.isEmpty()) {
            setupList();
            displaySummaryCard();   // ✅ list already available (e.g. passed directly via intent)
            return;
        }

        DatabaseReference questionBankRef =
                FirebaseDatabase.getInstance().getReference("QuestionBank");

        questionBankRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        questionList.clear();

                        for (String questionId : questionIds) {
                            QuestionModel foundQuestion = findQuestionInSnapshot(snapshot, questionId);
                            if (foundQuestion != null) {
                                questionList.add(foundQuestion);
                            }
                        }

                        setupList();
                        displaySummaryCard();   // ✅ now questionList is fully populated
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        android.widget.Toast.makeText(
                                DetailedReportActivity.this,
                                "Failed to load questions: " + error.getMessage(),
                                android.widget.Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private QuestionModel findQuestionInSnapshot(
            DataSnapshot snapshot,
            String questionId) {

        for (DataSnapshot child :
                snapshot.getChildren()) {

            if (child.getKey() != null
                    && child.getKey().equals(questionId)) {

                return child.getValue(
                        QuestionModel.class
                );
            }

            QuestionModel result =
                    findQuestionInSnapshot(
                            child,
                            questionId
                    );

            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private void displaySummaryCard() {

        if (isCustomizedQuiz) {

            tvSubject.setText("Subject: " + customSubject);
            tvChapterTopic.setText("Chapter / Topic: " + customChapter + " / " + customTopic);
            tvDifficulty.setText("Difficulty: " + customDifficulty);

        } else {

            tvSubject.setVisibility(android.view.View.GONE);
            tvChapterTopic.setVisibility(android.view.View.GONE);
            tvDifficulty.setVisibility(android.view.View.GONE);
        }

        int totalPossibleMarks = 0;

        for (QuestionModel question : questionList) {
            totalPossibleMarks += question.getMarks();
        }

        tvScore.setText("Score: " + score + "/" + totalPossibleMarks);

        long totalSeconds = timeTakenMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        tvTimeTaken.setText(
                "Time Taken: " + String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds)
        );
    }

    private void initViews() {
        tvSubject = findViewById(R.id.tvDetailedReportSubject);
        tvChapterTopic = findViewById(R.id.tvDetailedReportChapterTopic);
        tvDifficulty = findViewById(R.id.tvDetailedReportDifficulty);
        tvScore = findViewById(R.id.tvDetailedReportScore);
        tvTimeTaken = findViewById(R.id.tvDetailedReportTimeTaken);

        recyclerView = findViewById(R.id.recyclerViewDetailedReport);
        btnBack = findViewById(R.id.btnDetailedReportBack);
        tvEmptyState = findViewById(R.id.tvDetailedReportEmptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupList() {

        if (questionList.isEmpty()) {

            tvEmptyState.setVisibility(android.view.View.VISIBLE);
            recyclerView.setVisibility(android.view.View.GONE);

            return;
        }

        tvEmptyState.setVisibility(android.view.View.GONE);
        recyclerView.setVisibility(android.view.View.VISIBLE);

        List<QuestionResultModel> resultModelList = buildResultModelList();

        QuestionResultAdapter adapter =
                new QuestionResultAdapter(this, resultModelList);

        recyclerView.setAdapter(adapter);
    }

    private List<QuestionResultModel> buildResultModelList() {

        List<QuestionResultModel> resultList = new ArrayList<>();

        for (QuestionModel question : questionList) {

            String selectedKey = selectedAnswers.get(question.getQuestionId());
            String correctKey = question.getCorrectOption();

            boolean isAttempted = selectedKey != null && !selectedKey.trim().isEmpty();

            boolean isCorrect = isAttempted
                    && correctKey != null
                    && selectedKey.trim().equalsIgnoreCase(correctKey.trim());

            String yourAnswerDisplay = isAttempted ? selectedKey : "Not Attempted";

            int marksAwarded = isCorrect ? question.getMarks() : 0;

            resultList.add(
                    new QuestionResultModel(
                            question.getQuestion(),
                            question.getOptionA(),
                            question.getOptionB(),
                            question.getOptionC(),
                            question.getOptionD(),
                            yourAnswerDisplay,
                            correctKey,
                            marksAwarded
                    )
            );
        }

        return resultList;
    }
}