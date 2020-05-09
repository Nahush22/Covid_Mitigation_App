package com.example.Covid19App;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminFunctionsActivity extends AppCompatActivity {

    Button scannerBtn, volunteerListBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_functions);

        scannerBtn = findViewById(R.id.orderScanBtn);
        volunteerListBtn = findViewById(R.id.volunteerListBtn);

        scannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminFunctionsActivity.this, AdminScanner.class));
            }
        });

        volunteerListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminFunctionsActivity.this, VolunteerList.class));
            }
        });
    }
}
