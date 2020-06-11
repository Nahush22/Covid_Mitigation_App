package com.example.Covid19App;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class AdminRegister extends AppCompatActivity {

    private static final String TAG = "AdminRegister:";
    EditText name, no, address, mail, prof, id, place;
    Button uploadImgBtn, regBtn;
    TextView chooseImg;
    ImageView img;

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
        setContentView(R.layout.activity_admin_register);

        storageReference = FirebaseStorage.getInstance().getReference("Images");

        name = findViewById(R.id.adminNameTxt);
        no = findViewById(R.id.adminNumberTxt);
        address = findViewById(R.id.adminAddressTxt);
        mail = findViewById(R.id.adminMailTxt);
        prof = findViewById(R.id.adminProfTxt);
        id = findViewById(R.id.adminIdTxt);
        place = findViewById(R.id.adminCityTxt);

        uploadImgBtn = findViewById(R.id.adminImgBtn);
        regBtn = findViewById(R.id.adminRegBtn);
        chooseImg = findViewById(R.id.adminImgChooseBtn);
        img = findViewById(R.id.adminImgView);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userID = sharedPreferences.getString(ACTUALUSERID, "NAN");

        checkAdminPrevReg();

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Filechooser();
            }
        });

        uploadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadTask != null && uploadTask.isInProgress())
                {
                    Toast.makeText(AdminRegister.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    FileUploader();
                }
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAdminDb();
            }
        });
    }

    private void checkAdminPrevReg() {

        final ProgressDialog progressDialogdb = new ProgressDialog(this);
        progressDialogdb.setMessage("Checking registration details...");
        progressDialogdb.show();

        db.collection("DataStorage").document("Admin").collection("AdminCollection").document(userID)
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
                            startActivity(new Intent(AdminRegister.this, AdminFunctionsActivity.class));
                        }

                    } else {
                        Log.d(TAG, "No such document");

                        progressDialogdb.dismiss();
                        Toast.makeText(AdminRegister.this, "Please register to access admin module", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    progressDialogdb.dismiss();

                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void uploadAdminDb() {

        if(name.equals(null) || no.equals(null) || address.equals(null) || mail.equals(null) || prof.equals(null) || id.equals(null) || place.equals(null) || img.equals(null))
        {
            Toast.makeText(AdminRegister.this, "Please enter all details!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Map<String, Object> adminData = new HashMap<>();
            adminData.put("AdminID", userID);
            adminData.put("Name", name.getText().toString());
            adminData.put("Number", no.getText().toString());
            adminData.put("Address", address.getText().toString());
            adminData.put("Email", mail.getText().toString());
            adminData.put("Profession", prof.getText().toString());
            adminData.put("ID", id.getText().toString());
            adminData.put("Place", place.getText().toString());
            adminData.put("Verified", 0);

            DocumentReference adminRef = db.collection("DataStorage").document("Admin")
                    .collection("AdminCollection").document(userID);

            adminRef.set(adminData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("AdminReg", "DocumentSnapshot successfully written!");

                            Toast.makeText(AdminRegister.this, "Admin Details upload to database!", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(AdminRegister.this, AdminFunctionsActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("AdminReg", "Error writing document", e);
                            Log.d("AdminReg", "Unable to upload receipt to user location");

                            Toast.makeText(AdminRegister.this, "Error while uploading to to database!", Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(AdminRegister.this, "Image uploaded to database!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
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
