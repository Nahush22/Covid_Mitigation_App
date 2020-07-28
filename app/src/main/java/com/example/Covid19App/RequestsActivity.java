package com.example.Covid19App;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RequestsActivity extends AppCompatActivity {

    CardView complCard, servCard, workerSignUpReqCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        complCard = findViewById(R.id.volunteerReqCard);
        servCard = findViewById(R.id.servicePassReqCard);
        workerSignUpReqCard = findViewById(R.id.workerSignUpReqCard);

        complCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RequestsActivity.this, AdminTaskCompletionList.class));
            }
        });

        servCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RequestsActivity.this, EssentialServicePassList.class));
            }
        });

        workerSignUpReqCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RequestsActivity.this, WorkerRequestList.class));
            }
        });
    }
}
