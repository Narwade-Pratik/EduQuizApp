package com.vinodnarwade.eduquiz.studentactivities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinodnarwade.eduquiz.R;
import com.vinodnarwade.eduquiz.studentactivities.QuestionResultModel;

import java.util.List;

public class QuestionResultAdapter extends RecyclerView.Adapter<QuestionResultAdapter.QuestionViewHolder> {

    private Context context;
    private List<QuestionResultModel> questionList;

    public QuestionResultAdapter(Context context, List<QuestionResultModel> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_item_quiz_result, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        QuestionResultModel question = questionList.get(position);

        holder.tvQuestion.setText((position + 1) + ". " + question.getQuestion());
        holder.tvOption1.setText("A. " + question.getOption1());
        holder.tvOption2.setText("B. " + question.getOption2());
        holder.tvOption3.setText("C. " + question.getOption3());
        holder.tvOption4.setText("D. " + question.getOption4());
        holder.tvYourAnswer.setText("Your Answer: " + question.getYourAnswer());
        holder.tvCorrectAnswer.setText("Correct Answer: " + question.getCorrectAnswer());
        holder.tvMarks.setText("Marks Awarded: " + question.getMarks());
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvOption1, tvOption2, tvOption3, tvOption4, tvYourAnswer, tvCorrectAnswer, tvMarks;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvItemQuizResultQuestion);
            tvOption1 = itemView.findViewById(R.id.tvItemQuizResultOption1);
            tvOption2 = itemView.findViewById(R.id.tvItemQuizResultOption2);
            tvOption3 = itemView.findViewById(R.id.tvItemQuizResultOption3);
            tvOption4 = itemView.findViewById(R.id.tvItemQuizResultOption4);
            tvYourAnswer = itemView.findViewById(R.id.tvItemQuizResultYourAnswer);
            tvCorrectAnswer = itemView.findViewById(R.id.tvItemQuizResultCorrectAnswer);
            tvMarks = itemView.findViewById(R.id.tvItemQuizResultMarks);
        }
    }
}
