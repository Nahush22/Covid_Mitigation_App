package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DoctorList extends AppCompatActivity {

    Toolbar toolbar;

    TextView noReq;

    private static final String TAG = "DoctorList";
    ArrayList<DoctorClass> doctorList = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView doctorView;
    DoctorListAdapter doctorListAdapter;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        toolbar = findViewById(R.id.patient_toolbar);

        setSupportActionBar(toolbar);

        noReq = findViewById(R.id.doctorListTxt);

        progressBar = findViewById(R.id.doctorListProgressBar);

        doctorView = findViewById(R.id.docList);
        doctorView.setLayoutManager(new LinearLayoutManager(this));

        initialiseRecyclerView();

        getStoredData();

        initialiseDbListener();
    }

    private void initialiseDbListener() {
        db.collection("Doctor")
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
                            doctorList.clear();

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

                                    String name = document.get("Name").toString();
                                    String no = document.get("Number").toString();
                                    String address = document.get("Address").toString();
                                    String clinic = document.get("Clinic").toString();
                                    String domain = document.get("Domain").toString();
                                    String start = document.get("Start").toString();
                                    String end = document.get("End").toString();
                                    String id = document.get("Id").toString();
                                    String doctorId = document.get("UserID").toString();
                                    String lat = document.get("Latitude").toString();
                                    String lng = document.get("Longitude").toString();

                                    DoctorClass doctorClass = new DoctorClass(name, no, id, doctorId, address, clinic, domain, lat, lng, start, end);

                                    doctorList.add(doctorClass);

                                }
                            }

                            initialiseRecyclerView();
                        }

                    }
                });
    }

    private void getStoredData() {
        db.collection("Doctor")
                .whereEqualTo("Accepted", 1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {

                            doctorList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());


                                if(document.get("Name") != null && document.get("Denied") != "1") {

                                    String name = document.get("Name").toString();
                                    String no = document.get("Number").toString();
                                    String address = document.get("Address").toString();
                                    String clinic = document.get("Clinic").toString();
                                    String domain = document.get("Domain").toString();
                                    String start = document.get("Start").toString();
                                    String end = document.get("End").toString();
                                    String id = document.get("Id").toString();
                                    String doctorId = document.get("UserID").toString();
                                    String lat = document.get("Latitude").toString();
                                    String lng = document.get("Longitude").toString();

                                    DoctorClass doctorClass = new DoctorClass(name, no, id, doctorId, address, clinic, domain, lat, lng, start, end);

                                    doctorList.add(doctorClass);

                                }
                            }

                            if(doctorList.size() == 0)
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
        doctorListAdapter = new DoctorListAdapter(this, doctorList);
        doctorView.setAdapter(doctorListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.patient_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
//            case R.id.orderMenu:
//                break;

            case R.id.visitMenu:
                openVisitMenu();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openVisitMenu() {

        Intent intent = new Intent(this, VisitStatusList.class);
        startActivity(intent);

    }
}
