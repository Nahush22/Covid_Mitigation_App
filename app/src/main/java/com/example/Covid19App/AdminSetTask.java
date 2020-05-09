package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminSetTask extends AppCompatActivity implements OnMapReadyCallback {

    String taskSMS;

    EditText taskBrief, taskDesc, taskAddress;
    Button taskAssignBtn;

    private static final String TAG = "AdminSetTask";
    GoogleMap map;

    LatLng clickLng;

    com.google.android.gms.maps.model.LatLng loc;

    double myLat;
    double myLong;

    LocationManager locationManager;
    LocationListener locationListener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    MarkerOptions markerOptions;

    String userID = "6546354642" ;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TASKVOLUNTEERID = "taskVolID";
    private static final String VOLUNTEERADDRESS = "india";
    private static final String VOLUNTEERNUMBER = "volNo";

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    String volId, volAddress, volNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_set_task);

        taskBrief = findViewById(R.id.adminTaskBrief);
        taskDesc = findViewById(R.id.adminTaskDesc);
        taskAddress = findViewById(R.id.adminTaskAddress);

        taskAssignBtn = findViewById(R.id.adminAssignBtn);

        loadVolData();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Location permission not given", Toast.LENGTH_SHORT).show();
        }

//        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
//        {
//            locationEnabled();
//        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS))
            {

            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
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

                    Toast.makeText(getApplication(), String.valueOf(myLat) + "," + String.valueOf(myLong), Toast.LENGTH_SHORT).show();


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


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.adminTaskMap);
        mapFragment.getMapAsync(this);

        taskAssignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDetails();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Thanks for permitting", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Please permit to send task sms to volunteer", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void verifyDetails() {

        if (taskBrief.getText().toString().isEmpty() || taskDesc.getText().toString().isEmpty() || taskAddress.getText().toString().isEmpty()) {
            Log.d(TAG, "Name not present");
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Name present");
            updateTaskDb();
        }

    }

    private void updateTaskDb() {

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

        Map<String, Object> task = new HashMap<>();
        task.put("AdminID", userID);
        task.put("Brief", taskBrief.getText().toString());
        task.put("Description", taskDesc.getText().toString());
        task.put("Address", taskAddress.getText().toString());
        task.put("VolunteerID", volId.toString());
        task.put("Latitude", String.valueOf(myLat));
        task.put("Longitude", String.valueOf(myLong));
        task.put("Accepted", 0);
        task.put("Rejected", 0);
        task.put("Completed", 0);

        taskSMS = userID + "has assigned a task:" + "\n" + "Brief:" + taskBrief.getText().toString() + "\n" + "Address:" + taskAddress.getText().toString() + "\n" + "Contact the admin through this number for more details!";


        db.collection("VolunteerID").document(volId).collection("Tasks")
                .add(task)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        db.collection("VolunteerID").document(volId)
                .update("Assigned", 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        db.collection("DataStorage").document("Admin").collection("AdminCollection").document(userID).collection("Tasks")
                .add(task)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        volNo = "9444050540";

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
        {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(volNo, null, taskSMS, null, null);
            Toast.makeText(this, "SMS sent!", Toast.LENGTH_SHORT).show();
        }


        Toast.makeText(this, "Task Assigned!Wait for volunteer confirmation!", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this,VolunteerList.class));

    }




    private void loadVolData() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        volId = sharedPreferences.getString(TASKVOLUNTEERID, "");
        volAddress = sharedPreferences.getString(VOLUNTEERADDRESS, "");
        volNo = sharedPreferences.getString(VOLUNTEERNUMBER, "");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        loc = new com.google.android.gms.maps.model.LatLng(myLat, myLong);

        Log.d(TAG, String.valueOf(loc));
//        Toast.makeText(getApplicationContext(), loc.toString(), Toast.LENGTH_SHORT).show();

        markerOptions = new MarkerOptions().position(loc)
                .title("Store Location")
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
                Toast.makeText(AdminSetTask.this, "Starting to drag marker",
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
