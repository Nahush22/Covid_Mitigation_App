package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EssentialServiceActvity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    TextView name, number, home, dest, date, company, prof, id;
    TextView chooseImg;
    ImageView img;

    Button uploadImg, registerPass;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ACTUALUSERID = "actualuserid";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    StorageReference storageReference;
    private StorageTask uploadTask;

    public Uri imguri;

    String userID = "NAN" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_essential_service_actvity);

        storageReference = FirebaseStorage.getInstance().getReference("EssentialServicePass");

        name = findViewById(R.id.espName);
        number = findViewById(R.id.espNumber);
        home = findViewById(R.id.espHome);
        dest = findViewById(R.id.espDest);
        date = findViewById(R.id.espDate);
        company = findViewById(R.id.espCompName);
        prof = findViewById(R.id.espDesig);
        id = findViewById(R.id.espId);

        chooseImg = findViewById(R.id.espChooseImg);

        uploadImg = findViewById(R.id.espUploadImg);
        registerPass = findViewById(R.id.espPassRegister);

        img = findViewById(R.id.espImg);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userID = sharedPreferences.getString(ACTUALUSERID, "NAN");

        date.setFocusable(true);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Filechooser();
            }
        });

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadTask != null && uploadTask.isInProgress())
                {
                    Toast.makeText(EssentialServiceActvity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    FileUploader();
                }
            }
        });

        registerPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadServicePass();
            }
        });

    }

    private void uploadServicePass() {


        if(name.equals(null) || number.equals(null) || home.equals(null) || dest.equals(null) || date.equals(null) || company.equals(null) ||
                prof.equals(null) || id.equals(null) || img.equals(null))
        {
            Toast.makeText(EssentialServiceActvity.this, "Please enter all details!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Map<String, Object> servicePass = new HashMap<>();
            servicePass.put("UserId", userID);
            servicePass.put("Name", name.getText().toString());
            servicePass.put("Number", number.getText().toString());
            servicePass.put("Origin", home.getText().toString());
            servicePass.put("Destination", dest.getText().toString());
            servicePass.put("Date", date.getText().toString());
            servicePass.put("Company", company.getText().toString());
            servicePass.put("Prof", prof.getText().toString());
            servicePass.put("Id", id.getText().toString());
            servicePass.put("Accepted", 0);
            servicePass.put("Rejected", 0);
            servicePass.put("Cancelled", 0);

            DocumentReference travelPassRef = db.collection("ServicePass").document(userID);

            travelPassRef.set(servicePass)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("AdminReg", "DocumentSnapshot successfully written!");

                            Toast.makeText(EssentialServiceActvity.this, "Pass request sent!Wait for confirmation.", Toast.LENGTH_SHORT).show();

                            finish();
                            startActivity(new Intent(EssentialServiceActvity.this, TravelQR.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("AdminReg", "Error writing document", e);
                            Log.d("AdminReg", "Unable to upload receipt to user location");

                            Toast.makeText(EssentialServiceActvity.this, "Error while uploading to to database!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void showDatePickerDialog() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        int month = monthOfYear + 1;
        String tilldate = dayOfMonth + "/" + month + "/" + year;
        date.setText(tilldate);
    }

    private String getExtension(Uri uri)
    {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));

    }

    private void FileUploader() {

//        StorageReference ref = storageReference.child(userID + "." + getExtension(imguri));

        StorageReference ref = storageReference.child(userID);

        uploadTask = ref.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
//                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        Toast.makeText(EssentialServiceActvity.this, "Photo uploaded to database!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...

                        Toast.makeText(EssentialServiceActvity.this, "Unable to upload photo to database!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void Filechooser() {

        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            imguri = data.getData();
            img.setImageURI(imguri);
        }
    }
}
