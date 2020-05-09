package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
//import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Covid19App.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class FinderActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    protected LocationManager locationManager;
    protected android.location.LocationListener locationListener;

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;


    private double latitude, longitude;
    private int proximityRadius = 10000;

    private Marker marker;

    private static final int Request_Location = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finder);

//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();


                }
                catch (Exception e)
                {
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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, locationListener);

        }
        catch(SecurityException e)
        {
            e.printStackTrace();
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    public void onClick(View v)
    {
        String hospital = "hospital", pharmacy = "drugstore";
//        String pharmacyKeyword = "medical"
        Object transferData[] = new Object[2];
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();

        switch (v.getId())
        {
            case R.id.searchBtn:
                EditText searchReq = findViewById(R.id.locationSearch);
                String address = searchReq.getText().toString();
                MarkerOptions userMarkerOptions = new MarkerOptions();

                List<Address> addressList = null;

                if(!TextUtils.isEmpty(address))
                {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(address, 6);

                        if(addressList != null) {
                            for (int i = 0; i < addressList.size(); i++) {
                                Address userAddress = addressList.get(i);
                                LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());

                                userMarkerOptions.position(latLng);
                                userMarkerOptions.title(address);
                                userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                                mMap.addMarker(userMarkerOptions);

                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                            }
                        }
                        else
                        {
                            Toast.makeText(this, "No such location found!", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else
                {
                    Toast.makeText(this, "Please enter an location to search!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.hospitalBtn:
                mMap.clear();
                String url = getUrl(latitude, longitude, hospital);
                transferData[0] = mMap;
                transferData[1] = url;

                getNearbyPlaces.execute(transferData);
                Toast.makeText(this, "Searching for nearby hospitals...", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing nearby hospitals...", Toast.LENGTH_SHORT).show();
                break;

            case R.id.pharmacyBtn:
                mMap.clear();
                String url1 = getUrl(latitude, longitude, pharmacy);
                transferData[0] = mMap;
                transferData[1] = url1;

                getNearbyPlaces.execute(transferData);
                Toast.makeText(this, "Searching for nearby pharmacies...", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing nearby pharmacies...", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace)
    {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitude + ", " + longitude);
        googleURL.append("&radius=" + proximityRadius);
        googleURL.append("&type=" + nearbyPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyB707GrGuAJqY7AtL3seRljV30yV-vhmS8");

        Log.d("GoogleMapsActivity", "url  = " + googleURL.toString());

        return googleURL.toString();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    public boolean checkUserLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, Request_Location);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, Request_Location);
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case Request_Location:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(googleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this, "Location Permission not granted!", Toast.LENGTH_SHORT).show();
                }
        }

    }

    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Log.d("Location Changed: ", "lat: " + latitude + "lng: " + longitude);

        lastLocation = location;
        if(marker != null)
        {
            marker.remove();
        }

        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        Log.d("Location Changed: ", latLng.toString());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        marker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy( 14));

        if(!(googleApiClient != null))
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
