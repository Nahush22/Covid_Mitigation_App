package com.example.Covid19App;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

public class LauncherActivity extends AppCompatActivity {

    CardView storeCard, finderCard, adminCard, volunteerCard, passCard, labCard, virusCard, faqCard;

    String userID;
    FirebaseAuth mAuth;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ACTUALUSERID = "actualuserid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        storeCard = findViewById(R.id.storeCard);
        finderCard = findViewById(R.id.finderCard);
        adminCard = findViewById(R.id.adminCard);
        volunteerCard = findViewById(R.id.volunteerCard);
        passCard = findViewById(R.id.passCard);
        labCard = findViewById(R.id.labCard);
        virusCard = findViewById(R.id.virusCard);
        faqCard = findViewById(R.id.faqCard);

        userID = mAuth.getInstance().getCurrentUser().getUid();

        storeUserId();

        adminCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AdminRegister.class));
            }
        });

        finderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FinderActivity.class));
            }
        });

        storeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainStoreActivity.class));
            }
        });

        volunteerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), VolunteerSignUp.class));
            }
        });

        passCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PassActivity.class));
            }
        });

        labCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TestLabsActivity.class));
            }
        });

        virusCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SymptomsActivity.class));
            }
        });

        faqCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FaqActivity.class));
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
