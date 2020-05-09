package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class VolunteerList extends AppCompatActivity {

    private static final String TAG = "VolunteerList Db Access:";

    ArrayList<String> volunteerId = new ArrayList<String>();
    ArrayList<String> volunteerAddress = new ArrayList<String>();
    ArrayList<String> volunteerNumber = new ArrayList<String>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView volunteerView;

    VolunteerListAdapter volunteerListAdapter;

    int count=0;

    int position;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TASKVOLUNTEERID = "taskVolID";
    private static final String VOLUNTEERADDRESS = "india";
    private static final String VOLUNTEERNUMBER = "volNo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_list);

        volunteerView = findViewById(R.id.volunteerListView);

        volunteerView.setLayoutManager(new LinearLayoutManager(this));

        volunteerListAdapter = new VolunteerListAdapter(this, volunteerId, volunteerAddress, volunteerNumber, new VolunteerListAdapter.VolunteerListClick() {
            @Override
            public void onEvent(int pos) {
                position = pos;
                getProductLocation();
                startActivity(new Intent(getApplicationContext(), AdminSetTask.class));
            }
        });
        volunteerView.setAdapter(volunteerListAdapter);
        Log.d(TAG, "Recycler view initialisation");


        getStoredData();

        initialiseDbListener();
    }

    private void getProductLocation() {

        String volID = volunteerId.get(position);
        String volAddress = volunteerAddress.get(position);
        String volNumber = volunteerNumber.get(position);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TASKVOLUNTEERID, volID);
        editor.putString(VOLUNTEERADDRESS, volAddress);
        editor.putString(VOLUNTEERNUMBER, volNumber);
        editor.apply();

    }

    private void getStoredData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Volunteer List");
        progressDialog.show();

        db.collection("VolunteerID")
                .whereEqualTo("Assigned", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            volunteerId.clear();
                            volunteerAddress.clear();
                            volunteerNumber.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());


                                if(document.get("Number") != null && document.get("Address") != null && document.get("UserID") != null) {
                                    volunteerId.add(document.get("UserID").toString());
                                    volunteerAddress.add(document.get("Address").toString());
                                    volunteerNumber.add(document.get("Number").toString());

                                    Log.d(TAG, volunteerId.get(count));
                                    Log.d(TAG, volunteerAddress.get(count));

                                    count++;
                                }
                            }

                            recyclerViewInitialise();

                            progressDialog.dismiss();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    private void initialiseDbListener() {

        db.collection("VolunteerID")
//                .whereEqualTo("state", "CA")
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
                            volunteerId.clear();
                            volunteerAddress.clear();
                            volunteerNumber.clear();

                            for (QueryDocumentSnapshot document : snapshot) {
                                if(document.get("Number") != null && document.get("Address") != null && document.get("UserID") != null) {
                                    volunteerId.add(document.get("UserID").toString());
                                    volunteerAddress.add(document.get("Address").toString());
                                    volunteerNumber.add(document.get("Number").toString());

                                }
                            }

                            volunteerListAdapter = new VolunteerListAdapter(VolunteerList.this, volunteerId, volunteerAddress, volunteerNumber, new VolunteerListAdapter.VolunteerListClick() {
                                @Override
                                public void onEvent(int pos) {
                                    position = pos;
                                    getProductLocation();
                                    startActivity(new Intent(getApplicationContext(), AdminSetTask.class));
                                }
                            });
                            volunteerView.setAdapter(volunteerListAdapter);

//                            storeListAdapter.notifyDataSetChanged();
                        }

                    }
                });

    }

    private void recyclerViewInitialise() {

        volunteerListAdapter = new VolunteerListAdapter(this, volunteerId, volunteerAddress, volunteerNumber, new VolunteerListAdapter.VolunteerListClick() {
            @Override
            public void onEvent(int pos) {
                position = pos;
                getProductLocation();
                startActivity(new Intent(getApplicationContext(), AdminSetTask.class));
            }
        });
        volunteerView.setAdapter(volunteerListAdapter);
        Log.d(TAG, "Recycler view initialisation");
        volunteerView.setLayoutManager(new LinearLayoutManager(this));

//        storeListAdapter.notifyDataSetChanged();
    }

}
