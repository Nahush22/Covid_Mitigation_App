package com.example.Covid19App;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {

    Button storeActivityBtn, findBtn, adminActivityBtn, volunteerActivityBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        storeActivityBtn = findViewById(R.id.storeActivityBtn);
        findBtn = findViewById(R.id.findBtn);
        adminActivityBtn = findViewById(R.id.adminBtn);
        volunteerActivityBtn = findViewById(R.id.volunteerBtn);

        adminActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AdminRegister.class));
            }
        });

        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FinderActivity.class));
            }
        });

        storeActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainStoreActivity.class));
            }
        });

        volunteerActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), VolunteerSignUp.class));
            }
        });
    }
}
