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

import java.util.ArrayList;

public class WorkerRequestListAdapter extends RecyclerView.Adapter<WorkerRequestListAdapter.MyViewHolder> {

    Context context;
    ArrayList<WorkerClass> worker = new ArrayList<>();

    public WorkerRequestListAdapter(Context context, ArrayList<WorkerClass> worker) {
        this.context = context;
        this.worker = worker;
    }


    @NonNull
    @Override
    public WorkerRequestListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.worker_request_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerRequestListAdapter.MyViewHolder holder, int position) {

        final WorkerClass workerClass = worker.get(position);

        if(workerClass!=null)
        {
            holder.name.setText(workerClass.getName());
            holder.address.setText(workerClass.getHome());
            holder.prof.setText(workerClass.getProf());
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, WorkerRequestDetails.class).putExtra("Id", workerClass.getId()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return worker.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layout;
        TextView name, address, prof;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.wrl_layout);

            name = itemView.findViewById(R.id.wrlName);
            address = itemView.findViewById(R.id.wrlName);
            prof = itemView.findViewById(R.id.wrlProf);
        }
    }
}
