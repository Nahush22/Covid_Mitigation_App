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

public class VolunteerListAdapter extends RecyclerView.Adapter<VolunteerListAdapter.MyViewHolder> {

    Context context;

    ArrayList<String> volunteerAdapterID = new ArrayList<>();
    ArrayList<String> volunteerAdapterAddress = new ArrayList<>();
    ArrayList<String> volunteerAdapterNumber = new ArrayList<>();

    VolunteerListClick volunteerListClick;

    public VolunteerListAdapter(Context context, ArrayList<String> volunteerAdapterID, ArrayList<String> volunteerAdapterAddress, ArrayList<String> volunteerAdapterNumber, VolunteerListClick volunteerListClick) {
        this.context = context;
        this.volunteerAdapterID = volunteerAdapterID;
        this.volunteerAdapterAddress = volunteerAdapterAddress;
        this.volunteerAdapterNumber = volunteerAdapterNumber;
        this.volunteerListClick = volunteerListClick;
    }

    public interface VolunteerListClick{
        void onEvent(int pos);
    }

    @NonNull
    @Override
    public VolunteerListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.volunteer_list, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull VolunteerListAdapter.MyViewHolder holder, final int position) {

        if(holder.id != null)
        {
            holder.id.setText(volunteerAdapterID.get(position));
            holder.address.setText(volunteerAdapterAddress.get(position));
            holder.number.setText(volunteerAdapterNumber.get(position));
        }



        holder.volunteerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volunteerListClick.onEvent( position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return volunteerAdapterID.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView id, address, number;
        ConstraintLayout volunteerList;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.volunteerID);
            address = itemView.findViewById(R.id.vAddressTxt);
            number = itemView.findViewById(R.id.vNumber);

            volunteerList = itemView.findViewById(R.id.volunteerLayout);

        }
    }
}
