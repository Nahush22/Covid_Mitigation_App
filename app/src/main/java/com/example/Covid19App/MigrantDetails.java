package com.example.Covid19App;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MigrantDetails extends AppCompatActivity {

    TextView name, no, gender, home, current;
    Button locBtn;

    String lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_migrant_details);

        name = findViewById(R.id.migDetailsName);
        no = findViewById(R.id.migDetailsContact);
        gender = findViewById(R.id.migDetailsGender);
        home = findViewById(R.id.migDetailsHome);
        current = findViewById(R.id.migDetailsCurrent);

        locBtn = findViewById(R.id.migDetailsLoc);

        Bundle extras = getIntent().getExtras();
        if(extras == null)
        {
            Toast.makeText(this, "Unable to get migrant details...Please try again.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            name.setText(extras.getString("Name"));
            no.setText(extras.getString("No"));
            gender.setText(extras.getString("Gender"));
            home.setText(extras.getString("Home"));
            current.setText(extras.getString("Current"));

            lat = extras.getString("Lat");
            lng = extras.getString("Lng");

            locBtn.setVisibility(View.VISIBLE);
        }

        locBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapLocation();
            }
        });

    }

    private void openMapLocation() {

//        https://stackoverflow.com/questions/42677389/android-how-to-pass-lat-long-route-info-to-google-maps-app

//        String url = "google.navigation:q=" + lat + "," + lng;
        String url = "https://www.google.com/maps/dir/?api=1&destination=" + lat + "," + lng + "&travelmode=driving";

        Uri gmmIntentUri = Uri.parse(url);//Format:"google.navigation:q="latitude,longitude"
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

    }
}
