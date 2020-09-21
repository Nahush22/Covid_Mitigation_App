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

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.MyViewHolder> {

    Context context;
    ArrayList<DoctorClass> doctors = new ArrayList<>();

    public DoctorListAdapter(Context context, ArrayList<DoctorClass> doctors) {
        this.context = context;
        this.doctors = doctors;
    }

    @NonNull
    @Override
    public DoctorListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.material_doctor_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorListAdapter.MyViewHolder holder, int position) {

        final DoctorClass doctor = doctors.get(position);

        if(doctor.getName() != null)
        {
            holder.clinic.setText(doctor.getClinic());
            holder.name.setText(doctor.getName());
            holder.domain.setText(doctor.getDomain());
            holder.address.setText(doctor.getAddress());
            holder.number.setText(doctor.getNumber());
        }

        holder.doctorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PatientRegActivity.class);
                intent.putExtra("UserId", doctor.getUserId());
                intent.putExtra("Name", doctor.getName());
                intent.putExtra("Clinic", doctor.getClinic());
                intent.putExtra("Start", doctor.getStart());
                intent.putExtra("End", doctor.getEnd());
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
        TextView clinic, name, domain, address, number;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            doctorLayout = itemView.findViewById(R.id.materialDocListLayout);

            clinic = itemView.findViewById(R.id.materialDocListClinic);
            name = itemView.findViewById(R.id.materialDocListName);
            domain = itemView.findViewById(R.id.materialDocListProf);
            address = itemView.findViewById(R.id.materialDocListAddress);
            number = itemView.findViewById(R.id.materialDocListNumber);
        }
    }
}
