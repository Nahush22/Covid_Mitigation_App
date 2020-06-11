package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

public class EssentialServicePassList extends AppCompatActivity {

    TextView noReq;

    private static final String TAG = "EssentialServicePassList";
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> home = new ArrayList<>();
    ArrayList<String> prof = new ArrayList<>();
    ArrayList<String> id = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView servicePassList;
    EssentialServiceListAdapter essentialServiceListAdapter;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_essential_service_pass_list);

        noReq = findViewById(R.id.espReqTxt);

        progressBar = findViewById(R.id.espProgressBar);

        servicePassList = findViewById(R.id.espList);
        servicePassList.setLayoutManager(new LinearLayoutManager(this));

        initialiseRecyclerView();

        getStoredData();

        initialiseDbListener();
    }

    private void initialiseDbListener() {

        db.collection("ServicePass")
                .whereEqualTo("Accepted", 0)
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
                            name.clear();
                            home.clear();
                            prof.clear();
                            id.clear();

                            if(snapshot.size() == 0)
                            {
                                noReq.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                noReq.setVisibility(View.GONE);
                            }

                            for (QueryDocumentSnapshot document : snapshot) {

                                if(document.get("Name") != null && document.get("Origin") != null && document.get("Prof") != null && document.get("UserId") != null && document.get("Rejected") != "1") {
                                    name.add(document.get("Name").toString());
                                    home.add(document.get("Origin").toString());
                                    prof.add(document.get("Prof").toString());
                                    id.add(document.get("UserId").toString());

                                }
                            }

                            initialiseRecyclerView();
                        }

                    }
                });

    }

    private void getStoredData() {

        db.collection("ServicePass")
                .whereEqualTo("Accepted", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {

                            name.clear();
                            home.clear();
                            prof.clear();
                            id.clear();



                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());


                                if(document.get("Name") != null && document.get("Origin") != null && document.get("Prof") != null && document.get("UserId") != null && document.get("Rejected") != "1") {
                                    name.add(document.get("Name").toString());
                                    home.add(document.get("Origin").toString());
                                    prof.add(document.get("Prof").toString());
                                    id.add(document.get("UserId").toString());

                                }
                            }

                            if(name.size() == 0)
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
        essentialServiceListAdapter = new EssentialServiceListAdapter(this, name, home, prof, id);
        servicePassList.setAdapter(essentialServiceListAdapter);
    }

}
