package com.example.Covid19App;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class StoreList extends AppCompatActivity {

    private static final String TAG = "StoreList Db Access:";
    ArrayList<String> storeNames = new ArrayList<String>();
    ArrayList<String> storeAddress = new ArrayList<String>();
    ArrayList<String> storeProductLocation = new ArrayList<String>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView recyclerView;

    int count=0;

    int position;

    StoreListAdapter storeListAdapter;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String KEY = "StoreID";
    private static final String STORENAME = "StoreName";
    private static final String STOREADDRESS = "StoreAddress";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        recyclerView = findViewById(R.id.recyclerView);

//        storeNames.add("Dummy Store");
//        storeAddress.add("Dummy Address");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        storeListAdapter = new StoreListAdapter(this, storeNames, storeAddress, new StoreListAdapter.UserStoreClick() {
            @Override
            public void onEvent(int pos) {
                position = pos;
                getProductLocation();
                finish();
                startActivity(new Intent(getApplicationContext(), userProductList.class));
            }
        });
        recyclerView.setAdapter(storeListAdapter);
        Log.d(TAG, "Recycler view initialisation");


        getStoredData();

        initialiseDbListener();

    }

    private void getProductLocation() {

        String storeId = storeProductLocation.get(position);
        String storeName = storeNames.get(position);
        String mainStoreAddress = storeAddress.get(position);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY, storeId);
        editor.putString(STORENAME, storeName);
        editor.putString(STOREADDRESS, mainStoreAddress);
        editor.apply();

    }

    private void getStoredData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Store List");
        progressDialog.show();

        db.collection("SellerID")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            storeNames.clear();
                            storeAddress.clear();
                            storeProductLocation.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());


                                if(document.get("Name") != null && document.get("Address") != null && document.get("StoreId") != null) {
                                    storeNames.add(document.get("Name").toString());
                                    storeAddress.add(document.get("Address").toString());
                                    storeProductLocation.add(document.get("StoreId").toString());

                                    Log.d(TAG, storeNames.get(count));
                                    Log.d(TAG, storeAddress.get(count));

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

        db.collection("SellerID")
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
                            storeNames.clear();
                            storeAddress.clear();
                            storeProductLocation.clear();

                            for (QueryDocumentSnapshot document : snapshot) {
                                if (document.get("Name") != null && document.get("Address") != null && document.get("StoreId") != null) {
                                    storeNames.add(document.get("Name").toString());
                                    storeAddress.add(document.get("Address").toString());
                                    storeProductLocation.add(document.get("StoreId").toString());
                                }
                            }

                            storeListAdapter = new StoreListAdapter(getApplicationContext(), storeNames, storeAddress, new StoreListAdapter.UserStoreClick() {
                                @Override
                                public void onEvent(int pos) {
                                    position = pos;
                                    getProductLocation();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), userProductList.class));
                                }
                            });
                            recyclerView.setAdapter(storeListAdapter);

//                            storeListAdapter.notifyDataSetChanged();
                        }

                    }
                });

    }

    private void recyclerViewInitialise() {

        storeListAdapter = new StoreListAdapter(getApplicationContext(), storeNames, storeAddress, new StoreListAdapter.UserStoreClick() {
            @Override
            public void onEvent(int pos) {
                position = pos;
                getProductLocation();
                finish();
                startActivity(new Intent(getApplicationContext(), userProductList.class));
            }
        });
        recyclerView.setAdapter(storeListAdapter);
        Log.d(TAG, "Recycler view initialisation");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        storeListAdapter.notifyDataSetChanged();
    }

}
