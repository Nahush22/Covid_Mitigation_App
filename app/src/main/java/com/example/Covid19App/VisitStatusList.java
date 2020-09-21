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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class VisitStatusList extends AppCompatActivity {

    RecyclerView recyclerView;
    VisitStatusAdapter visitStatusAdapter;

    private static final String TAG = "VisitStatusList";
    ArrayList<VisitClass> visits = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView noReq;

    ProgressBar progressBar;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ACTUALUSERID = "actualuserid";

    String userID = "NAN" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_status_list);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userID = sharedPreferences.getString(ACTUALUSERID, "NAN");

        noReq = findViewById(R.id.visitNoReq);
        progressBar = findViewById(R.id.visitProgressBar);

        recyclerView = findViewById(R.id.visitStatusView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initialiseRecyclerView();

        getStoredData();

        initialiseDbListener();

    }

    private void initialiseDbListener() {
        db.collection("Patient").document(userID)
                .collection("PatientRequests")
                .whereEqualTo("Accepted", 1)
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
                            visits.clear();

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

                                    String id = document.get("DoctorId").toString();
                                    String clinic = document.get("Clinic").toString();
                                    String name = document.get("Name").toString();
                                    String time = document.get("Timings").toString();

                                    VisitClass visitClass = new VisitClass(id, clinic, name, time);

                                    visits.add(visitClass);

                                }
                            }

                            initialiseRecyclerView();
                        }

                    }
                });
    }

    private void getStoredData() {
        db.collection("Patient").document(userID)
                .collection("PatientRequests")
                .whereEqualTo("Accepted", 1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {

                            visits.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());


                                if(document.get("Name") != null && document.get("Denied") != "1") {

                                    String id = document.get("DoctorId").toString();
                                    String clinic = document.get("Clinic").toString();
                                    String name = document.get("Name").toString();
                                    String time = document.get("Timings").toString();

                                    VisitClass visitClass = new VisitClass(id, clinic, name, time);

                                    visits.add(visitClass);

                                }
                            }

                            if(visits.size() == 0)
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
        visitStatusAdapter = new VisitStatusAdapter(this, visits);
        recyclerView.setAdapter(visitStatusAdapter);
    }

}
