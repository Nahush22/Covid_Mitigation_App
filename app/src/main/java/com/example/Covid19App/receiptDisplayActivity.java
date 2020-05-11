package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class receiptDisplayActivity extends AppCompatActivity {

    private static final String TAG = "Within receipt display activity";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String STOREID = "storeid";
    private static final String TRANSACID = "TransacID";
    private static final String ACTUALUSERID = "actualuserid";

    String userID = "NAN" ;

    String transacId;
    String storeId;
    String storeName;

    Context context;

    ArrayList<String> itemNames = new ArrayList<>();
    ArrayList<Double> itemUnits = new ArrayList<>();

    TextView transacText, dateText, storeText, addressText, itemText;
    Button cancelBtn, qrBtn;

    float val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_display);

        transacText = findViewById(R.id.transsacText);
        dateText = findViewById(R.id.dateText);
        storeText = findViewById(R.id.storeText);
        addressText = findViewById(R.id.addressText);
        itemText = findViewById(R.id.itemText);

        cancelBtn = findViewById(R.id.adminCancelBtn);
        qrBtn = findViewById(R.id.qrCodeBtn);

        context = this;

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        transacId = sharedPreferences.getString(TRANSACID, "India");
        userID = sharedPreferences.getString(ACTUALUSERID, "NAN");

        retrieveReceipt();


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to cancel order?This will also deactivate the location tracker. ")
                        .setTitle("Cancel Order:")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                removeOrder();
                                Toast.makeText(getApplicationContext(), "Order Cancelled", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), StoreList.class));
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

        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQrActivity();
            }
        });

    }

    private void openQrActivity() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STOREID, storeId);
        editor.apply();

        startActivity(new Intent(this, QRActivity.class));

    }

    private void retrieveReceipt() {

        DocumentReference userReceiptLocation = db.collection("DataStorage").document("Users")
                .collection(userID).document(transacId);

        userReceiptLocation.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        storeId = document.get("StoreID").toString();
                        storeName = document.get("StoreName").toString();

                        itemNames = (ArrayList<String>)document.get("ItemNames");
                        itemUnits = (ArrayList<Double>)document.get("ItemUnits");

                        transacText.setText(document.get("TransacId").toString());
                        String time = document.get("DeliveryTime").toString();

                        switch (time)
                        {
                            case "9": time = "9" + "-" + "10 Am";
                                    break;
                            case "10": time = "10" + "-" + "11 Am";
                                    break;
                            case "11": time = "11" + "-" + "12 Am";
                                    break;
                            case "12": time = "12" + "-" + "1 Pm";
                                    break;
                            case "13": time = "1" + "-" + "2 Pm";
                                    break;
                            case "14": time = "2" + "-" + "3 Pm";
                                    break;
                            case "15": time = "3" + "-" + "4 Pm";
                                    break;
                            case "16": time = "4" + "-" + "5 Pm";
                                    break;

                        }

                        String deliveryDate = document.get("DeliveryDate").toString() + " , " + time;

                        dateText.setText(deliveryDate);
                        storeText.setText(document.get("StoreName").toString());
                        addressText.setText(document.get("StoreAddress").toString());

                        String items = document.get("Items").toString();

                        itemText.setText(items);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void removeOrder() {
        Log.d(TAG, itemUnits.get(0).getClass().toString());

        Intent serviceIntent = new Intent(this, LocationMonitorService.class);
        stopService(serviceIntent);

        db.collection("Location").document(userID)
                .update("Cancelled", 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Cancelled in location doc!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Couldn't cancel in location doc!", e);
                    }
                });

        DocumentReference userReceiptLocation = db.collection("DataStorage").document("Users")
                .collection(userID).document(transacId);

        DocumentReference sellerReceiptLocation = db.collection("Stores").document("Stored_Product_Storage")
                .collection(storeId).document("ProductRoot").collection("Receipts").document(transacId);

        for(int i=0; i<itemNames.size(); i++) {

            final CollectionReference prodRef = db.collection("Stores").document("Stored_Product_Storage")
                    .collection(storeId).document("ProductRoot")
                    .collection("Products");

            final int finalI = i;
            Log.d(TAG, itemUnits.get(i).getClass().toString());

            final double userItems = (Double) (itemUnits.get(finalI));
//            val = itemUnits.get(finalI);

            prodRef.whereEqualTo("name", itemNames.get(i))
                    .get()
//                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());


                                     double totalCount = userItems + Double.parseDouble(document.get("units").toString());

                                    prodRef.document(document.getId())
                                            .update("units", totalCount)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "Price successfully updated!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error updating document", e);
                                                }
                                            });

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

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

    }

}
