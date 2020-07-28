package com.example.Covid19App;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MigrantSignUp extends AppCompatActivity {

    EditText name, number, home, current;
    Button locationBtn;
    Spinner genderSpinner;

    String gender;

    String types[] = {"Male", "Female", "Others", "Not sharing"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_migrant_sign_up);

        name = findViewById(R.id.migrantName);
        number = findViewById(R.id.migrantNumber);
        home = findViewById(R.id.migrantHome);
        current = findViewById(R.id.migrantAddress);

        locationBtn = findViewById(R.id.migrantLocation);

        genderSpinner = findViewById(R.id.migrantGender);
        genderSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types));

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = genderSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(name.getText().toString() == null || number.getText().toString() == null || home.getText().toString() == null || current.getText().toString() == null )
                {
                    Toast.makeText(MigrantSignUp.this, "Fill in all the details!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), MigrantLocationSignUp.class);
                    intent.putExtra("Name", name.getText().toString());
                    intent.putExtra("No", number.getText().toString());
                    intent.putExtra("Home", home.getText().toString());
                    intent.putExtra("Current", current.getText().toString());
                    intent.putExtra("Gender", gender);

                    finish();
                    startActivity(intent);
                }
            }
        });

    }
}
