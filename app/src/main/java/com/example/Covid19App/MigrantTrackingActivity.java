package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MigrantTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MigrantTrackingActivity";

    GoogleMap map;

    LatLng clickLng;

    com.google.android.gms.maps.model.LatLng loc;

    double myLat;
    double myLong;

    LocationManager locationManager;
    LocationListener locationListener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    MarkerOptions markerOptions, migrantMarkerOptions;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ACTUALUSERID = "actualuserid";

    ArrayList<MigrantClass> migrants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_migrant_tracking);

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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.migrantMap);
        mapFragment.getMapAsync(this);

        getData();
    }

    private void getData() {

        db.collection("Migrants")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                migrants.clear();

                                if(document.get("Name").toString() != null )
                                {
                                    String id = document.getId();
                                    String name = document.get("Name").toString();
                                    String no = document.get("Number").toString();
                                    String home = document.get("Home").toString();
                                    String current = document.get("Location").toString();
                                    String gender = document.get("Gender").toString();
                                    String lat = document.get("Latitude").toString();
                                    String lng = document.get("Longitude").toString();

                                    MigrantClass migrantClass = new MigrantClass(id, name, no, home, current, gender, lat, lng);
                                    migrants.add(migrantClass);

                                    Double latitude = Double.valueOf(lat);
                                    Double longitude = Double.valueOf(lng);

                                    LatLng migrantLoc = new LatLng(latitude, longitude);

                                    migrantMarkerOptions = new MarkerOptions().position(migrantLoc)
                                            .title("Migrant")
//                                            .draggable(true)
                                            .icon(BitmapDescriptorFactory
                                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    map.addMarker(migrantMarkerOptions).setTag(migrantClass); //https://developers.google.com/maps/documentation/android-sdk/marker
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
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

//        migrantMarkerOptions = new MarkerOptions().position(loc)
//                .title("Migrant")
//                .draggable(true)
//                .icon(BitmapDescriptorFactory
//                        .defaultMarker(BitmapDescriptorFactory.HUE_RED));

        map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLng(loc));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 10));


        //https://github.com/googlemaps/android-samples/blob/master/ApiDemos/java/app/src/gms/java/com/example/mapdemo/MarkerDemoActivity.java
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
//                Toast.makeText(MigrantTrackingActivity.this, "Click Info Window", Toast.LENGTH_SHORT).show();

                if(marker.getTag() != null)
                {
                    MigrantClass migrantClass = (MigrantClass) marker.getTag();

                    Intent intent = new Intent(getApplicationContext(), MigrantDetails.class);
                    intent.putExtra("Name", migrantClass.getName());
                    intent.putExtra("No",  migrantClass.getNumber());
                    intent.putExtra("Home",  migrantClass.getHome());
                    intent.putExtra("Current",  migrantClass.getCurrent());
                    intent.putExtra("Gender",  migrantClass.getGender());
                    intent.putExtra("Lat",  migrantClass.getLat());
                    intent.putExtra("Lng",  migrantClass.getLng());

                    startActivity(intent);

                }

            }
        });

//        Marker markerName = map.addMarker(new MarkerOptions().position(latLng).title("Title"));
//        markerName.remove();

//        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//
//                clickLng = latLng;
//                map.clear();
//                markerOptions.position(clickLng);
//                map.addMarker(markerOptions);
//                map.moveCamera(CameraUpdateFactory.newLatLng(clickLng));
//                map.animateCamera(CameraUpdateFactory.newLatLngZoom(clickLng, 10));
//
//            }
//        });

//        map.setOnInfoWindowClickListener((GoogleMap.OnInfoWindowClickListener) MigrantTrackingActivity.this);

//        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
//
//            @Override
//            public void onMarkerDragStart(Marker marker) {
//                // TODO Auto-generated method stub
//                // Here your code
//                Toast.makeText(MigrantTrackingActivity.this, "Starting to drag marker",
//                        Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onMarkerDragEnd(Marker marker) {
//                // TODO Auto-generated method stub
//
//                loc = marker.getPosition();
//            }
//
//            @Override
//            public void onMarkerDrag(Marker marker) {
//                // TODO Auto-generated method stub
//                // Toast.makeText(MainActivity.this, "Dragging",
//                // Toast.LENGTH_SHORT).show();
//                System.out.println("Dragging");
//            }
//
//
//        });


    }


}
