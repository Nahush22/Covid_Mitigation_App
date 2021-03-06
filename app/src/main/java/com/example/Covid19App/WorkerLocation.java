package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WorkerLocation extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Volunteer Reg:";

    String wName, wNo, wHome, wWork, wOrg, wProf, wId, wType;

    Button reg;

    GoogleMap map;

    LatLng clickLng;

    com.google.android.gms.maps.model.LatLng loc;

    double myLat;
    double myLong;

    LocationManager locationManager;
    LocationListener locationListener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    MarkerOptions markerOptions;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ACTUALUSERID = "actualuserid";

    String userID = "NAN" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_location);

        reg = findViewById(R.id.workerRegBtn);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userID = sharedPreferences.getString(ACTUALUSERID, "NAN");

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Toast.makeText(this, "Unable to fetch user data.Please go back & fill it again...", Toast.LENGTH_SHORT).show();
        } else {
            wName= extras.getString("name");
            wNo= extras.getString("no");
            wHome= extras.getString("home");
            wWork= extras.getString("work");
            wType= extras.getString("type");
            wOrg= extras.getString("org");
            wProf= extras.getString("prof");
            wId= extras.getString("id");
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Location permission not given", Toast.LENGTH_SHORT).show();
        }

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {

        }


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {

                    myLat = location.getLatitude();
                    myLong = location.getLongitude();

                    String myLatitude = String.valueOf(location.getLatitude());
                    String myLongitude = String.valueOf(location.getLongitude());

                    LatLng latLng = new LatLng(myLat, myLong);

                    map.clear();
                    markerOptions.position(latLng);
                    map.addMarker(markerOptions);
                    map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

//                    Toast.makeText(getApplication(), String.valueOf(myLat) + "," + String.valueOf(myLong), Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locationListener);

        } catch (SecurityException e) {
            e.printStackTrace();
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.workerLocMap);
        mapFragment.getMapAsync(this);

//        String volunteerExists = loadStoreDoc();

//        if(volunteerExists.isEmpty())
//        {
////            Toast.makeText(this, "Create new volunteer id", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            finish();
//            startActivity(new Intent(WorkerLocation.this,VolunteerTaskActivity.class));
//        }

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDetails();
            }
        });

    }

    private void verifyDetails() {
        if(wName == null || wNo == null || wHome == null || wWork == null || wType == null || wOrg == null || wProf == null || wId == null)
        {
            Toast.makeText(this, "Please go back & fill the details again...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            updateSellerDb();
        }
    }

    private void updateSellerDb() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(location!=null) {
            if(String.valueOf(location.getLatitude())!=null && String.valueOf(location.getLongitude())!=null)
            {
                myLat = location.getLatitude();
                myLong = location.getLongitude();
            }

        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locationListener);

        if(clickLng != null)
        {
            myLat = clickLng.latitude;
            myLong = clickLng.longitude;
        }

        Map<String, Object> worker = new HashMap<>();
        worker.put("UserID", userID);
        worker.put("Name", wName);
        worker.put("Number", wNo);
        worker.put("Home", wHome);
        worker.put("Work", wWork);
        worker.put("Type", wType);
        worker.put("Latitude", String.valueOf(myLat));
        worker.put("Longitude", String.valueOf(myLong));
        worker.put("Org", wOrg);
        worker.put("Prof", wProf);
        worker.put("Id", wId);
        worker.put("Assigned", 0);
        worker.put("Accepted", 0);
        worker.put("Denied", 0);

        Map<String, Object> sample = new HashMap<>();
        sample.put("NAN", "none");

        db.collection("Worker").document(userID)
                .set(worker)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        db.collection("Worker").document(userID).collection("Tasks").document("NAN")
                .set(sample)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        Toast.makeText(WorkerLocation.this, "Registered as " + wType + " worker", Toast.LENGTH_SHORT).show();

                        finish();
                        startActivity(new Intent(WorkerLocation.this,WorkerTaskActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        loc = new com.google.android.gms.maps.model.LatLng(myLat, myLong);

        Log.d(TAG, String.valueOf(loc));
//        Toast.makeText(getApplicationContext(), loc.toString(), Toast.LENGTH_SHORT).show();

        markerOptions = new MarkerOptions().position(loc)
                .title("User Address")
                .draggable(true)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLng(loc));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 10));

//        Marker markerName = map.addMarker(new MarkerOptions().position(latLng).title("Title"));
//        markerName.remove();

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                clickLng = latLng;
                map.clear();
                markerOptions.position(clickLng);
                map.addMarker(markerOptions);
                map.moveCamera(CameraUpdateFactory.newLatLng(clickLng));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(clickLng, 10));

            }
        });

//        map.setOnInfoWindowClickListener((GoogleMap.OnInfoWindowClickListener) MainActivity.this);

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
                // TODO Auto-generated method stub
                // Here your code
                Toast.makeText(WorkerLocation.this, "Starting to drag marker",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // TODO Auto-generated method stub

                loc = marker.getPosition();
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                // TODO Auto-generated method stub
                // Toast.makeText(MainActivity.this, "Dragging",
                // Toast.LENGTH_SHORT).show();
                System.out.println("Dragging");
            }


        });

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
