package com.example.Covid19App;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class userProductList extends AppCompatActivity {

    private static final String TAG = "User ProductList:";
    TextView orderBtn, totalPrice;

    ProductClass product;
    ArrayList<ProductClass> products = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView userProductRecyclerList;
    userProductListAdapter adapter;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String KEY = "StoreID";

    String storeProductLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_product_list);

        orderBtn = findViewById(R.id.orderProductBtn);
        totalPrice = findViewById(R.id.userTotal);

        userProductRecyclerList = findViewById(R.id.userProductRecyclerView);
        userProductRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        storeProductLocation = loadStoreID();

        adapter = new userProductListAdapter(this, products);
        userProductRecyclerList.setAdapter(adapter);

        getStoredData();
    }

    private void getStoredData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Store Products");
        progressDialog.show();

        Log.d(TAG, storeProductLocation);

//        ProductClass dummyProduct = new ProductClass("dummyitem", 0, "none", 0);
//        db.collection("Stores").document("Stored_Product_Storage").collection(String.valueOf(docLocation)).add(dummyProduct);

        db.collection("Stores").document("Stored_Product_Storage").collection(storeProductLocation)
//                .whereGreaterThan("price", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            products.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                String name = document.get("name").toString();
                                int price = Integer.parseInt(document.get("price").toString());
                                float units = Float.parseFloat(document.get("units").toString());
                                String unitType = document.get("unitType").toString();

                                ProductClass product = new ProductClass( name, price, unitType, units);

                                products.add(product);

                            }

                            progressDialog.dismiss();
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    public String loadStoreID()
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String text = sharedPreferences.getString(KEY, "RandomStore");
        return text;
    }
}
