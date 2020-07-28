package com.example.Covid19App;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> skillTitle = new ArrayList<>();

    public SkillAdapter(Context context, ArrayList<String> skillTitle) {
        this.context = context;
        this.skillTitle = skillTitle;
    }


    @NonNull
    @Override
    public SkillAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.skill_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillAdapter.MyViewHolder holder, int position) {

        final String title = skillTitle.get(position);
        holder.skillTitle.setText(title);

        holder.skillLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, PlayListActivity.class);
//                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("Skill", title);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return skillTitle.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout skillLayout;
        TextView skillTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            skillTitle = itemView.findViewById(R.id.skill_title);
            skillLayout = itemView.findViewById(R.id.skill_main);
        }
    }
}
