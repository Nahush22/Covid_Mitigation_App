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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class DoctorSignUp extends AppCompatActivity {

    private static final String TAG = "DoctorSignUp";
    EditText name, number, address, domain, clinicName, id;
    TextView fromTime, toTime, fromAdd, fromSub, toAdd, toSub;
    ImageView img;
    Button chooseImg, uploadImg;

    String types[] = {"Am", "Pm"};

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ACTUALUSERID = "actualuserid";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    StorageReference storageReference;
    private StorageTask uploadTask;

    public Uri imguri;

    String userID = "NAN" ;

    ProgressBar progressBar;

    String fromType, toType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.material_doctor_sign_up);

        storageReference = FirebaseStorage.getInstance().getReference("DoctorID");

        progressBar = findViewById(R.id.materialDoctorProgressBar);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userID = sharedPreferences.getString(ACTUALUSERID, "NAN");

        getData();

        name = findViewById(R.id.materialDocName);
        number = findViewById(R.id.materialDocNo);
        address = findViewById(R.id.materialDocAddr);
        domain = findViewById(R.id.materialDocProf);
        clinicName = findViewById(R.id.materialDocClinicName);
        id = findViewById(R.id.materialDocId);
        fromTime = findViewById(R.id.materialDocFrom);
        toTime = findViewById(R.id.materialDocTo);

        fromAdd = findViewById(R.id.materialDocFromAdd);
        fromSub = findViewById(R.id.materialDocFromSub);

        toAdd = findViewById(R.id.materialDocToAdd);
        toSub = findViewById(R.id.materialDocToSub);

        chooseImg = findViewById(R.id.materialDocSelectImg);

        img = findViewById(R.id.materialDocImg);

        uploadImg = findViewById(R.id.materialDocReg);

        fromAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int no = Integer.parseInt(fromTime.getText().toString());
                if (no == 20) {
                    fromTime.setText(String.valueOf(no));
                } else {
                    no++;
                    fromTime.setText(String.valueOf(no));
                }
            }
        });

        fromSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int no = Integer.parseInt(fromTime.getText().toString());
                if (no == 6 ) {
                    fromTime.setText(String.valueOf(no));
                } else {
                    no--;
                    fromTime.setText(String.valueOf(no));
                }
            }
        });

        toAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int no = Integer.parseInt(toTime.getText().toString());
                if (no == 22) {
                    toTime.setText(String.valueOf(no));
                } else {
                    no++;
                    toTime.setText(String.valueOf(no));
                }
            }
        });

        toSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int no = Integer.parseInt(toTime.getText().toString());

                int diff = no - Integer.parseInt(fromTime.getText().toString());

                if(diff != 1) //To give minimum 1hr gap between starting & ending time
                {
                    if (no == 7) {
                        toTime.setText(String.valueOf(no));
                    } else {
                        no--;
                        toTime.setText(String.valueOf(no));
                    }
                }

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
                    Toast.makeText(DoctorSignUp.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(name.equals(null) || number.equals(null) || address.equals(null) || id.equals(null) || fromTime.equals(null) || toTime.equals(null) || domain.equals(null) || clinicName.equals(null) ||
                            imguri == null)
                    {
                        Toast.makeText(DoctorSignUp.this, "Ensure all fields are filled properly", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        FileUploader();
                        Intent intent = new Intent(DoctorSignUp.this, DoctorLocationActivity.class);
                        intent.putExtra("name", name.getText().toString());
                        intent.putExtra("no", number.getText().toString());
                        intent.putExtra("address", address.getText().toString());
                        intent.putExtra("clinic", clinicName.getText().toString());
                        intent.putExtra("fromTime", fromTime.getText().toString());
                        intent.putExtra("toTime", toTime.getText().toString());
                        intent.putExtra("domain", domain.getText().toString());
                        intent.putExtra("id", id.getText().toString());
                        intent.putExtra("UserId", userID);

                        finish();
                        startActivity(intent);
                    }
                }
            }
        });

    }

    private void getData() {
        db.collection("Doctor").document(userID)
            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        if(document.get("Accepted").toString().equals("1"))
                        {
                            finish();
                            startActivity(new Intent(DoctorSignUp.this, PatientRequestList.class));
                        }
                        else if(document.get("Denied").toString().equals("1"))
                        {
                            finish();
                            startActivity(new Intent(DoctorSignUp.this, DoctorReqStatus.class).putExtra("Status", "Your registration has been denied!"));
                        }
                        else
                        {
                            finish();
                            startActivity(new Intent(DoctorSignUp.this, DoctorReqStatus.class).putExtra("Status", "Your registration is pending approval!"));
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
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

                        Toast.makeText(DoctorSignUp.this, "Photo uploaded to database!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...

                        Toast.makeText(DoctorSignUp.this, "Unable to upload photo to database!", Toast.LENGTH_SHORT).show();
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
