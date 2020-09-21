package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PatientRequestList extends AppCompatActivity {

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ACTUALUSERID = "actualuserid";

    RecyclerView recyclerView;
    PatientRequestAdapter patientRequestAdapter;

    private static final String TAG = "PatientRequestList";
    ArrayList<PatientClass> patients = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView noReq;
    ProgressBar progressBar;

    String userID = "NAN";

    int start, end;

    String currentDate;
    String currentTime;

    String date;
    int currentHour;
    int dateFound = 0;

    String totalItems = "";

    int count = 1;

    int exists = 0;

    int reqPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_request_list);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userID = sharedPreferences.getString(ACTUALUSERID, "NAN");

        progressBar = findViewById(R.id.patientListProgressBar);
        noReq = findViewById(R.id.patientNoReq);

        recyclerView = findViewById(R.id.patientList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initialiseRecyclerView();

        getStoredData();

        initialiseDbListener();
    }

    private void initialiseDbListener() {
        db.collection("Doctor").document(userID)
                .collection("PatientRequests")
                .whereEqualTo("Accepted",0)
                .whereEqualTo("Denied", 0)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                                ? "Local" : "Server";

                        if(source == "Server")
                        {
                            patients.clear();

                            if(snapshot.size() == 0)
                            {
                                noReq.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                noReq.setVisibility(View.GONE);
                            }

                            for (QueryDocumentSnapshot document : snapshot) {

                                if(document.get("Name") != null && document.get("Denied") != "1") {

                                    String patientId  = document.get("PatientId").toString();
                                    String name = document.get("Name").toString();
                                    String symp = document.get("Symptoms").toString();
                                    String age = document.get("Age").toString();

                                    start = Integer.parseInt(document.get("OrigStart").toString());
                                    end = Integer.parseInt(document.get("OrigEnd").toString());

                                    PatientClass patientClass = new PatientClass(patientId, name, symp, age);

                                    patients.add(patientClass);

                                }
                            }

                            initialiseRecyclerView();
                        }

                    }
                });
    }

    private void getStoredData() {
        db.collection("Doctor").document(userID)
                .collection("PatientRequests")
                .whereEqualTo("Accepted",0)
                .whereEqualTo("Denied", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {

                            patients.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());


                                if(document.get("Name") != null && document.get("Denied") != "1") {

                                    String patientId  = document.get("PatientId").toString();
                                    String name = document.get("Name").toString();
                                    String symp = document.get("Symptoms").toString();
                                    String age = document.get("Age").toString();

                                    PatientClass patientClass = new PatientClass(patientId, name, symp, age);

                                    patients.add(patientClass);

                                }
                            }

                            if(patients.size() == 0)
                            {
                                noReq.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                noReq.setVisibility(View.GONE);
                            }

                            initialiseRecyclerView();

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void initialiseRecyclerView() {
        patientRequestAdapter = new PatientRequestAdapter(this, patients, new PatientRequestAdapter.PatientListClick() {
            @Override
            public void onEvent(int pos, String type) {

                if(type == "Yes")
                {
                    reqPos = pos;
                    assignDate(pos);
                }
                else
                {
                    rejectDb(pos);
                }

            }
        });
        recyclerView.setAdapter(patientRequestAdapter);

    }

    private void rejectDb(int pos) {
        db.collection("Doctor").document(userID)
                .collection("PatientRequests").document(patients.get(pos).getUserId())
                .update("Denied", 1)
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

        db.collection("Patient").document(patients.get(pos).getUserId())
                .collection("PatientRequests").document(userID)
                .update("Denied", 1)
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

    private void acceptDb(final int pos) {

        reqPos = pos;

        db.collection("Doctor").document(userID)
                .collection("PatientRequests").document(patients.get(pos).getUserId())
                .update("Accepted", 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                        assignDate(pos);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        db.collection("Patient").document(patients.get(pos).getUserId())
                .collection("PatientRequests").document(userID)
                .update("Accepted", 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                        assignDate(pos);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }

    private void assignDate(int pos) {

        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        Calendar c = Calendar.getInstance();

//        Date now = c.getTime();
//        c.add(Calendar.DATE, 1);

        date = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        currentHour +=1;

        if(currentHour < start )
        {
            currentHour = start;
        }
        else if( currentHour >= end)
        {
            c.add(Calendar.DATE, 1);
            date = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
            currentHour = start;
        }

        while( currentHour <= end)
        {
            if(exists == 1)
            {
                break;
            }
            db.collection("Doctor").document("DoctorPatientVisit")
                    .collection(userID).document("Allotted Dates")
                    .collection(date).document(String.valueOf(currentHour))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if(exists == 0) {

                                if (task.isSuccessful()) {
                                    final DocumentSnapshot document = task.getResult();
                                    final int newDocId = Integer.parseInt(document.getId());


                                    Map<String, Object> newCountVar = new HashMap<>();
                                    newCountVar.put("count", 1);



                                    if (document.exists()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                        int oldCountIncr = Integer.parseInt(document.get("count").toString()) + 1;
                                        Map<String, Object> oldcountVar = new HashMap<>();
                                        oldcountVar.put("count", oldCountIncr);

                                        if (Integer.parseInt(document.get("count").toString()) < 10) {

                                            int count = Integer.parseInt(document.get("count").toString());

                                            Log.d(TAG, "Getting count in document");

                                            db.collection("Stores").document("DoctorPatientVisit")
                                                    .collection(userID).document("Allotted Dates")
                                                    .collection(date).document(String.valueOf(currentHour))
                                                    .set(oldcountVar)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully written!");

                                                            Log.d(TAG, "Setting count + 1 in timeslot document");

                                                            exists = 1;

                                                            int finalHour = Integer.parseInt(document.getId().toString());

                                                            setPatientVisit( date, finalHour);

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error writing document", e);
                                                            Log.d(TAG, "Can't set count");
                                                        }
                                                    });


                                            exists = 1;
                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
//                                        Toast.makeText(getApplicationContext(), "Doesnt exist", Toast.LENGTH_SHORT).show();

                                        if(exists == 0) {

                                            db.collection("Stores").document("DoctorPatientVisit")
                                                    .collection(userID).document("Allotted Dates")
                                                    .collection(date).document(String.valueOf(newDocId))
                                                    .set(newCountVar)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully written!");

                                                            Log.d(TAG, "Creating new timeslot doc & setting count to 1");

                                                            setPatientVisit( date, newDocId);

                                                            exists = 1;
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error writing document", e);
                                                            Log.d(TAG, "Can't add new document");
                                                        }
                                                    });

                                        }
                                    }
                                }
                            }
                        }
                    });
            if(exists == 0)
            {
                break;
            }
            currentHour ++ ;

            if(currentHour == end)
            {
                c.add(Calendar.DATE, 1);
                date = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
                currentHour = start;
            }
        }

    }

    private void setPatientVisit(String date, int time) {

        int endTime = time + 1;

        String timings = date + " - " + time + " to " + endTime;

        db.collection("Doctor").document(userID)
                .collection("PatientRequests").document(patients.get(reqPos).getUserId())
                .update("Timings", timings)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                        Toast.makeText(PatientRequestList.this, "Date allocated for visit", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        db.collection("Patient").document(patients.get(reqPos).getUserId())
                .collection("PatientRequests").document(userID)
                .update("Timings", timings)
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

        acceptDb(reqPos);

    }

}
