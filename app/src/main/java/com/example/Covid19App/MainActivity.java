package com.example.Covid19App;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "DB Log:";
    TextView title;
    EditText uName, uNo, uStoreName, uStoreAddress, uEmail;
    Button sellerReg;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String KEY = "val";
    private static final String ACTUALUSERID = "actualuserid";

    String userID;

    String docLocation;

    String storeId;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    GoogleMap map;
    com.google.android.gms.maps.model.LatLng loc;

    LatLng clickLng;

    double myLat;
    double myLong;

    LocationManager locationManager;
    LocationListener locationListener;

    MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.sellerTitle);

        uName = findViewById(R.id.sellerName);
        uNo = findViewById(R.id.sellerNumber);
        uStoreName = findViewById(R.id.storeName);
        uStoreAddress = findViewById(R.id.storeAddress);
        uEmail = findViewById(R.id.sellerMail);

        sellerReg = findViewById(R.id.sellerRegister);


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


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mainStoreMap);
        mapFragment.getMapAsync(this);

        loadStoreDoc();

//        if(docLocation.isEmpty())
//        {
//            Toast.makeText(this, "Create new seller id", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            startActivity(new Intent(this, StoreProductUpdateActivity.class));
//        }

        checkPrevReg();

        if(storeId.equals(userID)) {
            finish();
            startActivity(new Intent(this, StoreProductUpdateActivity.class));
        }

        sellerReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDetails();
            }
        });

    }

    private void checkPrevReg() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        storeId = sharedPreferences.getString(KEY, "India");
    }

    private void verifyDetails() {

        if (uName.getText().toString().isEmpty() || uNo.getText().toString().isEmpty() || uStoreName.getText().toString().isEmpty() || uStoreAddress.getText().toString().isEmpty() ||
                uEmail.getText().toString().isEmpty()) {
            Log.d(TAG, "Name not present");
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Name present");
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

        Map<String, String> seller = new HashMap<>();
        seller.put("Name", uName.getText().toString());
        seller.put("Number", uNo.getText().toString());
        seller.put("StoreName", uStoreName.getText().toString());
        seller.put("Address", uStoreAddress.getText().toString());
        seller.put("Email", uEmail.getText().toString());
        seller.put("Latitude", String.valueOf(myLat));
        seller.put("Longitude", String.valueOf(myLong));
        seller.put("UserID", userID);

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

//        String storeId = uStoreName.getText().toString() + uName.getText().toString();

        String storeId = userID;

        seller.put("StoreId", storeId);

        saveStoreDoc(storeId);

//        db.collection("SellerID")
//                .add(seller)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });

        db.collection("SellerID").document(userID)
                .set(seller)
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

        Toast.makeText(this, "Seller Registered", Toast.LENGTH_SHORT).show();

        finish();
        startActivity(new Intent(this,StoreProductUpdateActivity.class));
    }

    private void saveStoreDoc(String text) {

        Log.d(TAG, text);
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY, text);
        editor.apply();

    }

    private void loadStoreDoc() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        docLocation = sharedPreferences.getString(KEY, "");
        userID = sharedPreferences.getString(ACTUALUSERID, "");

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
                Toast.makeText(MainActivity.this, "Starting to drag marker",
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

    private void locationEnabled () {

//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//        try {
//            gps_enabled = locationManager.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
//        } catch (Exception e) {
//            e.printStackTrace() ;
//        }
//        try {
//            network_enabled = locationManager.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;
//        } catch (Exception e) {
//            e.printStackTrace() ;
//        }
//        if (!gps_enabled && !network_enabled) {

            new AlertDialog.Builder(MainActivity. this )
                    .setMessage( "GPS Enable" )
                    .setPositiveButton( "Settings" , new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick (DialogInterface paramDialogInterface , int paramInt) {
                                    startActivity( new Intent(Settings. ACTION_LOCATION_SOURCE_SETTINGS )) ;
                                }
                            })
                    .setNegativeButton( "Cancel" , null )
                    .show() ;
//        }
    }

}
