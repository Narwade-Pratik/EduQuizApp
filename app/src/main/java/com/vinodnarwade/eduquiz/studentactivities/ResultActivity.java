package com.vinodnarwade.eduquiz.studentactivities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.vinodnarwade.eduquiz.R;
import com.vinodnarwade.eduquiz.teacheractivities.QuestionModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Shared result SUMMARY screen for BOTH:
 *   - Teacher-created quizzes
 *   - Customized quizzes
 *
 * Shows: total questions, attempted, skipped, correct, wrong, score,
 * percentage, and time taken.
 *
 * The "View Detailed Report" button is wired but intentionally left as a
 * placeholder for now — the question-by-question breakdown screen will be
 * built in a later pass. questionList / selectedAnswers are still received
 * here so that screen can be added without touching AttemptQuizActivity again.
 *
 * Expected intent extras (set by AttemptQuizActivity):
 *   - score                (int)
 *   - numberOfQuestions    (int)
 *   - timeTakenMillis      (long)
 *   - isCustomizedQuiz     (boolean)
 *   - customQuizId         (String, only relevant when isCustomizedQuiz = true)
 *   - questionList         (ArrayList<QuestionModel>, Parcelable)
 *   - selectedAnswers      (HashMap<String, String>, Serializable)
 */
public class ResultActivity extends AppCompatActivity {

    // =========================================================
    // VIEWS
    // =========================================================

    private TextView tvQuizType;
    private TextView tvScore;
    private TextView tvTotalQuestions;
    private TextView tvAttempted;
    private TextView tvSkipped;
    private TextView tvCorrect;
    private TextView tvWrong;
    private TextView tvPercentage;
    private TextView tvTimeTaken;
    private String customSubject;
    private String customChapter;
    private String customTopic;
    private String customDifficulty;

    private AppCompatButton btnViewDetailedReport;
    private AppCompatButton btnDone;

    // =========================================================
    // DATA
    // =========================================================

    private int score;
    private int numberOfQuestions;
    private long timeTakenMillis;
    private boolean isCustomizedQuiz;
    private String customQuizId;

    private ArrayList<QuestionModel> questionList = new ArrayList<>();
    private HashMap<String, String> selectedAnswers = new HashMap<>();


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result);

        readIntentData();
        initViews();
        displaySummary();

        btnDone.setOnClickListener(v -> finish());

        btnViewDetailedReport.setOnClickListener(v -> openDetailedReport());
    }


    // =========================================================
    // READ INTENT DATA
    // =========================================================

    private void readIntentData() {

        // Needed so the system can unmarshal the QuestionModel
        // Parcelable ArrayList correctly.
        getIntent().setExtrasClassLoader(QuestionModel.class.getClassLoader());

        customSubject = getIntent().getStringExtra("customSubject");
        customChapter = getIntent().getStringExtra("customChapter");
        customTopic = getIntent().getStringExtra("customTopic");
        customDifficulty = getIntent().getStringExtra("customDifficulty");

        score = getIntent().getIntExtra("score", 0);

        numberOfQuestions = getIntent().getIntExtra("numberOfQuestions", 0);

        timeTakenMillis = getIntent().getLongExtra("timeTakenMillis", 0);

        isCustomizedQuiz = getIntent().getBooleanExtra("isCustomizedQuiz", false);

        customQuizId = getIntent().getStringExtra("customQuizId");

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


    // =========================================================
    // INIT VIEWS
    // =========================================================

    private void initViews() {

        tvQuizType = findViewById(R.id.tvResultQuizType);

        tvScore = findViewById(R.id.tvResultScore);
        tvTotalQuestions = findViewById(R.id.tvResultTotalQuestions);
        tvAttempted = findViewById(R.id.tvResultAttempted);
        tvSkipped = findViewById(R.id.tvResultSkipped);
        tvCorrect = findViewById(R.id.tvResultCorrect);
        tvWrong = findViewById(R.id.tvResultWrong);
        tvPercentage = findViewById(R.id.tvResultPercentage);
        tvTimeTaken = findViewById(R.id.tvResultTimeTaken);

        btnViewDetailedReport = findViewById(R.id.btnResultViewDetailedReport);
        btnDone = findViewById(R.id.btnResultDone);
    }


    // =========================================================
    // DISPLAY SUMMARY
    // =========================================================

    private void displaySummary() {

        tvQuizType.setText(
                isCustomizedQuiz ? "Customized Quiz" : "Quiz"
        );

        int correctCount = 0;
        int wrongCount = 0;
        int skippedCount = 0;
        int totalPossibleMarks = 0;

        for (QuestionModel question : questionList) {

            totalPossibleMarks += question.getMarks();

            String selected = selectedAnswers.get(question.getQuestionId());
            String correct = question.getCorrectOption();

            if (selected == null || selected.trim().isEmpty()) {

                skippedCount++;

            } else if (correct != null
                    && selected.trim().equalsIgnoreCase(correct.trim())) {

                correctCount++;

            } else {

                wrongCount++;
            }
        }

        int attemptedCount = numberOfQuestions - skippedCount;

        double percentage = 0;

        if (totalPossibleMarks > 0) {
            percentage = (score * 100.0) / totalPossibleMarks;
        }

        tvScore.setText(
                String.format(Locale.getDefault(), "%d / %d", score, totalPossibleMarks)
        );

        tvTotalQuestions.setText(String.valueOf(numberOfQuestions));
        tvAttempted.setText(String.valueOf(attemptedCount));
        tvSkipped.setText(String.valueOf(skippedCount));
        tvCorrect.setText(String.valueOf(correctCount));
        tvWrong.setText(String.valueOf(wrongCount));

        tvPercentage.setText(
                String.format(Locale.getDefault(), "%.1f%%", percentage)
        );

        tvTimeTaken.setText(formatTime(timeTakenMillis));
    }


    // =========================================================
    // VIEW DETAILED REPORT (placeholder — built later)
    // =========================================================

    private void openDetailedReport() {

        Intent intent = new Intent(this, DetailedReportActivity.class);

        intent.putParcelableArrayListExtra("questionList", questionList);
        intent.putExtra("selectedAnswers", selectedAnswers);

        intent.putExtra("score", score);
        intent.putExtra("timeTakenMillis", timeTakenMillis);
        intent.putExtra("isCustomizedQuiz", isCustomizedQuiz);

        intent.putExtra("customSubject", customSubject);
        intent.putExtra("customChapter", customChapter);
        intent.putExtra("customTopic", customTopic);
        intent.putExtra("customDifficulty", customDifficulty);

        startActivity(intent);
    }


    // =========================================================
    // FORMAT TIME
    // =========================================================

    private String formatTime(long millis) {

        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}