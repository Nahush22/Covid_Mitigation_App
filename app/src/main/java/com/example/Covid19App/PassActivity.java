package com.example.Covid19App;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PassActivity extends AppCompatActivity {

    CardView travelCard, serviceCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);

        travelCard = findViewById(R.id.travelPassCard);
        serviceCard = findViewById(R.id.essentialServicePassCard);

        travelCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PassActivity.this, TravelPassActivity.class));
            }
        });

        serviceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PassActivity.this, EssentialServicePassQR.class));
            }
        });
    }
}
