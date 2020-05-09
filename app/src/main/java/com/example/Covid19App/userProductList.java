package com.example.Covid19App;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class userProductList extends AppCompatActivity {

    private static final String TAG = "User ProductList:";
    TextView orderBtn, totalPrice;

    ArrayList<userSelectedProducts> selectedProducts = new ArrayList<>();
    ArrayList<ProductClass> products = new ArrayList<>();
    ArrayList<ProductClass> successTransactions = new ArrayList<>();
    ArrayList<ProductClass> failedTransactions = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView userProductRecyclerList;
    userProductListAdapter adapter;

    ProgressDialog progressDialogdb;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String KEY = "StoreID";
    private static final String STORENAME = "StoreName";
    private static final String STOREADDRESS = "StoreAddress";
    private static final String TRANSACID = "TransacID";
    private static final String USERID = "UserID";

    String storeProductLocation, storeName, storeAddress;

    float total = 0;

    String itemName;
    float itemUnit;
    float itemPrice;
    String itemType;

    String userID = "6546354642" ;

    String currentDate;
    String currentTime;

    String date;
    int currentHour;
    int dateFound = 0;

    String totalItems = "";

    int count = 1;

    int exists = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_product_list);

        orderBtn = findViewById(R.id.orderProductBtn);
        totalPrice = findViewById(R.id.userTotal);

        userProductRecyclerList = findViewById(R.id.userProductRecyclerView);
        userProductRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        loadStoreID();

        adapter = new userProductListAdapter(this, products, new userProductListAdapter.UserProductOnclick(){
            @Override
            public void onEvent(ProductClass productClass, int pos, String productName, float units, float userPrice, String userType, String event) {

                if (event == "Add")
                {
                    float priceDiff = userPrice -  selectedProducts.get(pos).getUserPrice();

                    selectedProducts.get(pos).setUserUnits(units);
                    selectedProducts.get(pos).setUserPrice(userPrice);
                    selectedProducts.get(pos).setPosition(pos);

                    total += priceDiff;
                }
                else if(event == "Sub")
                {
                    float priceDiff = selectedProducts.get(pos).getUserPrice() - userPrice;

                    selectedProducts.get(pos).setUserUnits(units);
                    selectedProducts.get(pos).setUserPrice(userPrice);
                    selectedProducts.get(pos).setPosition(pos);

                    total -= priceDiff;
                }

                totalPrice.setText(String.valueOf(total));

            }
        });
        userProductRecyclerList.setAdapter(adapter);

        getStoredData();

        initialiseDbListener();

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performTransaction();
            }
        });

    }

    private void performTransaction() {

        progressDialogdb = new ProgressDialog(this);
        progressDialogdb.setMessage("Processing request!");
        progressDialogdb.show();
//        int count = 0;

        for ( int i = 0; i < selectedProducts.size(); i++)
        {

            Log.d(TAG, "Entering performTransaction");

            String itemDoc = String.valueOf(selectedProducts.get(i).getPosition());
            itemName = String.valueOf(selectedProducts.get(i).getProdName());
            itemUnit = selectedProducts.get(i).getUserUnits();
            itemPrice = selectedProducts.get(i).getUserPrice();
            itemType = selectedProducts.get(i).getUnitType();

            Log.d(TAG, "Name:"+itemName);
            Log.d(TAG, "Unit:"+itemUnit);
            Log.d(TAG, "Price:"+itemPrice);

//            final DocumentReference prodRef = db.collection("Stores").document("Stored_Product_Storage")
//                    .collection(storeProductLocation).document("ProductRoot")
//                    .collection("Products").document(String.valueOf(count));

//            count ++;

//            Log.d(TAG, String.valueOf(prodRef));
//            Log.d(TAG, String.valueOf(count));


            db.collection("Stores").document("Stored_Product_Storage")
                    .collection(storeProductLocation).document("ProductRoot")
                    .collection("Products").document(String.valueOf(i))
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

//                            Toast.makeText(getApplicationContext(), "Item: " +itemName, Toast.LENGTH_SHORT).show();

                            int docId = Integer.parseInt(document.getId());

                            if((document.get("productName").toString()).equals(selectedProducts.get(docId).getProdName()))
                            {
                                Log.d(TAG, document.get("name").toString());

                                float currentUnit = Float.parseFloat(document.get("units").toString());
                                String itemName = selectedProducts.get(docId).getProdName();
                                float itemUnit = selectedProducts.get(docId).getUserUnits();
                                float itemPrice = selectedProducts.get(docId).getUserPrice();
                                String itemType = selectedProducts.get(docId).getUnitType();

                                if(currentUnit - itemUnit >= 0)
                                {
                                    float finalUnits = currentUnit - itemUnit;
                                    db.collection("Stores").document("Stored_Product_Storage")
                                            .collection(storeProductLocation).document("ProductRoot")
                                            .collection("Products").document(String.valueOf(docId))
                                            .set(new ProductClass(itemName,products.get(docId).getPrice(), itemType, finalUnits));
                                    Log.d(TAG, "Item unit changed");

                                    successTransactions.add(new ProductClass(itemName, itemPrice, itemType, itemUnit));

                                    if(docId == selectedProducts.size()-1)
                                        updateReceipt();
                                }
                                else
                                {
                                    //Do something if there are concurrency issues
                                    Log.d(TAG, "item not present");
                                    Toast.makeText(getApplicationContext(), "Item  " +itemName+ "  unavailable for the specified amount!Other item purchases will proceed!", Toast.LENGTH_LONG).show();
                                    failedTransactions.add(new ProductClass( itemName, itemPrice, itemType, itemUnit));

                                    if(docId == selectedProducts.size()-1)
                                        updateReceipt();
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Item" +itemName+ "appears to be removed by the seller", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Seller removed item");
                            }

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        Toast.makeText(getApplicationContext(), "Transaction failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }




    }

    private void updateReceipt() {


        for( int i = 0; i < selectedProducts.size(); i++)
        {

            if(selectedProducts.get(i).getUserPrice() > 0)
                totalItems = totalItems + selectedProducts.get(i).prodName + " - " + selectedProducts.get(i).userUnits + " " + selectedProducts.get(i).unitType + " for Rs." + selectedProducts.get(i).userPrice + " " + "\n";
            Log.d(TAG, "TotalItems set");
        }

        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        Calendar c = Calendar.getInstance();

//        Date now = c.getTime();
//        c.add(Calendar.DATE, 1);

        date = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        currentHour +=1;

        if(currentHour < 9 )
        {
            currentHour = 9;
        }
        else if( currentHour >= 16)
        {
            c.add(Calendar.DATE, 1);
            date = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
            currentHour = 9;
        }

        while( currentHour <= 16)
        {
            if(exists == 1)
            {
                break;
            }
            db.collection("Stores").document("Stored_Product_Storage")
                    .collection(storeProductLocation).document("Allotted Dates")
                    .collection(date).document(String.valueOf(currentHour))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if(exists == 0) {

                                if (task.isSuccessful()) {
                                    final DocumentSnapshot document = task.getResult();
                                    final int newDocId = Integer.parseInt(document.getId());


                                    Map<String, Object> newCountVar = new HashMap<>();
                                    newCountVar.put("count", 1);



                                    if (document.exists()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                        int oldCountIncr = Integer.parseInt(document.get("count").toString()) + 1;
                                        Map<String, Object> oldcountVar = new HashMap<>();
                                        oldcountVar.put("count", oldCountIncr);

                                        if (Integer.parseInt(document.get("count").toString()) < 10) {

                                            int count = Integer.parseInt(document.get("count").toString());

                                            Log.d(TAG, "Getting count in document");

                                            db.collection("Stores").document("Stored_Product_Storage")
                                                    .collection(storeProductLocation).document("Allotted Dates")
                                                    .collection(date).document(String.valueOf(currentHour))
                                                    .set(oldcountVar)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully written!");

                                                            Log.d(TAG, "Setting count + 1 in timeslot document");

                                                            exists = 1;

                                                            int finalHour = Integer.parseInt(document.getId().toString());

                                                            setUserReceipt(currentDate, currentTime, date, finalHour);

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error writing document", e);
                                                            Log.d(TAG, "Can't set count");
                                                        }
                                                    });


                                            exists = 1;
                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                        Toast.makeText(getApplicationContext(), "Doesnt exist", Toast.LENGTH_SHORT).show();

                                        if(exists == 0) {

                                            db.collection("Stores").document("Stored_Product_Storage")
                                                    .collection(storeProductLocation).document("Allotted Dates")
                                                    .collection(date).document(String.valueOf(newDocId))
                                                    .set(newCountVar)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully written!");

                                                            Log.d(TAG, "Creating new timeslot doc & setting count to 1");

                                                            setUserReceipt(currentDate, currentTime, date, newDocId);

                                                            exists = 1;
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error writing document", e);
                                                            Log.d(TAG, "Can't add new document");
                                                        }
                                                    });

                                        }
                                    }
                                }
                            }
                        }
                    });
            if(exists == 0)
            {
                break;
            }
            currentHour ++ ;

            if(currentHour == 16)
            {
                c.add(Calendar.DATE, 1);
                date = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
                currentHour = 9;
            }
        }


    }

    private void setUserReceipt(String transactionDate, String transactionTime, String date, int currentHour) {

        String transactionId = transactionDate + transactionTime + userID + storeName;

        ArrayList<String> itemNames = new ArrayList<>();
        ArrayList<Float> itemUnits = new ArrayList<>();
        for (int i=0; i < successTransactions.size(); i++)
        {
            itemNames.add(successTransactions.get(i).getProductName());
            itemUnits.add(successTransactions.get(i).getUnits());
        }

        Map<String, Object> receiptDetails = new HashMap<>();
        receiptDetails.put("TransacId", transactionId);
        receiptDetails.put("StoreID", storeProductLocation);
        receiptDetails.put("StoreName", storeName);
        receiptDetails.put("StoreAddress", storeAddress);
        receiptDetails.put("TransacDate", transactionDate);
        receiptDetails.put("TransacTime", transactionTime);
        receiptDetails.put("DeliveryDate", date);
        receiptDetails.put("DeliveryTime", currentHour);
        receiptDetails.put("Items", totalItems);
        receiptDetails.put("ItemNames", itemNames);
        receiptDetails.put("ItemUnits", itemUnits);
        receiptDetails.put("Ongoing", 1);
        receiptDetails.put("Completed", 0);
        receiptDetails.put("Cancelled", 0);
        receiptDetails.put("CancelAck", 0);

        count = 0;

        Log.d(TAG, "Setting transactionid & receipt map");

        DocumentReference userReceiptLocation = db.collection("DataStorage").document("Users")
                .collection(userID).document(transactionId);

        userReceiptLocation.set(receiptDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        count++;

                        if(count == 2)
                        {
                            storeUserID();

                            progressDialogdb.dismiss();

                            showServiceAlert();

                        }

                        Log.d(TAG, "Receipt uploaded to user location");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Log.d(TAG, "Unable to upload receipt to user location");
                    }
                });

        DocumentReference sellerReceiptLocation = db.collection("Stores").document("Stored_Product_Storage")
                .collection(storeProductLocation).document("ProductRoot").collection("Receipts").document(transactionId);

        sellerReceiptLocation.set(receiptDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Log.d(TAG, "Receipt uploaded to seller location");

                        count++;

                        if(count == 2)
                        {
                            storeUserID();

                            progressDialogdb.dismiss();

                            showServiceAlert();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Log.d(TAG, "Unable to upload receipt to seller location");
                    }
                });

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TRANSACID, transactionId);
        editor.apply();

        Log.d(TAG, "Opening user receipt page");



    }

    private synchronized void showServiceAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Items ordered!User location will now be tracked to ensure social distancing.This can be disable by cancelling the order.This is to prevent people from visiting shops outside of the allotted time & to prevent misuse of the permit! ")
                .setTitle("Alert:")
                .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent serviceIntent = new Intent(userProductList.this, LocationMonitorService.class);
                        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");

                        ContextCompat.startForegroundService(userProductList.this, serviceIntent);

                        Toast.makeText(getApplicationContext(), "Items Ordered successfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), receiptDisplayActivity.class));

                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void storeUserID() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERID, userID);
        editor.apply();

    }

    private void getStoredData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Store Products");
        progressDialog.show();

        Log.d(TAG, storeProductLocation);

