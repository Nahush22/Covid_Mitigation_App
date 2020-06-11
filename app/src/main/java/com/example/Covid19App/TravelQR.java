package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class TravelQR extends AppCompatActivity {

    private static final String TAG = "TravelQR";

    TextView newPassBtn, notExistMsg, cancelTxt;
    ImageView qr;
    ScrollView scrollView;

    ProgressBar progressBar;

    String userID;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ACTUALUSERID = "actualuserid";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String name, number, orig, dest, date, id, reason;
    int cancelled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_q_r);

        newPassBtn = findViewById(R.id.newPassBtn);
        notExistMsg = findViewById(R.id.notExistMsg);
        cancelTxt = findViewById(R.id.travelQrCancelTxt);

        qr = findViewById(R.id.travelQrImg);

        scrollView = findViewById(R.id.travelPassScrollView);

        progressBar = findViewById(R.id.travelPassProgressBar);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userID = sharedPreferences.getString(ACTUALUSERID, "NAN");

        getQrData();

        newPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(TravelQR.this);
                builder.setMessage("Are you sure you want to apply for a new pass?")
                        .setTitle("New Pass Confirmation:")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                startActivity(new Intent(getApplicationContext(), TravelPassActivity.class));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Do something when cancelled
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        notExistMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(TravelQR.this, TravelPassActivity.class));
            }
        });
    }

    private void getQrData() {
        DocumentReference docRef = db.collection("TravelPass").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    progressBar.setVisibility(View.GONE);

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        scrollView.setVisibility(View.VISIBLE);

                        name = document.get("Name").toString();
                        number = document.get("Number").toString();
                        orig = document.get("Origin").toString();
                        dest = document.get("Destination").toString();
                        date = document.get("Date").toString();
                        id = document.get("ID").toString();
                        reason = document.get("Reason").toString();

                        cancelled = Integer.parseInt(document.get("Cancelled").toString());

                        createQr();

                    } else {
                        Log.d(TAG, "No such document");

                        scrollView.setVisibility(View.GONE);

                        notExistMsg.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void createQr() {
        String data_in_code = name + "," + userID + "," + number + "," + orig + "," + dest + "," + date + "," + id + "," + reason;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{

            scrollView.setVisibility(View.VISIBLE);

            if(cancelled == 1)
            {
                cancelTxt.setVisibility(View.VISIBLE);
                qr.setVisibility(View.GONE);
            }

            BitMatrix bitMatrix=multiFormatWriter.encode(data_in_code, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
            Bitmap bitmap=barcodeEncoder.createBitmap(bitMatrix);
            qr.setImageBitmap(bitmap);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
