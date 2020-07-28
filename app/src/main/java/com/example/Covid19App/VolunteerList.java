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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class VolunteerList extends AppCompatActivity {

    TextView reqTxt;

    private static final String TAG = "VolunteerList Db Access:";

    ArrayList<String> volunteerId = new ArrayList<String>();
    ArrayList<String> volunteerAddress = new ArrayList<String>();
    ArrayList<String> volunteerNumber = new ArrayList<String>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView volunteerView;

    VolunteerListAdapter volunteerListAdapter;

    Spinner typeSpinner;

    int count = 0;

    int position;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TASKVOLUNTEERID = "taskVolID";
    private static final String VOLUNTEERADDRESS = "india";
    private static final String VOLUNTEERNUMBER = "volNo";

    String types[] = {"Volunteer", "Health", "Essential"};

    String volType = "Volunteer";

    String foodTask = null;
    String foodLat,foodLng, foodAddr, foodId, foodNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_list);

        reqTxt = findViewById(R.id.volListReqTxt);

        volunteerView = findViewById(R.id.volunteerListView);

        Bundle b = getIntent().getExtras();

        if(b != null)
        {
            foodId = b.getString("Id");
            foodTask = b.getString("Task");
            foodNo = b.getString("No");
            foodAddr = b.getString("Address");
            foodLat = b.getString("Lat");
            foodLng = b.getString("Lng");

//            Toast.makeText(this, foodId, Toast.LENGTH_SHORT).show();
        }

        typeSpinner = findViewById(R.id.volunteerListSpinner);
        typeSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types));

//        Toast.makeText(this, userID, Toast.LENGTH_SHORT).show();

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = typeSpinner.getSelectedItem().toString();

                volType = type;

//                Toast.makeText(VolunteerList.this, volType, Toast.LENGTH_SHORT).show();

                if(type.equals("Volunteer"))
                {
                    getStoredVolunteerData();
                    initialiseVolunteerDbListener();
                }
                else if(type.equals("Health"))
                {
                    getStoredHealthWorkerData();
                    initialiseHealthWorkerDbListener();
                }
                else
                {
                    getStoredEssentialWorkerData();
                    initialiseEssentialWorkerDbListener();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        volunteerView.setLayoutManager(new LinearLayoutManager(this));

        volunteerListAdapter = new VolunteerListAdapter(this, volunteerId, volunteerAddress, volunteerNumber, new VolunteerListAdapter.VolunteerListClick() {
            @Override
            public void onEvent(int pos) {
                position = pos;
                getProductLocation();
                Intent intent = new Intent(getApplicationContext(), AdminSetTask.class);
                intent.putExtra("Type", volType);

                if(foodTask != null)
                {
                    intent.putExtra("Task", foodTask);
                    intent.putExtra("Id", foodId);
                    intent.putExtra("No", foodNo);
                    intent.putExtra("Address", foodAddr);
                    intent.putExtra("Lat", foodLat);
                    intent.putExtra("Lng", foodLng);
                }
                startActivity(intent);
            }
        });
        volunteerView.setAdapter(volunteerListAdapter);
        Log.d(TAG, "Recycler view initialisation");


        getStoredVolunteerData();

        initialiseVolunteerDbListener();
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

    private void getStoredVolunteerData() {

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

//                                    Log.d(TAG, volunteerId.get(count));
//                                    Log.d(TAG, volunteerAddress.get(count));
//
//                                    count++;
                                }
                            }

                            if(volunteerId.size() == 0)
                            {
                                reqTxt.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                reqTxt.setVisibility(View.GONE);
                            }

                            recyclerViewInitialise();

                            progressDialog.dismiss();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    private void getStoredHealthWorkerData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Health worker List");
        progressDialog.show();

        db.collection("Worker")
                .whereEqualTo("Accepted",1)
                .whereEqualTo("Assigned", 0)
                .whereEqualTo("Type", "Health")
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


                                if(document.get("Number") != null && document.get("Home") != null && document.get("UserID") != null) {
                                    volunteerId.add(document.get("UserID").toString());
                                    volunteerAddress.add(document.get("Home").toString());
                                    volunteerNumber.add(document.get("Number").toString());

//                                    Log.d(TAG, volunteerId.get(count));
//                                    Log.d(TAG, volunteerAddress.get(count));
//
//                                    count++;
                                }
                            }

                            if(volunteerId.size() == 0)
                            {
                                reqTxt.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                reqTxt.setVisibility(View.GONE);
                            }

                            recyclerViewInitialise();

                            progressDialog.dismiss();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    private void getStoredEssentialWorkerData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Essential worker List");
        progressDialog.show();

        db.collection("Worker")
                .whereEqualTo("Accepted",1)
                .whereEqualTo("Assigned", 0)
                .whereEqualTo("Type", "Essential")
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


                                if(document.get("Number") != null && document.get("Home") != null && document.get("UserID") != null) {
                                    volunteerId.add(document.get("UserID").toString());
                                    volunteerAddress.add(document.get("Home").toString());
                                    volunteerNumber.add(document.get("Number").toString());

//                                    Log.d(TAG, volunteerId.get(count));
//                                    Log.d(TAG, volunteerAddress.get(count));
//
//                                    count++;
                                }
                            }

                            if(volunteerId.size() == 0)
                            {
                                reqTxt.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                reqTxt.setVisibility(View.GONE);
                            }

                            recyclerViewInitialise();

                            progressDialog.dismiss();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    private void initialiseVolunteerDbListener() {

        db.collection("VolunteerID")
                .whereEqualTo("Assigned", 0)
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

                            if(snapshot.size() == 0)
                            {
                                reqTxt.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                reqTxt.setVisibility(View.GONE);
                            }

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

                                    Intent intent = new Intent(getApplicationContext(), AdminSetTask.class);
                                    intent.putExtra("Type", volType);

                                    if(foodTask != null)
                                    {
                                        intent.putExtra("Task", foodTask);
                                        intent.putExtra("Id", foodId);
                                        intent.putExtra("No", foodNo);
                                        intent.putExtra("Address", foodAddr);
                                        intent.putExtra("Lat", foodLat);
                                        intent.putExtra("Lng", foodLng);
                                    }
                                    startActivity(intent);
                                }
                            });
                            volunteerView.setAdapter(volunteerListAdapter);

