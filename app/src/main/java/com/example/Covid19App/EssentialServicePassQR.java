package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class EssentialServicePassQR extends AppCompatActivity {

    private static final String TAG = "TravelQR";

    Button newPassBtn, newPassBtn1;
    TextView notExistMsg, cancelTxt, confirmTxt;
    ImageView qr;
    ScrollView scrollView;

    ProgressBar progressBar;

    String userID;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ACTUALUSERID = "actualuserid";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String name, number, orig, dest, date, company, prof, id;
    int cancelled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_essential_service_pass_q_r);

        newPassBtn = findViewById(R.id.espQrBtn);
        newPassBtn1 = findViewById(R.id.espQrBtn2);

        notExistMsg = findViewById(R.id.espQrNotExistMsg);
        cancelTxt = findViewById(R.id.espQrCancelTxt);
        confirmTxt = findViewById(R.id.espQrConfirmTxt);

        qr = findViewById(R.id.espQrImg);

        scrollView = findViewById(R.id.espQrScrollView);

        progressBar = findViewById(R.id.espQrProgressBar);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userID = sharedPreferences.getString(ACTUALUSERID, "NAN");

        getQrData();

        newPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EssentialServicePassQR.this);
                builder.setMessage("Are you sure you want to apply for a new pass?")
                        .setTitle("New Pass Confirmation:")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                startActivity(new Intent(getApplicationContext(), EssentialServiceActvity.class));
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

        newPassBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EssentialServicePassQR.this);
                builder.setMessage("Are you sure you want to apply for a new pass?")
                        .setTitle("New Pass Confirmation:")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                startActivity(new Intent(getApplicationContext(), EssentialServiceActvity.class));
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
                startActivity(new Intent(EssentialServicePassQR.this, EssentialServiceActvity.class));
            }
        });
    }

    private void getQrData() {
        final DocumentReference docRef = db.collection("ServicePass").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    progressBar.setVisibility(View.GONE);

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        if(document.get("Accepted").toString().equals("1"))
                        {
                            confirmTxt.setVisibility(View.GONE);
                            cancelTxt.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);

                            name = document.get("Name").toString();
                            number = document.get("Number").toString();
                            orig = document.get("Origin").toString();
                            dest = document.get("Destination").toString();
                            date = document.get("Date").toString();
                            company = document.get("Date").toString();
                            prof = document.get("Date").toString();
                            id = document.get("Id").toString();

                            cancelled = Integer.parseInt(document.get("Rejected").toString());

                            Toast.makeText(EssentialServicePassQR.this, "Creating QR code...", Toast.LENGTH_SHORT).show();

                            createQr();
                        }
                        else if (document.get("Rejected").toString().equals("1"))
                        {
                            scrollView.setVisibility(View.GONE);
                            cancelTxt.setVisibility(View.VISIBLE);

                            Toast.makeText(EssentialServicePassQR.this, "The pass request has been rejected!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            confirmTxt.setVisibility(View.VISIBLE);

                            Toast.makeText(EssentialServicePassQR.this, "Wait for pass confirmation...", Toast.LENGTH_SHORT).show();
                        }


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

        String data_in_code = name + "," + userID + "," + number + "," + orig + "," + dest + "," + date + "," + company + "," + prof + "," + id + ",";
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{

            if(cancelled == 1)
            {
                cancelTxt.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);

                newPassBtn1.setVisibility(View.VISIBLE);

                qr.setVisibility(View.GONE);
            }
            else
            {
                scrollView.setVisibility(View.VISIBLE);

                BitMatrix bitMatrix=multiFormatWriter.encode(data_in_code, BarcodeFormat.QR_CODE,200,200);
                BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
                Bitmap bitmap=barcodeEncoder.createBitmap(bitMatrix);
                qr.setImageBitmap(bitmap);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


}
