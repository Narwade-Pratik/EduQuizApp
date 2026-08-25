package com.vinodnarwade.eduquiz.teacheractivities;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinodnarwade.eduquiz.R;

import java.util.List;

public class WeakAreaDetailAdapter
        extends RecyclerView.Adapter<WeakAreaDetailAdapter.ViewHolder> {

    public interface OnRowClickListener {
        void onRowClick(WeakAreaRow row);
    }

    private final List<WeakAreaRow> rows;
    private final OnRowClickListener listener;

    public WeakAreaDetailAdapter(List<WeakAreaRow> rows) {
        this(rows, null);
    }

    public WeakAreaDetailAdapter(List<WeakAreaRow> rows, OnRowClickListener listener) {
        this.rows = rows;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weak_area_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WeakAreaRow row = rows.get(position);

        holder.tvSubject.setText(row.getSubject());
        holder.tvTopic.setText(row.getTopic());

        holder.tvCorrect.setText("Correct: " + row.getCorrectCount());
        holder.tvIncorrect.setText("Incorrect: " + row.getIncorrectCount());
        holder.tvUnattempted.setText("Unattempted: " + row.getUnattemptedCount());

        String label = row.getPerformanceLabel();
        holder.tvLabel.setText(label);

        switch (label) {

            case "Strong":
                holder.tvLabel.setTextColor(Color.parseColor("#2E7D32"));
                break;

            case "Average":
                holder.tvLabel.setTextColor(Color.parseColor("#F57C00"));
                break;

            case "Weak":
                holder.tvLabel.setTextColor(Color.parseColor("#C62828"));
                break;

            default:
                holder.tvLabel.setTextColor(Color.parseColor("#9E9E9E"));
        }

        if (listener != null) {

            holder.itemView.setClickable(true);
            holder.itemView.setFocusable(true);
            holder.itemView.setOnClickListener(v -> listener.onRowClick(row));

        } else {

            holder.itemView.setClickable(false);
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvSubject, tvTopic, tvCorrect, tvIncorrect, tvUnattempted, tvLabel;

        ViewHolder(View itemView) {
            super(itemView);

            tvSubject = itemView.findViewById(R.id.tvItemWeakAreaSubject);
            tvTopic = itemView.findViewById(R.id.tvItemWeakAreaTopic);
            tvCorrect = itemView.findViewById(R.id.tvItemWeakAreaCorrect);
            tvIncorrect = itemView.findViewById(R.id.tvItemWeakAreaIncorrect);
            tvUnattempted = itemView.findViewById(R.id.tvItemWeakAreaUnattempted);
            tvLabel = itemView.findViewById(R.id.tvItemWeakAreaLabel);
        }
    }
}