package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class VolunteerTaskActivity extends AppCompatActivity {

    private static final String TAG = "VolunteerTaskActivity:";
    TextView adminId, brief, desc, address;
    TextView noTask;

    Button acceptBtn, denyBtn, completedBtn, locBtn;

    String userId;
    String docId;
    String adminDocId;

    String adminNo;
    String taskBrief;

    String lat, lng;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ACTUALUSERID = "actualuserid";


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_task);

        adminId = findViewById(R.id.volunteerAdminTxt);
        brief = findViewById(R.id.volunteerBriefTxt);
        desc = findViewById(R.id.volunteerTaskDescTxt);
        address = findViewById(R.id.volunteerAddressTxt);
        noTask = findViewById(R.id.volunteerTaskStatusTxt);

        acceptBtn = findViewById(R.id.volunteerAcceptBtn);
        denyBtn = findViewById(R.id.volunteerDenyBtn);
        completedBtn = findViewById(R.id.volunteerCompletedBtn);
        locBtn = findViewById(R.id.volTaskLocBtn);

        loadUserId();
        getTaskData();


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS))
            {

            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptTask();
            }
        });

        denyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectTask();
            }
        });

        completedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeTask();
            }
        });

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

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Thanks for permitting", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Please permit to send task sms to volunteer", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void completeTask() {

        String taskSMS = "The task:" + "\n" + taskBrief + "\n" + "has been completed by this volunteer!Contact him & validate completion in the app task section.";

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
        {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(adminNo, null, taskSMS, null, null);
            Toast.makeText(this, "SMS sent!", Toast.LENGTH_SHORT).show();
        }


        Toast.makeText(this, "Task Assigned!Wait for volunteer confirmation!", Toast.LENGTH_SHORT).show();



        db.collection("VolunteerID").document(userId).collection("Tasks").document(docId)
                .update("CompletionRequest", 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        db.collection("VolunteerID").document(userId).collection("Tasks").document(docId)
                .update("Assigned", 0)//Only this is assigned to 0 since we wont know name of task doc present in volunteerID & within volunteers Tasks ...even admin doc is given 0 after
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        db.collection("VolunteerID").document(userId)
                .update("Assigned", 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        db.collection("DataStorage").document("Admin").collection("AdminCollection").document(adminDocId).collection("Tasks")
                .whereEqualTo("Assigned", 1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                if(document.getData() != null)
                                {
                                    String taskDocID = document.getId();
                                    String volunteerID = document.get("VolunteerID").toString();

                                    if(volunteerID.equals(userId))
                                    {
                                        db.collection("DataStorage").document("Admin").collection("AdminCollection").document(adminDocId).collection("Tasks").document(taskDocID)
                                                .update("CompletionRequest", 1)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error updating document", e);
                                                    }
                                                });

                                        db.collection("DataStorage").document("Admin").collection("AdminCollection").document(adminDocId).collection("Tasks").document(taskDocID)
                                                .update("Assigned", 0)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error updating document", e);
                                                    }
                                                });

                                        Toast.makeText(VolunteerTaskActivity.this, "Task completed & SMS sent to admin", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



        stopLocationService();

        finish();
        startActivity(new Intent(this,LauncherActivity.class));
    }

    private void rejectTask() {

        String taskSMS = "The task:" + "\n" + taskBrief + "\n" + "has been rejected by the volunteer.";

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
        {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(adminNo, null, taskSMS, null, null);
            Toast.makeText(this, "SMS sent!", Toast.LENGTH_SHORT).show();
        }

        db.collection("VolunteerID").document(userId).collection("Tasks").document(docId)
                .update("Rejected", 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        db.collection("VolunteerID").document(userId).collection("Tasks").document(docId)
                .update("Accepted", 0)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        db.collection("VolunteerID").document(userId)
                .update("Assigned", 0)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        db.collection("DataStorage").document("Admin").collection("AdminCollection").document(adminDocId).collection("Tasks")
                .whereEqualTo("Assigned", 1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                if(document.getData() != null)
                                {
                                    if(document.get("VolunteerID").toString() == userId)
                                    {
                                        db.collection("DataStorage").document("Admin").collection("AdminCollection").document(adminDocId).collection("Tasks").document(document.getId())
                                                .update("Rejected", 1)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error updating document", e);
                                                    }
                                                });

                                        db.collection("DataStorage").document("Admin").collection("AdminCollection").document(adminDocId).collection("Tasks").document(document.getId())
                                                .update("Accepted", 0)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error updating document", e);
                                                    }
                                                });
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        Toast.makeText(this, "Task rejected & msg sent to admin", Toast.LENGTH_SHORT).show();

        stopLocationService();
    }

    private void acceptTask() {

        String taskSMS = "The task:" + "\n" + taskBrief + "\n" + "has been accepted by this volunteer!Contact him monitor the task.";

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
        {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(adminNo, null, taskSMS, null, null);
            Toast.makeText(this, "SMS sent!", Toast.LENGTH_SHORT).show();
        }

        db.collection("VolunteerID").document(userId).collection("Tasks").document(docId)
                .update("Accepted", 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        db.collection("DataStorage").document("Admin").collection("AdminCollection").document(adminDocId).collection("Tasks")
                .whereEqualTo("Assigned", 1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                if(document.getData() != null)
                                {
                                    String adminDoc = document.getId();
                                    String volunteerId = document.get("VolunteerID").toString();

                                    if(volunteerId.equals(userId))
                                    {
                                        db.collection("DataStorage").document("Admin").collection("AdminCollection").document(adminDocId).collection("Tasks").document(adminDoc)
                                                .update("Accepted", 1)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error updating document", e);
                                                    }
                                                });
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        completedBtn.setVisibility(View.VISIBLE);

        Toast.makeText(this, "Task Accepted", Toast.LENGTH_SHORT).show();

        startLocationService();

    }

    private synchronized void startLocationService() {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Task accepted!User location will now be tracked to ensure social distancing.This can be disabled by rejecting or completing the task.")
                    .setTitle("Alert:")
                    .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent serviceIntent = new Intent(VolunteerTaskActivity.this, LocationMonitorService.class);
                            serviceIntent.putExtra("type", "vol");

                            ContextCompat.startForegroundService(VolunteerTaskActivity.this, serviceIntent);

                            Toast.makeText(getApplicationContext(), "Items Ordered successfully", Toast.LENGTH_LONG).show();

                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

    }

    private void stopLocationService()
    {
        Intent serviceIntent = new Intent(this, VolunteerLocationService.class);
        stopService(serviceIntent);

        db.collection("Location").document(userId)
                .update("Volunteer", 0)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        db.collection("Location").document(userId)
                .update("Cancelled", 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        finish();
        startActivity(new Intent(this, LauncherActivity.class));

        Toast.makeText(this, "Location monitor stopped", Toast.LENGTH_SHORT).show();
    }


    private void getTaskData() {

//        Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();

        final ProgressDialog progressDialogdb = new ProgressDialog(this);
        progressDialogdb.setMessage("Checking if tasks issued..");
        progressDialogdb.show();

//        db.collection("VolunteerID").document(userId)
//                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//
//                        if(document.get("Assigned").toString() == "1")
//                        {

                            db.collection("VolunteerID").document(userId).collection("Tasks")
                                    .whereEqualTo("Assigned", 1)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {

                                                progressDialogdb.dismiss();

                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                                    if(document.getData() != null)
                                                    {
                                                        docId = document.getId();

                                                        adminDocId = document.get("AdminID").toString();
                                                        adminNo = document.get("AdminNo").toString();
                                                        taskBrief = document.get("Brief").toString();
                                                        
                                                        lat = document.get("Latitude").toString();
                                                        lng = document.get("Longitude").toString();

                                                        adminId.setText("ADMIN ID: " + document.get("AdminID").toString());
                                                        brief.setText("BRIEF: " + document.get("Brief").toString());
                                                        desc.setText("DESCRIPTION: " + document.get("Description").toString());
                                                        address.setText("ADDRESS: " + document.get("Address").toString());

                                                        adminId.setVisibility(View.VISIBLE);
                                                        brief.setVisibility(View.VISIBLE);
                                                        desc.setVisibility(View.VISIBLE);
                                                        address.setVisibility(View.VISIBLE);

                                                        acceptBtn.setVisibility(View.VISIBLE);
                                                        denyBtn.setVisibility(View.VISIBLE);
                                                        locBtn.setVisibility(View.VISIBLE);

                                                        noTask.setVisibility(View.GONE);

                                                        progressDialogdb.dismiss();
                                                    }
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                                Toast.makeText(VolunteerTaskActivity.this, "Error retrieving from database", Toast.LENGTH_SHORT).show();
                                                progressDialogdb.dismiss();
                                            }
                                        }
                                    });
//                        }
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });



    }

    private void loadUserId() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getString(ACTUALUSERID, "");

    }
}
