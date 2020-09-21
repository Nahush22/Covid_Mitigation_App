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

public class DoctorReqAdapter extends RecyclerView.Adapter<DoctorReqAdapter.MyViewHolder> {

    Context context;
    ArrayList<DoctorClass> doctors = new ArrayList<>();

    public DoctorReqAdapter(Context context, ArrayList<DoctorClass> doctors) {
        this.context = context;
        this.doctors = doctors;
    }

    @NonNull
    @Override
    public DoctorReqAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.doctor_req_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorReqAdapter.MyViewHolder holder, int position) {

        final DoctorClass doctor = doctors.get(position);

        if(doctor.getName() != null)
        {
            holder.name.setText(doctor.getName());
            holder.address.setText(doctor.getAddress());
            holder.number.setText(doctor.getNumber());
        }

        holder.doctorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DoctorDetails.class);
                intent.putExtra("name", doctor.getName());
                intent.putExtra("no", doctor.getNumber());
                intent.putExtra("address", doctor.getAddress());
                intent.putExtra("clinic", doctor.getClinic());
                intent.putExtra("fromTime", doctor.getStart());
                intent.putExtra("toTime", doctor.getEnd());
                intent.putExtra("domain", doctor.getDomain());
                intent.putExtra("id", doctor.getId());
                intent.putExtra("UserId", doctor.getUserId());

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout doctorLayout;
        TextView name, address, number;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            doctorLayout = itemView.findViewById(R.id.doctorReqLayout);

            name = itemView.findViewById(R.id.doctorReqName);
            address = itemView.findViewById(R.id.doctorReqAddress);
            number = itemView.findViewById(R.id.doctorReqNo);
        }
    }
}
