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

public class WorkerRequestList extends AppCompatActivity {

    TextView noReq;

    private static final String TAG = "WorkerRequestList";
    ArrayList<WorkerClass> workersList = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView workerReqList;
    WorkerRequestListAdapter workerReqListAdapter;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_request_list);

        noReq = findViewById(R.id.wrReqTxt);

        progressBar = findViewById(R.id.wrProgressBar);

        workerReqList = findViewById(R.id.wrList);
        workerReqList.setLayoutManager(new LinearLayoutManager(this));

        initialiseRecyclerView();

        getStoredData();

        initialiseDbListener();

    }

    private void initialiseDbListener() {
        db.collection("Worker")
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
                            workersList.clear();

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
                                    String home = document.get("Home").toString();
                                    String work = document.get("Work").toString();
                                    String type = document.get("Type").toString();
                                    String org = document.get("Org").toString();
                                    String prof = document.get("Prof").toString();
                                    String id = document.get("UserID").toString();
                                    String lat = document.get("Latitude").toString();
                                    String lng = document.get("Longitude").toString();

                                    WorkerClass worker = new WorkerClass(name, no, home, work, type, org, prof, id, lat, lng);

                                    workersList.add(worker);

                                }
                            }

                            initialiseRecyclerView();
                        }

                    }
                });
    }

    private void getStoredData() {
        db.collection("Worker")
                .whereEqualTo("Accepted", 0)
                .whereEqualTo("Denied", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {

                            workersList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());


                                if(document.get("Name") != null && document.get("Denied") != "1") {

                                    String name = document.get("Name").toString();
                                    String no = document.get("Number").toString();
                                    String home = document.get("Home").toString();
                                    String work = document.get("Work").toString();
                                    String type = document.get("Type").toString();
                                    String org = document.get("Org").toString();
                                    String prof = document.get("Prof").toString();
                                    String id = document.get("UserID").toString();
                                    String lat = document.get("Latitude").toString();
                                    String lng = document.get("Longitude").toString();

                                    WorkerClass worker = new WorkerClass(name, no, home, work, type, org, prof, id, lat, lng);

                                    workersList.add(worker);

                                }
                            }

                            if(workersList.size() == 0)
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
        workerReqListAdapter = new WorkerRequestListAdapter(this, workersList);
        workerReqList.setAdapter(workerReqListAdapter);
    }
}
