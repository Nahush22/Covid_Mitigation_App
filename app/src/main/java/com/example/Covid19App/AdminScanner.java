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

public class AdminScanner extends AppCompatActivity {

    private static final String TAG = "AdminScanner";
    TextView transacTxt, dateTxt, addressTxt;
    Button scanBtn, cancelBtn;
    ImageView scanImg;

    String transacId, userId, storeId, date, address;
    String travelName, travelPassUserId, travelNo, travelOrig, travelDest, travelDate, travelId, travelReason;
    String esrName, esrId, esrNo, esrOrig, esrDest, esrDate, esrCompany, esrProf, esrEmployeeId;

    String type;

    private static final int CAMERA_PERMISSION_CODE=101;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_scanner);

        transacTxt = findViewById(R.id.adminTransacTxt);
        dateTxt = findViewById(R.id.adminDateTxt);
        addressTxt = findViewById(R.id.adminStoreAddressTxt);

        scanBtn = findViewById(R.id.adminScanBtn);
        cancelBtn = findViewById(R.id.adminCancelBtn);

        scanImg = findViewById(R.id.adminScannerImg);

        scanBtn.setOnClickListener(new View.OnClickListener() {
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

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(type.equals("travel"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminScanner.this);
                    builder.setMessage("Do you want to cancel the pass?")
                            .setTitle("Cancel Travel Pass:")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    cancelTravelPass();
                                    Toast.makeText(getApplicationContext(), "Travel Pass Cancelled", Toast.LENGTH_SHORT).show();

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
                else if(type.equals("service"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminScanner.this);
                    builder.setMessage("Do you want to cancel the pass?")
                            .setTitle("Cancel Essential Service Pass:")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    cancelServicePass();
                                    Toast.makeText(getApplicationContext(), "Essential Service Pass Cancelled", Toast.LENGTH_SHORT).show();

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
                else if(type.equals("store"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminScanner.this);
                    builder.setMessage("Do you want to cancel order?")
                            .setTitle("Cancel Transaction:")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    cancelReceipt();
                                    Toast.makeText(getApplicationContext(), "Transaction Cancelled", Toast.LENGTH_SHORT).show();

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

            }
        });
    }

    private void cancelServicePass() {


        DocumentReference userReceiptLocation = db.collection("ServicePass").document(esrId);

        userReceiptLocation
                .update("Rejected", 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                        transacTxt.setText("");

                        cancelBtn.setEnabled(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }

    private void cancelTravelPass() {

        DocumentReference userReceiptLocation = db.collection("TravelPass").document(travelPassUserId);

        userReceiptLocation
                .update("Cancelled", 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                        transacTxt.setText("");

                        cancelBtn.setEnabled(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }

    private void cancelReceipt() {

        DocumentReference userReceiptLocation = db.collection("DataStorage").document("Users")
                .collection(userId).document(transacId);

        DocumentReference sellerReceiptLocation = db.collection("Stores").document("Stored_Product_Storage")
                .collection(storeId).document("ProductRoot").collection("Receipts").document(transacId);

        userReceiptLocation
                .update("Cancelled", 1)
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
                .update("CancelAck", 1)
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
                .update("Cancelled", 1)
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
                .update("CancelAck", 1)
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

        transacTxt.setText("");
        dateTxt.setText("");
        addressTxt.setText("");

        cancelBtn.setEnabled(false);

    }

    private void openScanner() {
        new IntentIntegrator(AdminScanner.this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(resultCode != RESULT_CANCELED)
        {
            if(result != null){
                if(result.getContents() == null){
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


    }

    private void parseResult(String contents) {

        final String[] data = contents.split(",");

//        if(data != null) {
//            Toast.makeText(this, String.valueOf(data.length), Toast.LENGTH_SHORT).show();
//        }

        if(data.length == 8)
        {
            type = "travel";

            travelName = data[0];
            travelPassUserId = data[1];
            travelNo = data[2];
            travelOrig = data[3];
            travelDest = data[4];
            travelDate = data[5];
            travelId = data[6];
            travelReason = data[7];

            String txt = "Name: " + travelName + "\n" + "Number: " + travelNo + "\n" + "Origin: " + travelOrig + "\n" + "Destination: " + travelDest + "\n" +
                    "Date: " + travelDate + "\n" + "Id: " + travelId + "\n" + "Reason: " + travelReason;

            transacTxt.setText(txt);

            cancelBtn.setEnabled(true);
        }
        else if(data.length == 9)
        {
            type = "service";

            esrName = data[0];
            esrId = data[1];
            esrNo = data[2];
            esrOrig = data[3];
            esrDest = data[4];
            esrDate = data[5];
            esrCompany = data[6];
            esrProf = data[7];
            esrEmployeeId = data[8];

            String txt = "Name: " + esrName + "\n" + "Number: " + esrNo + "\n" + "Origin: " + esrOrig + "\n" + "Destination: " + esrDest + "\n" +
                    "Date: " + esrDate + "\n" + "Organisation: " + esrCompany + "\n" + "Profession: " + esrProf + "\n" + "Employee Id: " + esrEmployeeId;

            transacTxt.setText(txt);

            cancelBtn.setEnabled(true);
        }
        else
        {
            type = "store";

            transacId = data[0];
            userId = data[1];
            storeId = data[2];

            transacTxt.setText("Transaction Id: " + data[0]);

            DocumentReference userReceiptLocation = db.collection("DataStorage").document("Users")
                    .collection(userId).document(transacId);


            userReceiptLocation.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("AdminScanner", "DocumentSnapshot data: " + document.getData());

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

                            date = document.get("DeliveryDate").toString() + ", " + hr;
                            address = document.get("StoreAddress").toString();

                            Log.d(TAG, date);
                            Log.d(TAG, address);

                            if(data.equals(null) || address.equals(null))
                            {
                                Toast.makeText(AdminScanner.this, "Unable to get transaction data", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                dateTxt.setText("Date: " + date);
                                addressTxt.setText("Address: " + address);

                                cancelBtn.setEnabled(true);
                            }



                        } else {
                            Log.d("AdminScanner", "No such document");

                            Toast.makeText(AdminScanner.this, "Document doesnt exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("AdminScanner", "get failed with ", task.getException());

                        Toast.makeText(AdminScanner.this, "Unable to access receipt", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }



    }


    private boolean checkPermission(String permission){
        int result = ContextCompat.checkSelfPermission(AdminScanner.this,permission);
        if(result == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else{
            return false;
        }
    }

    private void requestPermission(String permission, int code){
        if(ActivityCompat.shouldShowRequestPermissionRationale(AdminScanner.this,permission)){

        }
        else{
            ActivityCompat.requestPermissions(AdminScanner.this,new String[]{permission},code);
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
