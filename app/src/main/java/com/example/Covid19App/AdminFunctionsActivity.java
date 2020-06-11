package com.example.Covid19App;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AdminFunctionsActivity extends AppCompatActivity {

    Toolbar toolbar;

    CardView scanCard, taskCard, reqCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_functions);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        scanCard = findViewById(R.id.scanCard);
        taskCard = findViewById(R.id.taskCard);
        reqCard = findViewById(R.id.reqCard);

        scanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminFunctionsActivity.this, AdminScanner.class));
            }
        });

        taskCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminFunctionsActivity.this, VolunteerList.class));
            }
        });

        reqCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminFunctionsActivity.this, RequestsActivity.class));
            }
        });


    }
}




