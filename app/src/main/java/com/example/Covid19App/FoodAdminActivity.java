package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ThrowOnExtraProperties;

public class FoodAdminActivity extends AppCompatActivity {

    private static final String TAG = "FoodAdminActivity";
    TextView name, no, address, type, items;
    Button loc, assign;
    ProgressBar progressBar;

    String userId, lat, lng;

    String fName, fNo, fAddr, fType, fItems;

    String foodTask;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_admin);

        name = findViewById(R.id.adminFoodName);
        no = findViewById(R.id.adminFoodNo);
        address = findViewById(R.id.adminFoodAddress);
        type = findViewById(R.id.adminFoodType);
        items = findViewById(R.id.adminFoodItem);

        loc = findViewById(R.id.adminFoodLocBtn);
        assign = findViewById(R.id.adminFoodAssign);

        progressBar = findViewById(R.id.adminFoodProgressBar);

        Bundle b = getIntent().getExtras();

        if(b == null)
        {
            Toast.makeText(this, "Unable to get data", Toast.LENGTH_SHORT).show();
        }
        else
        {
            userId = b.getString("Id");
            getData();
        }

        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapLocation();
            }
        });

        assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VolunteerList.class);
                intent.putExtra("Id", userId);
                intent.putExtra("No", fNo);
                intent.putExtra("Task", foodTask);
                intent.putExtra("Address", fAddr);
                intent.putExtra("Lat", lat);
                intent.putExtra("Lng", lng);
                startActivity(intent);
            }
        });
    }

    private void openMapLocation() {

//        https://stackoverflow.com/questions/42677389/android-how-to-pass-lat-long-route-info-to-google-maps-app

//        String url = "google.navigation:q=" + lat + "," + lng;
        String url = "https://www.google.com/maps/dir/?api=1&destination=" + lat + "," + lng + "&travelmode=driving";

        Uri gmmIntentUri = Uri.parse(url);//Format:"google.navigation:q="latitude,longitude"
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

    }

    private void getData() {

        DocumentReference docRef = db.collection("Food").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        if (document.get("Name") != null) {

                            fName = document.get("Name").toString();
                            fNo = document.get("Number").toString();
                            fAddr = document.get("Address").toString();
                            fType = document.get("Type").toString();
                            fItems = document.get("Items").toString();
                            lat = document.get("Latitude").toString();
                            lng = document.get("Longitude").toString();

                            name.setText(fName);
                            no.setText(fNo);
                            address.setText(fAddr);
                            type.setText(fType);
                            items.setText(fItems);

                            foodTask = "\n" + "Name: " + fName + "\n" + "Number: " + fNo + "\n" + "Address: " + fAddr + "\n" + "Type: " + fType + "\n" + "Items: " + fItems;

                            loc.setClickable(true);
                            assign.setClickable(true);

                            progressBar.setVisibility(View.GONE);

                        } else {
                            Log.d(TAG, "No such document");
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        progressBar.setVisibility(View.GONE);
                    }
                }

            }
        });

    }

}
