package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TravelPassActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "TravelPassActivity";
    EditText travelName, travelNo, travelOrig, travelDest, travelDate, travelId, travelHome, travelReason;
    TextView chooseImg;
    ImageView userImg;
    Button uploadImg, registerBtn;

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
        setContentView(R.layout.activity_travel_pass);

        storageReference = FirebaseStorage.getInstance().getReference("EssentialServicePass");

        travelName = findViewById(R.id.travelName);
        travelNo = findViewById(R.id.travelNo);
        travelOrig = findViewById(R.id.travelOrig);
        travelDest = findViewById(R.id.travelDest);
        travelDate = findViewById(R.id.travelDate);
        travelId = findViewById(R.id.travelId);
        travelHome = findViewById(R.id.travelHomeAddr);
        travelReason = findViewById(R.id.travelReason);

        chooseImg = findViewById(R.id.travelChooseImg);
        userImg = findViewById(R.id.travelImg);

        uploadImg = findViewById(R.id.travelImgUpload);
        registerBtn = findViewById(R.id.travelPassRegister);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userID = sharedPreferences.getString(ACTUALUSERID, "NAN");

//        checkPrevTravelPassReg();

        travelDate.setFocusable(true);
        travelDate.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(TravelPassActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    FileUploader();
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTravelPass();
            }
        });

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
        String date = dayOfMonth + "/" + month + "/" + year;
        travelDate.setText(date);
    }

    private void uploadTravelPass() {

        if(travelName.equals(null) || travelNo.equals(null) || travelOrig.equals(null) || travelDest.equals(null) || travelDate.equals(null) || travelId.equals(null) ||
                travelHome.equals(null) || travelReason.equals(null) || userImg.equals(null))
        {
            Toast.makeText(TravelPassActivity.this, "Please enter all details!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Map<String, Object> travelPass = new HashMap<>();
            travelPass.put("UserId", userID);
            travelPass.put("Name", travelName.getText().toString());
            travelPass.put("Number", travelNo.getText().toString());
            travelPass.put("Origin", travelOrig.getText().toString());
            travelPass.put("Destination", travelDest.getText().toString());
            travelPass.put("Date", travelDate.getText().toString());
            travelPass.put("ID", travelId.getText().toString());
            travelPass.put("Home", travelHome.getText().toString());
            travelPass.put("Reason", travelReason.getText().toString());
            travelPass.put("Cancelled", 0);

            DocumentReference travelPassRef = db.collection("TravelPass").document(userID);

            travelPassRef.set(travelPass)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("AdminReg", "DocumentSnapshot successfully written!");

                            Toast.makeText(TravelPassActivity.this, "Admin Details upload to database!", Toast.LENGTH_SHORT).show();

                            finish();
                            startActivity(new Intent(TravelPassActivity.this, TravelQR.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("AdminReg", "Error writing document", e);
                            Log.d("AdminReg", "Unable to upload receipt to user location");

                            Toast.makeText(TravelPassActivity.this, "Error while uploading to to database!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private String getExtension(Uri uri)
    {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));

    }

    private void FileUploader() {

        StorageReference ref = storageReference.child(userID + "." + getExtension(imguri));

        uploadTask = ref.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
//                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        Toast.makeText(TravelPassActivity.this, "Photo uploaded to database!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...

                        Toast.makeText(TravelPassActivity.this, "Unable to upload photo to database!", Toast.LENGTH_SHORT).show();
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
            userImg.setImageURI(imguri);
        }
    }

    private void checkPrevTravelPassReg() {

        final ProgressDialog progressDialogdb = new ProgressDialog(this);
        progressDialogdb.setMessage("Checking if already applied...");
        progressDialogdb.show();

        db.collection("TravelPass").document(userID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        if(document.getId().toString().equals(userID))
                        {
                            progressDialogdb.dismiss();
                            finish();
                            startActivity(new Intent(TravelPassActivity.this, TravelQR.class));
                        }

                    } else {
                        Log.d(TAG, "No such document");

                        progressDialogdb.dismiss();
                        Toast.makeText(TravelPassActivity.this, "Fill in your details to register for a travel pass", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    progressDialogdb.dismiss();

                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }
}
