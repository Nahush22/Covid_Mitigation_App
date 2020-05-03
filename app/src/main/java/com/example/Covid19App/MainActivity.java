package com.example.Covid19App;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DB Log:";
    TextView title;
    EditText uName, uNo, uStoreName, uStoreAddress, uEmail;
    Button sellerReg;

    private static final String SHARED_PREFS = "sharedPrefs";

    private static final String KEY = "val";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.sellerTitle);

        uName = findViewById(R.id.sellerName);
        uNo = findViewById(R.id.sellerNumber);
        uStoreName = findViewById(R.id.storeName);
        uStoreAddress = findViewById(R.id.storeAddress);
        uEmail = findViewById(R.id.sellerMail);

        sellerReg = findViewById(R.id.sellerRegister);

        String docLocation = loadStoreDoc();

        if(docLocation.isEmpty())
        {
            Toast.makeText(this, "Create new seller id", Toast.LENGTH_SHORT).show();
        }
        else
        {
            startActivity(new Intent(this, StoreProductUpdateActivity.class));
        }

        sellerReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDetails();
            }
        });

    }

    private void verifyDetails() {

            if(uName.getText().toString().isEmpty() || uNo.getText().toString().isEmpty() || uStoreName.getText().toString().isEmpty() || uStoreAddress.getText().toString().isEmpty() ||
                    uEmail.getText().toString().isEmpty())
            {
                Log.d(TAG, "Name not present");
                Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Log.d(TAG, "Name present");
                updateSellerDb();
            }

    }

    private void updateSellerDb() {

        Map<String, String> seller = new HashMap<>();
        seller.put("Name", uName.getText().toString());
        seller.put("Number", uNo.getText().toString());
        seller.put("StoreName", uStoreName.getText().toString());
        seller.put("Address", uStoreAddress.getText().toString());
        seller.put("Email", uEmail.getText().toString());

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        String storeId = uStoreName.getText().toString() + uName.getText().toString();

        seller.put("StoreId", storeId);

        saveStoreDoc(storeId);

        db.collection("SellerID")
                .add(seller)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        Toast.makeText(this, "Seller Registered", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this,StoreProductUpdateActivity.class));
    }

    private void saveStoreDoc(String text) {

        Log.d(TAG, text);
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY, text);
        editor.apply();

    }

    private String loadStoreDoc() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String text = sharedPreferences.getString(KEY, "");
        return text;

    }

}
