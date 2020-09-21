package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DoctorDetails extends AppCompatActivity {

    private static final String TAG = "DoctorDetails";
    TextView dName, dNo, dAddr, dClinic, dDomain, dId, dTime;
    ImageView img;
    Button accept, deny;

    String name, no, address, clinic, domain, id, userId, fromTime, toTime;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);

        storageReference = FirebaseStorage.getInstance().getReference("DoctorID");

        dName = findViewById(R.id.docDetailsName);
        dNo = findViewById(R.id.docDetailsNo);
        dAddr = findViewById(R.id.docDetailsAddress);
        dClinic = findViewById(R.id.docDetailsClinic);
        dDomain = findViewById(R.id.docDetailsType);
        dId = findViewById(R.id.docDetailsId);
        dTime = findViewById(R.id.docDetailsTimings);

        img = findViewById(R.id.docDetailsImg);

        accept = findViewById(R.id.docDetailsAccept);
        deny = findViewById(R.id.docDetailsDeny);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Toast.makeText(this, "Unable to fetch user data.Please go back & fill it again...", Toast.LENGTH_SHORT).show();
        } else {
            name= extras.getString("name");
            no= extras.getString("no");
            address= extras.getString("address");
            clinic= extras.getString("clinic");
            domain= extras.getString("domain");
            id= extras.getString("id");
            userId = extras.getString("UserId");
            fromTime= extras.getString("fromTime");
            toTime= extras.getString("toTime");

            dName.setText(name);
            dNo.setText(no);
            dAddr.setText(address);
            dClinic.setText(clinic);
            dDomain.setText(domain);
            dId.setText(id);
            dTime.setText(fromTime + " Hrs to " + toTime + " Hrs");

            StorageReference ref = storageReference.child(userId);

            Glide.with(DoctorDetails.this)
                    .load(ref)
                    .into(img);

        }

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptPass();
            }
        });

        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectPass();
            }
        });

    }

    private void acceptPass() {
        db.collection("Doctor").document(userId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        if(document.get("Accepted").toString().equals("1"))
                        {
                            Toast.makeText(DoctorDetails.this, "This pass request has been accepted by another admin", Toast.LENGTH_SHORT).show();
                        }
                        else if(document.get("Denied").toString().equals("1"))
                        {
                            Toast.makeText(DoctorDetails.this, "This pass request has been rejected by another admin", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            db.collection("Doctor").document(userId)
                                    .update("Accepted", 1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");

                                            Toast.makeText(DoctorDetails.this, "This pass request has been accepted & database has been updated!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating document", e);

                                            Toast.makeText(DoctorDetails.this, "Unable to change pass status in database!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                    } else {
                        Log.d(TAG, "No such document");

                        Toast.makeText(DoctorDetails.this, "Unable to access user details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());

                    Toast.makeText(DoctorDetails.this, "Unable to access db", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void rejectPass() {
        db.collection("Doctor").document(userId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        if(document.get("Accepted").toString().equals("1"))
                        {
                            Toast.makeText(DoctorDetails.this, "This pass request has been accepted by another admin", Toast.LENGTH_SHORT).show();
                        }
                        else if(document.get("Denied").toString().equals("1"))
                        {
                            Toast.makeText(DoctorDetails.this, "This pass request has been rejected by another admin", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            db.collection("Doctor").document(userId)
                                    .update("Denied", 1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");

                                            Toast.makeText(DoctorDetails.this, "This pass request has been rejected & database has been updated!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating document", e);

                                            Toast.makeText(DoctorDetails.this, "Unable to change pass status in database!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                    } else {
                        Log.d(TAG, "No such document");

                        Toast.makeText(DoctorDetails.this, "Unable to access user details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());

                    Toast.makeText(DoctorDetails.this, "Unable to access db", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
