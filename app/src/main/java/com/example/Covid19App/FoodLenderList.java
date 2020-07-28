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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FoodLenderList extends AppCompatActivity {

    RecyclerView recyclerView;

    TextView reqTxt;

    private static final String TAG = "VolunteerList Db Access:";

    ArrayList<FoodClass> food = new ArrayList<FoodClass>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FoodListAdapter foodListAdapter;

    int position;

    private static final String SHARED_PREFS = "sharedPrefs";

    String types[] = {"Volunteer", "Health", "Essential"};

    String volType = "Volunteer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_lender_list);

        reqTxt = findViewById(R.id.foodNoReq);

        recyclerView = findViewById(R.id.foodRecyclerView);

//        Toast.makeText(this, userID, Toast.LENGTH_SHORT).show();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        foodListAdapter = new FoodListAdapter(this, food);
        recyclerView.setAdapter(foodListAdapter);
        Log.d(TAG, "Recycler view initialisation");


        getStoredData();

        initialiseDbListener();
    }

    private void getStoredData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Volunteer List");
        progressDialog.show();

        db.collection("Food")
                .whereEqualTo("Assigned", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            food.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());


                                if(document.get("Name") != null) {

                                    String id = document.get("UserID").toString();
                                    String name = document.get("Name").toString();
                                    String no = document.get("Number").toString();
                                    String address = document.get("Address").toString();
                                    String type = document.get("Type").toString();
                                    String items = document.get("Items").toString();
                                    String lat = document.get("Latitude").toString();
                                    String lng = document.get("Longitude").toString();

                                    FoodClass foodClass = new FoodClass( id, name, no, address, type, items, lat, lng);
                                    food.add(foodClass);

                                }
                            }

                            if(food.size() == 0)
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

    private void initialiseDbListener() {

        db.collection("Food")
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
                            food.clear();

                            if(snapshot.size() == 0)
                            {
                                reqTxt.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                reqTxt.setVisibility(View.GONE);
                            }

                            for (QueryDocumentSnapshot document : snapshot) {
                                if(document.get("Name") != null) {

                                    String id = document.get("UserID").toString();
                                    String name = document.get("Name").toString();
                                    String no = document.get("Number").toString();
                                    String address = document.get("Address").toString();
                                    String type = document.get("Type").toString();
                                    String items = document.get("Items").toString();
                                    String lat = document.get("Latitude").toString();
                                    String lng = document.get("Longitude").toString();

                                    FoodClass foodClass = new FoodClass( id, name, no, address, type, items, lat, lng);
                                    food.add(foodClass);

                                }
                            }

                            foodListAdapter = new FoodListAdapter(FoodLenderList.this, food);
                            recyclerView.setAdapter(foodListAdapter);

//                            storeListAdapter.notifyDataSetChanged();
                        }

                    }
                });

    }

    private void recyclerViewInitialise() {

        foodListAdapter = new FoodListAdapter(this, food);
        recyclerView.setAdapter(foodListAdapter);
        Log.d(TAG, "Recycler view initialisation");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        storeListAdapter.notifyDataSetChanged();
    }
}
