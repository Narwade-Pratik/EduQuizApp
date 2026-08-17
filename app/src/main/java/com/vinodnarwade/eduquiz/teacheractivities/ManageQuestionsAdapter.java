package com.vinodnarwade.eduquiz.teacheractivities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.vinodnarwade.eduquiz.R;

import java.util.List;

public class ManageQuestionsAdapter
        extends RecyclerView.Adapter<ManageQuestionsAdapter.ViewHolder> {

    public interface OnQuestionActionListener {

        void onEditQuestion(QuestionBankQuestionModel question);

        void onDeleteQuestion(QuestionBankQuestionModel question);
    }

    private final List<QuestionBankQuestionModel> questionList;
    private final OnQuestionActionListener listener;

    public ManageQuestionsAdapter(
            List<QuestionBankQuestionModel> questionList,
            OnQuestionActionListener listener) {

        this.questionList = questionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_manage_question,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        QuestionBankQuestionModel model =
                questionList.get(position);

        // Question Topic
        holder.tvQuestionTopic.setText(
                "Topic: " + model.getQuestionTopic()
        );

        // Question
        holder.tvQuestion.setText(
                model.getQuestion()
        );

        // Options
        holder.tvOptionA.setText(
                "A. " + model.getOptionA()
        );

        holder.tvOptionB.setText(
                "B. " + model.getOptionB()
        );

        holder.tvOptionC.setText(
                "C. " + model.getOptionC()
        );

        holder.tvOptionD.setText(
                "D. " + model.getOptionD()
        );

        // Correct option
        holder.tvCorrectOption.setText(
                "Correct Option: " + model.getCorrectOption()
        );

        // Marks
        holder.tvMarks.setText(
                "Marks: " + model.getMarks()
        );

        // Edit
        holder.btnEditQuestion.setOnClickListener(v ->
                listener.onEditQuestion(model)
        );

        // Delete
        holder.btnDeleteQuestion.setOnClickListener(v ->
                listener.onDeleteQuestion(model)
        );
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvQuestionTopic;
        TextView tvQuestion;
        TextView tvOptionA;
        TextView tvOptionB;
        TextView tvOptionC;
        TextView tvOptionD;
        TextView tvCorrectOption;
        TextView tvMarks;

        AppCompatButton btnEditQuestion;
        AppCompatButton btnDeleteQuestion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvQuestionTopic =
                    itemView.findViewById(
                            R.id.tvQuestionTopic
                    );

            tvQuestion =
                    itemView.findViewById(
                            R.id.tvQuestion
                    );

            tvOptionA =
                    itemView.findViewById(
                            R.id.tvOptionA
                    );

            tvOptionB =
                    itemView.findViewById(
                            R.id.tvOptionB
                    );

            tvOptionC =
                    itemView.findViewById(
                            R.id.tvOptionC
                    );

            tvOptionD =
                    itemView.findViewById(
                            R.id.tvOptionD
                    );

            tvCorrectOption =
                    itemView.findViewById(
                            R.id.tvCorrectOption
                    );

            tvMarks =
                    itemView.findViewById(
                            R.id.tvMarks
                    );

            btnEditQuestion =
                    itemView.findViewById(
                            R.id.btnEditQuestion
                    );

            btnDeleteQuestion =
                    itemView.findViewById(
                            R.id.btnDeleteQuestion
                    );
        }
    }
}