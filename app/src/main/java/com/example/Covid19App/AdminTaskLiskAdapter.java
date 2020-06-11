package com.example.Covid19App;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminTaskLiskAdapter extends RecyclerView.Adapter<AdminTaskLiskAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> task = new ArrayList<>();
    ArrayList<String> volunteerId = new ArrayList<>();

    AdminTaskListClick adminTaskListClick;

    public AdminTaskLiskAdapter(Context context, ArrayList<String> task, ArrayList<String> volunteerId, AdminTaskListClick adminTaskListClick) {
        this.context = context;
        this.task = task;
        this.volunteerId = volunteerId;
        this.adminTaskListClick = adminTaskListClick;
    }

    public interface AdminTaskListClick{
        void onEvent(int pos);
    }

    @NonNull
    @Override
    public AdminTaskLiskAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.admin_task_completion_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminTaskLiskAdapter.MyViewHolder holder, final int position) {

        if(holder.brief != null)
        {
            holder.brief.setText(task.get(position));
            holder.userId.setText(volunteerId.get(position));
        }

        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminTaskListClick.onEvent(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return task.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView brief, userId;
        TextView acceptBtn;
        ConstraintLayout adminTaskListLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            brief = itemView.findViewById(R.id.adminTaskBrief);
            userId = itemView.findViewById(R.id.adminTaskId);

            acceptBtn = itemView.findViewById(R.id.adminAcceptBtn);

            adminTaskListLayout = itemView.findViewById(R.id.adminTaksCompletionLayout);
        }
    }
}
