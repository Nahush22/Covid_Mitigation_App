package com.example.Covid19App;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BasicsListAdapter extends RecyclerView.Adapter<BasicsListAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> content = new ArrayList<>();

    public BasicsListAdapter(Context context, ArrayList<String> title, ArrayList<String> content) {
        this.context = context;
        this.title = title;
        this.content = content;
    }

    @NonNull
    @Override
    public BasicsListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.basics_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BasicsListAdapter.MyViewHolder holder, int position) {

        if(holder.basicTitle != null)
        {
            holder.basicTitle.setText(title.get(position));
            holder.basicTxt.setText(content.get(position));
        }


        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.expand.getVisibility() == View.GONE)
                {
                    TransitionManager.beginDelayedTransition(holder.main, new AutoTransition());
                    holder.expand.setVisibility(View.VISIBLE);
                    holder.arrBtn.setBackgroundResource(R.drawable.arr_up);
                }
                else
                {
                    TransitionManager.beginDelayedTransition(holder.main, new AutoTransition());
                    holder.expand.setVisibility(View.GONE);
                    holder.arrBtn.setBackgroundResource(R.drawable.arr_down);
                }

            }
        });

        holder.arrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.expand.getVisibility() == View.GONE)
                {
                    TransitionManager.beginDelayedTransition(holder.main, new AutoTransition());
                    holder.expand.setVisibility(View.VISIBLE);
                    holder.arrBtn.setBackgroundResource(R.drawable.arr_up);
                }
                else
                {
                    TransitionManager.beginDelayedTransition(holder.main, new AutoTransition());
                    holder.expand.setVisibility(View.GONE);
                    holder.arrBtn.setBackgroundResource(R.drawable.arr_down);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView basicTitle, basicTxt;
        Button arrBtn;
        ConstraintLayout main, defaultLayout, expand;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            basicTitle = itemView.findViewById(R.id.basicTitle);
            basicTxt = itemView.findViewById(R.id.basicTxt);

            arrBtn = itemView.findViewById(R.id.basicArrBtn);

            main = itemView.findViewById(R.id.basicMainLayout);
            defaultLayout = itemView.findViewById(R.id.basicDefaultView);
            expand = itemView.findViewById(R.id.basicExpandableView);
        }
    }
}
