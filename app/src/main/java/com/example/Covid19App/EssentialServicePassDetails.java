package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.EventLog;
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

public class EssentialServicePassDetails extends AppCompatActivity {

    private static final String TAG = "EssentialServicePassDetails";
    TextView name, no, home, dest, date, org, desig, id;
    ImageView img;
    Button acceptBtn, rejectBtn;

    String passUserId;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_essential_service_pass_details);

        storageReference = FirebaseStorage.getInstance().getReference("EssentialServicePass");

        name = findViewById(R.id.espdName);
        no = findViewById(R.id.espdNumber);
        home = findViewById(R.id.espdHome);
        dest = findViewById(R.id.espdDest);
        date = findViewById(R.id.espdDate);
        org = findViewById(R.id.espdOrg);
        desig = findViewById(R.id.espdDesig);
        id = findViewById(R.id.espdId);

        img = findViewById(R.id.espdImg);

        acceptBtn = findViewById(R.id.espdAcceptBtn);
        rejectBtn = findViewById(R.id.espdRejectBtn);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                passUserId = null;
                Toast.makeText(this, "Unable to get User Id", Toast.LENGTH_SHORT).show();
            } else {
                passUserId= extras.getString("PassUserId");
                retrieveDetails();
            }
        } else {
            //Do nothing
        }

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptPass();
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectPass();
            }
        });

    }

    private void acceptPass() {
        db.collection("ServicePass").document(passUserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        if(document.get("Accepted").toString().equals("1"))
                        {
                            Toast.makeText(EssentialServicePassDetails.this, "This pass request has been accepted by another admin", Toast.LENGTH_SHORT).show();
                        }
                        else if(document.get("Rejected").toString().equals("1"))
                        {
                            Toast.makeText(EssentialServicePassDetails.this, "This pass request has been rejected by another admin", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            db.collection("ServicePass").document(passUserId)
                                    .update("Accepted", 1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");

                                            Toast.makeText(EssentialServicePassDetails.this, "This pass request has been accepted & database has been updated!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating document", e);

                                            Toast.makeText(EssentialServicePassDetails.this, "Unable to change pass status in database!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                    } else {
                        Log.d(TAG, "No such document");

                        Toast.makeText(EssentialServicePassDetails.this, "Unable to access user details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());

                    Toast.makeText(EssentialServicePassDetails.this, "Unable to access db", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void rejectPass() {

        db.collection("ServicePass").document(passUserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        if(document.get("Accepted").toString().equals("1"))
                        {
                            Toast.makeText(EssentialServicePassDetails.this, "This pass request has been accepted by another admin", Toast.LENGTH_SHORT).show();
                        }
                        else if(document.get("Rejected").toString().equals("1"))
                        {
                            Toast.makeText(EssentialServicePassDetails.this, "This pass request has been rejected by another admin", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            db.collection("ServicePass").document(passUserId)
                                    .update("Rejected", 1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");

                                            Toast.makeText(EssentialServicePassDetails.this, "This pass request has been rejected & database has been updated!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating document", e);

                                            Toast.makeText(EssentialServicePassDetails.this, "Unable to change pass status in database!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                    } else {
                        Log.d(TAG, "No such document");

                        Toast.makeText(EssentialServicePassDetails.this, "Unable to access user details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());

                    Toast.makeText(EssentialServicePassDetails.this, "Unable to access db", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void retrieveDetails() {

        db.collection("ServicePass").document(passUserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        if(document.get("Accepted").equals("1"))
                        {
                            Toast.makeText(EssentialServicePassDetails.this, "This pass request has been accepted by another admin", Toast.LENGTH_SHORT).show();
                        }
                        else if(document.get("Rejected").equals("1"))
                        {
                            Toast.makeText(EssentialServicePassDetails.this, "This pass request has been rejected by another admin", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            name.setText(document.get("Name").toString());
                            no.setText(document.get("Number").toString());
                            home.setText(document.get("Origin").toString());
                            dest.setText(document.get("Destination").toString());
                            date.setText(document.get("Date").toString());
                            org.setText(document.get("Company").toString());
                            desig.setText(document.get("Prof").toString());
                            id.setText(document.get("Id").toString());

                            StorageReference ref = storageReference.child(passUserId);

                            Glide.with(EssentialServicePassDetails.this)
                                    .load(ref)
                                    .into(img);
                        }

                    } else {
                        Log.d(TAG, "No such document");

                        Toast.makeText(EssentialServicePassDetails.this, "Unable to access user details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());

                    Toast.makeText(EssentialServicePassDetails.this, "Unable to access db", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
