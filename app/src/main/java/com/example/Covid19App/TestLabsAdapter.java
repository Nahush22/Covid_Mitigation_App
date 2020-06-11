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

public class TestLabsAdapter extends RecyclerView.Adapter<TestLabsAdapter.MyViewHolder> {

    Context context;
    ArrayList<LabClass> lab = new ArrayList<>();

    public TestLabsAdapter(Context context, ArrayList<LabClass> lab) {
        this.context = context;
        this.lab = lab;
    }

    @NonNull
    @Override
    public TestLabsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.test_labs_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TestLabsAdapter.MyViewHolder holder, final int position) {

        final LabClass labClass = lab.get(position);

        if(labClass.getName() != null)
        {
            holder.name.setText(labClass.getName());
            holder.city.setText(labClass.getCity());
            holder.state.setText(labClass.getState());
        }

        holder.testLabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LabDetails.class);
                intent.putExtra("name", labClass.getName());
                intent.putExtra("city", labClass.getCity());
                intent.putExtra("state", labClass.getState());
                intent.putExtra("desc", labClass.getDesc());
                intent.putExtra("contact", labClass.getContact());
                intent.putExtra("number", labClass.getNumber());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lab.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, city, state;
        ConstraintLayout testLabLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.labName);
            city = itemView.findViewById(R.id.labCity);
            state = itemView.findViewById(R.id.labState);

            testLabLayout = itemView.findViewById(R.id.testLabLayout);
        }
    }
}
