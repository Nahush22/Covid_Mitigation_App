package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PatientRegActivity extends AppCompatActivity {

    private static final String TAG = "PatientRegActivity";
    TextView name, age, symptoms;
    Button reg;

    String doctorId, doctorClinic, doctorName, docterStart, doctorEnd;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ACTUALUSERID = "actualuserid";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String userID = "NAN" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.material_patient_reg);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userID = sharedPreferences.getString(ACTUALUSERID, "NAN");

        Bundle b = getIntent().getExtras();
        if(b == null)
        {
            Toast.makeText(this, "Unable to get doctor data.Please go back & select the doctor again", Toast.LENGTH_SHORT).show();
        }
        else
        {
            doctorId = b.getString("UserId");
            doctorName = b.getString("Name");
            doctorClinic = b.getString("Clinic");
            docterStart = b.getString("Start");
            doctorEnd = b.getString("End");
        }

        name = findViewById(R.id.materialPatientName);
        age = findViewById(R.id.materialPatientAge);
        symptoms = findViewById(R.id.materialPatientSymptom);

        reg = findViewById(R.id.materialPatientReg);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(name != null && age != null && symptoms != null)
                {
                    regDb();
                }
                else
                {
                    Toast.makeText(PatientRegActivity.this, "Fill in all the details", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void regDb() {

        Map<String,Object> patient = new HashMap<>();
        patient.put("PatientId", userID);
        patient.put("DoctorId", doctorId);
        patient.put("DoctorName", doctorName);
        patient.put("Clinic", doctorClinic);
        patient.put("OrigStart", docterStart);
        patient.put("OrigEnd", doctorEnd);
        patient.put("Name", name.getText().toString());
        patient.put("Age", age.getText().toString());
        patient.put("Symptoms", symptoms.getText().toString());
        patient.put("Timings", 0);
        patient.put("Date", 0);
        patient.put("Accepted", 0);
        patient.put("Denied", 0);
        patient.put("Completed", 0);

        db.collection("Doctor").document(doctorId)
                .collection("PatientRequests").document(userID)
                .set(patient)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        Toast.makeText(PatientRegActivity.this, "Appoinment requested.Wait to be accepted", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        db.collection("Patient").document(userID)
                .collection("PatientRequests").document(doctorId)
                .set(patient)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        finish();

    }
}
