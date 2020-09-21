package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class DoctorRequestList extends AppCompatActivity {

    TextView noReq;

    private static final String TAG = "WorkerRequestList";
    ArrayList<DoctorClass> doctorList = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView doctorReqList;
    DoctorReqAdapter doctorReqAdapter;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_request_list);

        noReq = findViewById(R.id.doctorReqTxt);

        progressBar = findViewById(R.id.doctorReqProgressBar);

        doctorReqList = findViewById(R.id.doctorReqList);
        doctorReqList.setLayoutManager(new LinearLayoutManager(this));

        initialiseRecyclerView();

        getStoredData();

        initialiseDbListener();
    }

    private void initialiseDbListener() {
        db.collection("Doctor")
                .whereEqualTo("Accepted", 0)
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
                .whereEqualTo("Accepted", 0)
                .whereEqualTo("Denied", 0)
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
        doctorReqAdapter = new DoctorReqAdapter(this, doctorList);
        doctorReqList.setAdapter(doctorReqAdapter);
    }

}
