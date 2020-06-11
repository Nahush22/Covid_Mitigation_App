package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class StoreScanner extends AppCompatActivity {

    private static final String TAG = "StoreScanner:";
    TextView no, name, date, items, total;
    Button scan, confirm;
    ImageView scanImg;

    private static final int CAMERA_PERMISSION_CODE=101;

    String transacId, userId, storeId;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_scanner);

        no = findViewById(R.id.storeReceiptNoTxt);
        name = findViewById(R.id.storeNameTxt);
        date=  findViewById(R.id.storeDateTxt);
        items = findViewById(R.id.storeScannerItemsTxt);
        total = findViewById(R.id.storeScannerTotalTxt);

        scan = findViewById(R.id.storeScanBtn);
        confirm = findViewById(R.id.storeConfirmBtn);

        scanImg = findViewById(R.id.storeScannerImg);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT>=23){
                    if(checkPermission(Manifest.permission.CAMERA)){
                        openScanner();
                    }
                    else{
                        requestPermission(Manifest.permission.CAMERA,CAMERA_PERMISSION_CODE);
                    }
                }
                else{
                    openScanner();
                }

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StoreScanner.this);
                builder.setMessage("Do you want to complete the order?")
                        .setTitle("Confirm Transaction:")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                confirmTransaction();
                                Toast.makeText(getApplicationContext(), "Transaction Completed", Toast.LENGTH_SHORT).show();

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

    }

    private void confirmTransaction() {

        no.setText("Receipt No:");
        name.setText("Store Name:");
        date.setText("Allotted Date:");
        items.setText("Items:");
        total.setText("Total:");

        DocumentReference userReceiptLocation = db.collection("DataStorage").document("Users")
                .collection(userId).document(transacId);

        DocumentReference sellerReceiptLocation = db.collection("Stores").document("Stored_Product_Storage")
                .collection(storeId).document("ProductRoot").collection("Receipts").document(transacId);

        userReceiptLocation
                .update("Completed", 1)
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

        userReceiptLocation
                .update("Ongoing", 0)
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

        sellerReceiptLocation
                .update("Completed", 1)
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

        sellerReceiptLocation
                .update("Ongoing", 0)
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

    }

    private void openScanner() {
        new IntentIntegrator(StoreScanner.this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null){
            if(result.getContents()==null){
                Toast.makeText(this, "Blank", Toast.LENGTH_SHORT).show();
            }
            else{
                parseResult(result.getContents());
            }
        }
        else{
            Toast.makeText(this, "Blank", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseResult(String contents) {

        final String[] data = contents.split(",");

        transacId = data[0];
        userId = data[1];
        storeId = data[2];

        no.setText(data[0]);

        DocumentReference userReceiptLocation = db.collection("DataStorage").document("Users")
                .collection(userId).document(transacId);

        DocumentReference sellerReceiptLocation = db.collection("Stores").document("Stored_Product_Storage")
                .collection(storeId).document("ProductRoot").collection("Receipts").document(transacId);

        userReceiptLocation.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("AdminScanner", "DocumentSnapshot data: " + document.getData());

                        name.setText(document.get("StoreName").toString());
                        items.setText(document.get("Items").toString());
                        total.setText("Rs." + document.get("Total").toString());

                        String hr = document.get("DeliveryTime").toString();

                        switch (hr)
                        {
                            case "9": hr = "9" + "-" + "10 Am";
                                break;
                            case "10": hr = "10" + "-" + "11 Am";
                                break;
                            case "11": hr = "11" + "-" + "12 Am";
                                break;
                            case "12": hr = "12" + "-" + "1 Pm";
                                break;
                            case "13": hr = "1" + "-" + "2 Pm";
                                break;
                            case "14": hr = "2" + "-" + "3 Pm";
                                break;
                            case "15": hr = "3" + "-" + "4 Pm";
                                break;
                            case "16": hr = "4" + "-" + "5 Pm";
                                break;

                        }

                        String receivedDate = document.get("DeliveryDate").toString() + ", " + hr;

                        if(receivedDate.equals(null))
                        {
                            Toast.makeText(StoreScanner.this, "Unable to get transaction data", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            date.setText(receivedDate);
                        }

                    } else {
                        Log.d("AdminScanner", "No such document");

                        Toast.makeText(StoreScanner.this, "Document doesnt exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("AdminScanner", "get failed with ", task.getException());

                    Toast.makeText(StoreScanner.this, "Unable to access receipt", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private boolean checkPermission(String permission){
        int result = ContextCompat.checkSelfPermission(StoreScanner.this,permission);
        if(result == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else{
            return false;
        }
    }

    private void requestPermission(String permission, int code){
        if(ActivityCompat.shouldShowRequestPermissionRationale(StoreScanner.this,permission)){

        }
        else{
            ActivityCompat.requestPermissions(StoreScanner.this,new String[]{permission},code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_PERMISSION_CODE:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openScanner();
                }
        }
    }

}
