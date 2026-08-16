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

public class QuestionBankAdapter
        extends RecyclerView.Adapter<QuestionBankAdapter.ViewHolder> {

    public interface OnManageClickListener {
        void onManageClick(QuestionBankModel model);
    }

    private final List<QuestionBankModel> questionBankList;
    private final OnManageClickListener listener;

    public QuestionBankAdapter(
            List<QuestionBankModel> questionBankList,
            OnManageClickListener listener) {

        this.questionBankList = questionBankList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_question_bank,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        QuestionBankModel model =
                questionBankList.get(position);

        holder.tvClass.setText(model.getClassName());
        holder.tvSubject.setText(model.getSubject());
        holder.tvChapter.setText(
                "Chapter: " + model.getChapter()
        );
        holder.tvTopic.setText(
                "Topic: " + model.getTopic()
        );

        holder.tvQuestionCounts.setText(
                "Easy: " + model.getEasyCount()
                        + " | Medium: " + model.getMediumCount()
                        + " | Hard: " + model.getHardCount()
        );

        holder.btnManage.setOnClickListener(v ->
                listener.onManageClick(model)
        );
    }

    @Override
    public int getItemCount() {
        return questionBankList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvClass;
        TextView tvSubject;
        TextView tvChapter;
        TextView tvTopic;
        TextView tvQuestionCounts;

        AppCompatButton btnManage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClass =
                    itemView.findViewById(R.id.tvClass);

            tvSubject =
                    itemView.findViewById(R.id.tvSubject);

            tvChapter =
                    itemView.findViewById(R.id.tvChapter);

            tvTopic =
                    itemView.findViewById(R.id.tvTopic);

            tvQuestionCounts =
                    itemView.findViewById(
                            R.id.tvQuestionCounts
                    );

            btnManage =
                    itemView.findViewById(R.id.btnManage);
        }
    }
}