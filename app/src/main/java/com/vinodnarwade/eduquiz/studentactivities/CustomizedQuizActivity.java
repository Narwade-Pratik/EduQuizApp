package com.vinodnarwade.eduquiz.studentactivities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.vinodnarwade.eduquiz.R;

public class CustomizedQuizActivity extends AppCompatActivity {

    private Spinner spinnerSubject;
    private Spinner spinnerChapter;
    private Spinner spinnerTopic;
    private Spinner spinnerDifficulty;

    private EditText etNumberOfQuestions;

    private AppCompatButton btnGenerateQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Customized Quiz");

        setContentView(R.layout.activity_customized_quiz);

        initializeViews();

        setupDifficultySpinner();

        setupGenerateButton();
    }

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
                    "Ready to generate " +
                            numberOfQuestions +
                            " questions",
                    Toast.LENGTH_SHORT
            ).show();

            // Firebase question selection will be implemented next.

        });
    }
}