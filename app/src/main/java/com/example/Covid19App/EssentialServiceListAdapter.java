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

public class EssentialServiceListAdapter extends RecyclerView.Adapter<EssentialServiceListAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> home = new ArrayList<>();
    ArrayList<String> prof = new ArrayList<>();
    ArrayList<String> id = new ArrayList<>();

    public EssentialServiceListAdapter(Context context, ArrayList<String> name, ArrayList<String> home, ArrayList<String> prof, ArrayList<String> id) {
        this.context = context;
        this.name = name;
        this.home = home;
        this.prof = prof;
        this.id = id;
    }

    @NonNull
    @Override
    public EssentialServiceListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.essential_service_request_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EssentialServiceListAdapter.MyViewHolder holder, int position) {

        String servName = name.get(position);
        String servHome = home.get(position);
        String servProf = prof.get(position);
        final String servId = id.get(position);

        if(holder.name != null)
        {
            holder.name.setText(servName);
            holder.home.setText(servHome);
            holder.prof.setText(servProf);
        }

        holder.esrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EssentialServicePassDetails.class);
                intent.putExtra("PassUserId", servId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, home, prof;
        ConstraintLayout esrLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.esrName);
            home = itemView.findViewById(R.id.esrHome);
            prof = itemView.findViewById(R.id.esrProf);

            esrLayout = itemView.findViewById(R.id.esrLayout);
        }
    }
}
