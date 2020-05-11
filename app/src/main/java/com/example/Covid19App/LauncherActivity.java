package com.example.Covid19App;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LauncherActivity extends AppCompatActivity {

    Button storeActivityBtn, findBtn, adminActivityBtn, volunteerActivityBtn;

    String userID;
    FirebaseAuth mAuth;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ACTUALUSERID = "actualuserid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        storeActivityBtn = findViewById(R.id.storeActivityBtn);
        findBtn = findViewById(R.id.findBtn);
        adminActivityBtn = findViewById(R.id.adminBtn);
        volunteerActivityBtn = findViewById(R.id.volunteerBtn);

        userID = mAuth.getInstance().getCurrentUser().getUid();

        storeUserId();

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

    private void storeUserId() {

        Log.d("Launcher Activity", userID);
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ACTUALUSERID, userID);
        editor.apply();

    }
}
