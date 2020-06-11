package com.example.Covid19App;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

public class LabDetails extends AppCompatActivity {

    TextView name, city, state, desc, url, number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_details);

        name = findViewById(R.id.testLabName);
        city = findViewById(R.id.testCityName);
        state = findViewById(R.id.testStateName);
        desc = findViewById(R.id.testLabDesc);
        url = findViewById(R.id.testLabUrl);
        number = findViewById(R.id.testLabNumber);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                Toast.makeText(this, "No data received!", Toast.LENGTH_SHORT).show();
            } else {

                if(extras.getString("name") != null)
                {
                    name.setText(extras.getString("name").trim());
                    city.setText(extras.getString("city"));
                    state.setText(extras.getString("state"));
                    desc.setText(extras.getString("desc"));
                    url.setText(extras.getString("contact"));
                    number.setText(extras.getString("number"));

                }
            }
        }

    }
}
