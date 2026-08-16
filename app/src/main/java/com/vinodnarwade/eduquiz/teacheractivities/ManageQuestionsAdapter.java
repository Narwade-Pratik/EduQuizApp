package com.vinodnarwade.eduquiz.teacheractivities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinodnarwade.eduquiz.R;

import java.util.List;

public class ManageQuestionsAdapter
        extends RecyclerView.Adapter<ManageQuestionsAdapter.ViewHolder> {

    private final List<QuestionBankQuestionModel> questionList;

    public ManageQuestionsAdapter(
            List<QuestionBankQuestionModel> questionList) {

        this.questionList = questionList;
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

        holder.tvQuestion.setText(
                model.getQuestion()
        );

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

        holder.tvCorrectOption.setText(
                "Correct Option: "
                        + model.getCorrectOption()
        );

        holder.tvMarks.setText(
                "Marks: " + model.getMarks()
        );
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvQuestion;
        TextView tvOptionA;
        TextView tvOptionB;
        TextView tvOptionC;
        TextView tvOptionD;
        TextView tvCorrectOption;
        TextView tvMarks;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvQuestion =
                    itemView.findViewById(R.id.tvQuestion);

            tvOptionA =
                    itemView.findViewById(R.id.tvOptionA);

            tvOptionB =
                    itemView.findViewById(R.id.tvOptionB);

            tvOptionC =
                    itemView.findViewById(R.id.tvOptionC);

            tvOptionD =
                    itemView.findViewById(R.id.tvOptionD);

            tvCorrectOption =
                    itemView.findViewById(
                            R.id.tvCorrectOption
                    );

            tvMarks =
                    itemView.findViewById(R.id.tvMarks);
        }
    }
}