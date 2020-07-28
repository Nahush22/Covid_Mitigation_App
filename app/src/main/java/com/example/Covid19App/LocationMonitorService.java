package com.example.Covid19App;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class LocationMonitorService extends Service {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private double lat;
    private double lng;

//    private final long MIN_TIME = 1000 * 60 * 2;
//    private final long MIN_DIST = 20;

    private final long MIN_TIME = 1;
    private final long MIN_DIST = 1;

    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ACTUALUSERID = "actualuserid";
    private static final String PHONENO = "phoneno";

    private String userID, phoneNumber;

    String storeId, transacId;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LocationMonitorService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        https://stackoverflow.com/questions/5265913/how-to-use-putextra-and-getextra-for-string-data

        getUserID();

        Bundle extras = intent.getExtras();
        if(extras != null)
        {

            if(extras.getString("type").equals("store"))
            {
                storeId = extras.getString("storeId");
                transacId = extras.getString("transacId");

                    initialiseStoreDbListener();

            }

            if(extras.getString("type").equals("vol"))
            {
                initialiseVolunteerDbListener();
            }

        }

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, receiptDisplayActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.service_notif);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Covid19 MitiGATOR")
                .setContentText(getResources().getString(R.string.NotificationString))
                .setSmallIcon(R.mipmap.service_notif)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //do heavy work on a background thread

            locationDetector();

        //stopSelf();

        return START_NOT_STICKY;

    }

    private void deleteLocationDoc() {
        db.collection("Location").document(userID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
//                        Toast.makeText(LocationMonitorService.this, "Location details deleted!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private void initialiseVolunteerDbListener() {

        final DocumentReference docRef = db.collection("VolunteerID").document(userID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, source + " data: " + snapshot.getData());

                    if(snapshot.get("Assigned").toString().equals("0"))
                    {
                        stopSelf(); //https://stackoverflow.com/questions/55930516/how-to-force-stop-android-service-programmatically

                        deleteLocationDoc();
                        Toast.makeText(LocationMonitorService.this, "Admin has accepted task completion request...location service has been closed", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.d(TAG, source + " data: null");
                }
            }
        });

    }

    private void initialiseStoreDbListener() {

        final DocumentReference docRef = db.collection("DataStorage").document("Users")
                .collection(userID).document(transacId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, source + " data: " + snapshot.getData());

                    if(snapshot.get("Cancelled").toString().equals("1"))
                    {
                        stopSelf(); //https://stackoverflow.com/questions/55930516/how-to-force-stop-android-service-programmatically

                        deleteLocationDoc();
                        Toast.makeText(LocationMonitorService.this, "Order has cancelled", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.d(TAG, source + " data: null");
                }
            }
        });

    }

    private void getUserID() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userID = sharedPreferences.getString(ACTUALUSERID, "NAN");
        phoneNumber = sharedPreferences.getString(PHONENO, "NAN");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void locationDetector() {

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {

                    lat = location.getLatitude();
                    lng = location.getLongitude();

                    updateDb();

//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage(phoneNumber,null,message,null,null);



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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    private void updateDb() {

        Map<String, Object> userdata = new HashMap<>();
        userdata.put("UserID", userID);
        userdata.put("UserNo.", phoneNumber);
        userdata.put("Latitude", lat);
        userdata.put("Longitude", lng);
        userdata.put("User", 1);
        userdata.put("Volunteer", 0);
        userdata.put("Cancelled", 0);

        db.collection("Location").document(userID)
                .set(userdata)
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

    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

    }
}
