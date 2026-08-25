package com.vinodnarwade.eduquiz.teacheractivities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinodnarwade.eduquiz.R;

import java.util.List;

public class SimpleNameListAdapter
        extends RecyclerView.Adapter<SimpleNameListAdapter.ViewHolder> {

    public interface OnNameClickListener {
        void onNameClick(String name);
    }

    private final List<String> names;
    private final OnNameClickListener listener;

    public SimpleNameListAdapter(List<String> names, OnNameClickListener listener) {
        this.names = names;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_simple_name, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String name = names.get(position);
        holder.tvName.setText(name);
        holder.itemView.setOnClickListener(v -> listener.onNameClick(name));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemSimpleName);
        }
    }
}