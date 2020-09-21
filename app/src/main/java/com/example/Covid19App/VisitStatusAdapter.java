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

public class VisitStatusAdapter extends RecyclerView.Adapter<VisitStatusAdapter.MyViewHolder> {

    Context context;
    ArrayList<VisitClass> visits = new ArrayList<>();

    public VisitStatusAdapter(Context context, ArrayList<VisitClass> visits) {
        this.context = context;
        this.visits = visits;
    }

    @NonNull
    @Override
    public VisitStatusAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.visit_status_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitStatusAdapter.MyViewHolder holder, int position) {
        VisitClass visit = visits.get(position);

        if(visit.getName() != null)
        {
            holder.clinic.setText(visit.getClinic());
            holder.name.setText(visit.getName());
            holder.timings.setText(visit.getTimings());
        }
    }

    @Override
    public int getItemCount() {
        return visits.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout visitLayout;
        TextView clinic, name, timings;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            visitLayout = itemView.findViewById(R.id.visitStatusLayout);

            clinic = itemView.findViewById(R.id.visitClinic);
            name = itemView.findViewById(R.id.visitName);
            timings = itemView.findViewById(R.id.visitTimings);

        }
    }
}
