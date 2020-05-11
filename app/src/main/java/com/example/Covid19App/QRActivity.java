package com.example.Covid19App;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRActivity extends AppCompatActivity {



    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String STOREID = "storeid";
    private static final String TRANSACID = "TransacID";
    private static final String ACTUALUSERID = "actualuserid";
    String userID = "NAN" ;

    ImageView img;

    public String transacId;
    public String storeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r);

        img = findViewById(R.id.qrImg);

        retrieveReceiptData();

        String data_in_code = transacId + "," + userID + "," + storeId;
        MultiFormatWriter multiFormatWriter=new MultiFormatWriter();
        try{
            BitMatrix bitMatrix=multiFormatWriter.encode(data_in_code, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
            Bitmap bitmap=barcodeEncoder.createBitmap(bitMatrix);
            img.setImageBitmap(bitmap);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void retrieveReceiptData() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        transacId = sharedPreferences.getString(TRANSACID, "India");
        storeId = sharedPreferences.getString(STOREID, "India");
        userID = sharedPreferences.getString(ACTUALUSERID, "NAN");

    }
}
