package com.example.Covid19App;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class ServiceFragment extends Fragment {

    CardView storeCard, adminCard, volunteerCard, passCard, workerCard, foodCard, migrantCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storeCard = view.findViewById(R.id.storeCard);
        adminCard = view.findViewById(R.id.adminCard);
        volunteerCard = view.findViewById(R.id.volunteerCard);
        passCard = view.findViewById(R.id.passCard);
        workerCard = view.findViewById(R.id.workerCard);
        foodCard = view.findViewById(R.id.foodDonorCard);
        migrantCard = view.findViewById(R.id.migrantCard);

        adminCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AdminRegister.class));
            }
        });

        storeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainStoreActivity.class));
            }
        });

        volunteerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MaterialVolunteerSignUp.class));
            }
        });

        passCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PassActivity.class));
            }
        });

        workerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WorkerSignUp.class));
            }
        });

        foodCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FoodLocationActivity.class));
            }
        });

        migrantCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MigrantSignUp.class));
            }
        });
    }
}
