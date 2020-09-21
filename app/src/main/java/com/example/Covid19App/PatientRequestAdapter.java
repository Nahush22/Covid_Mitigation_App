package com.example.Covid19App;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PatientRequestAdapter extends RecyclerView.Adapter<PatientRequestAdapter.MyViewHolder> {

    Context context;
    ArrayList<PatientClass> patients = new ArrayList<>();

    PatientListClick patientListClick;

    public interface PatientListClick{
        void onEvent(int pos, String type);
    }

    public PatientRequestAdapter(Context context, ArrayList<PatientClass> patients, PatientListClick patientListClick) {
        this.context = context;
        this.patients = patients;
        this.patientListClick = patientListClick;
    }

    @NonNull
    @Override
    public PatientRequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.material_patient_request_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientRequestAdapter.MyViewHolder holder, final int position) {

        PatientClass patient = patients.get(position);

        if(patient.getName() != null)
        {
            holder.name.setText(patient.getName());
            holder.symp.setText(patient.getSymptoms());
            holder.age.setText(patient.getAge());
        }

        holder.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patientListClick.onEvent(position, "Yes");
            }
        });

        holder.no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patientListClick.onEvent(position, "No");
            }
        });

    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout patientLayout;
        TextView name, symp, age;
        ImageView yes, no;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            patientLayout = itemView.findViewById(R.id.materialPatientReqLayout);

            name = itemView.findViewById(R.id.materialPatientReqName);
            symp = itemView.findViewById(R.id.materialPatientReqSymp);
            age = itemView.findViewById(R.id.materialPatientReqAge);

            yes = itemView.findViewById(R.id.materialPatientReqYes);
            no = itemView.findViewById(R.id.materialPatientReqNo);
        }
    }
}