//        ProductClass dummyProduct = new ProductClass("dummyitem", 0, "none", 0);
//        db.collection("Stores").document("Stored_Product_Storage").collection(String.valueOf(docLocation)).add(dummyProduct);

        db.collection("Stores").document("Stored_Product_Storage")
                .collection(storeProductLocation).document("ProductRoot")
                .collection("Products")
//                .whereGreaterThan("price", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int i = 0;

                            selectedProducts.clear();
                            products.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                String name = document.get("name").toString();
                                float price = Float.parseFloat(document.get("price").toString());
                                float units = Float.parseFloat(document.get("units").toString());
                                String unitType = document.get("unitType").toString();

                                ProductClass product = new ProductClass( name, price, unitType, units);

                                products.add(product);

                                selectedProducts.add(new userSelectedProducts(i, name, 0, 0, unitType));

                                i++;

                            }

                            progressDialog.dismiss();
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    public void loadStoreID()
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        storeProductLocation = sharedPreferences.getString(KEY, "India");
        storeName = sharedPreferences.getString(STORENAME, "RandomStore");
        storeAddress = sharedPreferences.getString(STOREADDRESS, "India");
    }

    private void initialiseDbListener() {

        db.collection("Stores").document("Stored_Product_Storage")
                .collection(storeProductLocation).document("ProductRoot")
                .collection("Products")
//                .whereEqualTo("state", "CA")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                                ? "Local" : "Server";

                        if(source == "Server")
                        {
                            products.clear();
                            selectedProducts.clear();
                            int i = 0;

                            for (QueryDocumentSnapshot document : snapshot) {
                                if (document.get("Name") != null && document.get("price") != null && document.get("units") != null || document.get("unitType") != null) {
                                    String name = document.get("name").toString();
                                    float price = Float.parseFloat(document.get("price").toString());
                                    float units = Float.parseFloat(document.get("units").toString());
                                    String unitType = document.get("unitType").toString();

                                    ProductClass product = new ProductClass( name, price, unitType, units);

                                    products.add(product);

                                    selectedProducts.add(new userSelectedProducts(i, name, 0, 0, unitType));

                                    i++;
                                }
                            }

                            adapter.notifyDataSetChanged();

                        }

                    }
                });

    }

}
