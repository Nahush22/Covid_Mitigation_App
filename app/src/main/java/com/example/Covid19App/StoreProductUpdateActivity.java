package com.example.Covid19App;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class StoreProductUpdateActivity extends AppCompatActivity {

    EditText sellerProdName, sellerProdUnits, sellerProdPrice, sellerProdUnitType;
    TextView sellerAddBtn, sellerUpdateBtn;

    RecyclerView sellerProdView;

    StoreProductUpdateActivityAdapter storeProductUpdateActivityAdapter;

    ArrayList<ProductClass> products = new ArrayList<>();

    int position;

    private static final String TAG = "SellerUI Product Db Access:";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String KEY = "val";

    String docLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_product_update);

        sellerProdName = findViewById(R.id.sellerProdName);
        sellerProdUnits = findViewById(R.id.sellerUnitsAvailable);
        sellerProdPrice = findViewById(R.id.sellerUnitPrice);
        sellerProdUnitType = findViewById(R.id.sellerUnitType);

        sellerAddBtn = findViewById(R.id.sellerAddBtn);
        sellerUpdateBtn = findViewById(R.id.sellerUpdateBtn);

        sellerProdView = findViewById(R.id.sellerProductRecyclerView);

        sellerProdView.setLayoutManager(new LinearLayoutManager(this));

        docLocation = loadStoreDoc();
        Log.d(TAG, docLocation);

        storeProductUpdateActivityAdapter = new StoreProductUpdateActivityAdapter(this, products,
                new StoreProductUpdateActivityAdapter.Onclick() {
                    @Override
                    public void onEvent(ProductClass productClass, int pos, String event) {

                        if(event == "delete")
                        {
                            position = pos;
                            deleteDbData();
                        }
                        else if(event == "update")
                        {
                            position = pos;
                            sellerUpdateBtn.setVisibility(View.VISIBLE);
                            sellerAddBtn.setVisibility(View.GONE);

                            sellerProdName.setText(productClass.getProductName());
                            sellerProdPrice.setText(String.valueOf(productClass.getPrice()));
                            sellerProdUnits.setText(String.valueOf(productClass.getUnits()));
                            sellerProdUnitType.setText(productClass.getUnitType());
                        }

                    }
                });

        sellerProdView.setAdapter(storeProductUpdateActivityAdapter);

        getStoredData();

        sellerAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name, price, units, unitType;
                name = sellerProdName.getText().toString();
                price = sellerProdPrice.getText().toString();
                units = sellerProdUnits.getText().toString();
                unitType = sellerProdUnitType.getText().toString();

                sellerProdName.setText("");
                sellerProdPrice.setText("");
                sellerProdUnits.setText("");
                sellerProdUnitType.setText("");

                if(name.isEmpty() || price.isEmpty() || units.isEmpty() || unitType.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Enter all item details to add!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    insertData( name, price, units, unitType);
                }
            }
        });

        sellerUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, price, units, unitType;
                name = sellerProdName.getText().toString();
                price = sellerProdPrice.getText().toString();
                units = sellerProdUnits.getText().toString();
                unitType = sellerProdUnitType.getText().toString();

                sellerProdName.setText("");
                sellerProdPrice.setText("");
                sellerProdUnits.setText("");
                sellerProdUnitType.setText("");

                products.get(position).setProductName(name);
                products.get(position).setPrice(Float.parseFloat(price));
                products.get(position).setUnits(Float.parseFloat(units));
                products.get(position).setUnitType(unitType);

                storeProductUpdateActivityAdapter.notifyDataSetChanged();
                updateDb();

                sellerAddBtn.setVisibility(View.VISIBLE);
                sellerUpdateBtn.setVisibility(View.GONE);
            }
        });

    }

    private String loadStoreDoc() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String text = sharedPreferences.getString(KEY, "RandomStore");
        return text;

    }

    private void insertData(String name, String price, String units, String unitType) {

        ProductClass productClass = new ProductClass(name, Float.parseFloat(price), unitType, Float.parseFloat(units));
        products.add(productClass);
        updateDb();
        storeProductUpdateActivityAdapter.notifyDataSetChanged();
    }

    private void updateDb() {

        for(int i=0; i<products.size(); i++)
        {
            db.collection("Stores").document("Stored_Product_Storage")
                    .collection(docLocation).document("ProductRoot")
                    .collection("Products").document(String.valueOf(i))
                    .set(products.get(i))
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

    }

    private void deleteDbData() {
        db.collection("Stores").document("Stored_Product_Storage")
                .collection(docLocation).document("ProductRoot")
                .collection("Products").document(String.valueOf(position))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private void getStoredData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Store Products");
        progressDialog.show();

        Log.d(TAG, docLocation);

//        ProductClass dummyProduct = new ProductClass("dummyitem", 0, "none", 0);
//        db.collection("Stores").document("Stored_Product_Storage").collection(String.valueOf(docLocation)).add(dummyProduct);

        db.collection("Stores").document("Stored_Product_Storage")
                .collection(docLocation).document("ProductRoot")
                .collection("Products")
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
                                float price = Float.parseFloat(document.get("price").toString());
                                float units = Float.parseFloat(document.get("units").toString());
                                String unitType = document.get("unitType").toString();

                                ProductClass product = new ProductClass( name, price, unitType, units);

                                products.add(product);

                            }

                            progressDialog.dismiss();
                            storeProductUpdateActivityAdapter.notifyDataSetChanged();

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

}
