package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.ViewHolder> {
    private List<Lesson> lessons;

    public LessonAdapter(List<Lesson> lessons) { this.lessons = lessons; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.tvTitle.setText(lesson.subject);
        holder.tvTime.setText(lesson.time + " • " + lesson.type);
        holder.tvTeacher.setText(lesson.teacher);
        holder.tvRoom.setText(lesson.room);
        holder.indicator.setBackgroundColor(lesson.color);
    }

    @Override
    public int getItemCount() { return (lessons != null) ? lessons.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvTeacher, tvRoom;
        View indicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTeacher = itemView.findViewById(R.id.tvTeacher);
            tvRoom = itemView.findViewById(R.id.tvRoom);
            indicator = itemView.findViewById(R.id.indicator);
        }
    }
}