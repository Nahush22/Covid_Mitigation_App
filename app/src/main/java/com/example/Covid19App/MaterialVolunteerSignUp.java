package com.example.Covid19App;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class MaterialVolunteerSignUp extends AppCompatActivity {

    private static final String TAG = "Material Volunteer Reg:";

    EditText vName, vNo, vAddress, vMail;
    Button reg;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String VOLUNTEERID = "volunteerId";
    private static final String ACTUALUSERID = "actualuserid";

    String userID = "NAN" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.material_volunteer_sign_up);

        loadUserId();

        vName = findViewById(R.id.materialVolName);
        vNo = findViewById(R.id.materialVolNo);
        vAddress = findViewById(R.id.materialVolAddress);
        vMail = findViewById(R.id.materialVolMail);

        reg = findViewById(R.id.materialVolReg);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userID = sharedPreferences.getString(ACTUALUSERID, "NAN");

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Location permission not given", Toast.LENGTH_SHORT).show();
        }


        String volunteerExists = loadStoreDoc();

        if(volunteerExists.isEmpty())
        {
//            Toast.makeText(this, "Create new volunteer id", Toast.LENGTH_SHORT).show();
        }
        else
        {
            finish();
            startActivity(new Intent(MaterialVolunteerSignUp.this,MaterialVolunteerTaskActivity.class));
        }

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDetails();
            }
        });

    }

    private void verifyDetails() {

        if (vName.getText().toString().isEmpty() || vNo.getText().toString().isEmpty() || vAddress.getText().toString().isEmpty() ||
                vMail.getText().toString().isEmpty()) {
            Log.d(TAG, "Name not present");
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Name present");
//            updateSellerDb();

            Intent intent = new Intent(this, MaterialVolunteerMapActivity.class);
            intent.putExtra("name", vName.getText().toString());
            intent.putExtra("no", vNo.getText().toString());
            intent.putExtra("address", vAddress.getText().toString());
            intent.putExtra("mail", vMail.getText().toString());
            finish();
            startActivity(intent);

        }

    }

    private String loadStoreDoc() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String text = sharedPreferences.getString(VOLUNTEERID, "");
        return text;

    }

    private void loadUserId() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userID = sharedPreferences.getString(ACTUALUSERID, "");

    }

}