//                            storeListAdapter.notifyDataSetChanged();
                        }

                    }
                });

    }

    private void initialiseHealthWorkerDbListener() {

        db.collection("Worker")
                .whereEqualTo("Accepted",1)
                .whereEqualTo("Assigned", 0)
                .whereEqualTo("Type", "Health")
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

                            if(snapshot.size() == 0)
                            {
                                reqTxt.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                reqTxt.setVisibility(View.GONE);
                            }

                            for (QueryDocumentSnapshot document : snapshot) {
                                if(document.get("Number") != null && document.get("Home") != null && document.get("UserID") != null) {
                                    volunteerId.add(document.get("UserID").toString());
                                    volunteerAddress.add(document.get("Home").toString());
                                    volunteerNumber.add(document.get("Number").toString());

                                }
                            }

                            volunteerListAdapter = new VolunteerListAdapter(VolunteerList.this, volunteerId, volunteerAddress, volunteerNumber, new VolunteerListAdapter.VolunteerListClick() {
                                @Override
                                public void onEvent(int pos) {
                                    position = pos;
                                    getProductLocation();

                                    Intent intent = new Intent(getApplicationContext(), AdminSetTask.class);
                                    intent.putExtra("Type", volType);

                                    if(foodTask != null)
                                    {
                                        intent.putExtra("Task", foodTask);
                                        intent.putExtra("Id", foodId);
                                        intent.putExtra("No", foodNo);
                                        intent.putExtra("Address", foodAddr);
                                        intent.putExtra("Lat", foodLat);
                                        intent.putExtra("Lng", foodLng);
                                    }
                                    startActivity(intent);
                                }
                            });
                            volunteerView.setAdapter(volunteerListAdapter);

//                            storeListAdapter.notifyDataSetChanged();
                        }

                    }
                });

    }

    private void initialiseEssentialWorkerDbListener() {

        db.collection("Worker")
                .whereEqualTo("Accepted",1)
                .whereEqualTo("Assigned", 0)
                .whereEqualTo("Type", "Essential")
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

                            if(snapshot.size() == 0)
                            {
                                reqTxt.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                reqTxt.setVisibility(View.GONE);
                            }

                            for (QueryDocumentSnapshot document : snapshot) {
                                if(document.get("Number") != null && document.get("Home") != null && document.get("UserID") != null) {
                                    volunteerId.add(document.get("UserID").toString());
                                    volunteerAddress.add(document.get("Home").toString());
                                    volunteerNumber.add(document.get("Number").toString());

                                }
                            }

                            volunteerListAdapter = new VolunteerListAdapter(VolunteerList.this, volunteerId, volunteerAddress, volunteerNumber, new VolunteerListAdapter.VolunteerListClick() {
                                @Override
                                public void onEvent(int pos) {
                                    position = pos;
                                    getProductLocation();

                                    Intent intent = new Intent(getApplicationContext(), AdminSetTask.class);
                                    intent.putExtra("Type", volType);

                                    if(foodTask != null)
                                    {
                                        intent.putExtra("Task", foodTask);
                                        intent.putExtra("Id", foodId);
                                        intent.putExtra("No", foodNo);
                                        intent.putExtra("Address", foodAddr);
                                        intent.putExtra("Lat", foodLat);
                                        intent.putExtra("Lng", foodLng);
                                    }
                                    startActivity(intent);
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

                Intent intent = new Intent(getApplicationContext(), AdminSetTask.class);
                intent.putExtra("Type", volType);

                if(foodTask != null)
                {
                    intent.putExtra("Task", foodTask);
                    intent.putExtra("Id", foodId);
                    intent.putExtra("No", foodNo);
                    intent.putExtra("Address", foodAddr);
                    intent.putExtra("Lat", foodLat);
                    intent.putExtra("Lng", foodLng);
                }
                startActivity(intent);
            }
        });
        volunteerView.setAdapter(volunteerListAdapter);
        Log.d(TAG, "Recycler view initialisation");
        volunteerView.setLayoutManager(new LinearLayoutManager(this));

//        storeListAdapter.notifyDataSetChanged();
    }

}
