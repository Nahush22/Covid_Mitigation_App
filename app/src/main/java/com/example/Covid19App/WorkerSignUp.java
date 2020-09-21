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
import android.widget.ImageView;
import android.widget.Spinner;
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

public class WorkerSignUp extends AppCompatActivity {

    private static final String TAG = "WorkerSignUp";
    TextView name, no, home, work, org, prof, id;
    Spinner typeSpinner;
    TextView chooseImg;
    ImageView img;
    Button uploadBtn;

    String types[] = {"Health", "Essential"};

    String wType;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ACTUALUSERID = "actualuserid";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    StorageReference storageReference;
    private StorageTask uploadTask;

    public Uri imguri;

    String userID = "NAN" ;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_sign_up);

        storageReference = FirebaseStorage.getInstance().getReference("WorkerPass");

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userID = sharedPreferences.getString(ACTUALUSERID, "NAN");

        Bundle b= getIntent().getExtras();
        if(b==null)
        {
            progressDialog = new ProgressDialog(this);

//            Toast.makeText(getApplicationContext(), "Inside bundle null checker", Toast.LENGTH_SHORT).show();

            progressDialog.setMessage("Checking if registered....");
            progressDialog.show();

            checkDb();
        }

        name = findViewById(R.id.workerName);
        no = findViewById(R.id.workerNo);
        home = findViewById(R.id.workerHome);
        work = findViewById(R.id.workerWork);
        org = findViewById(R.id.workerOrg);
        prof = findViewById(R.id.workerProf);
        id = findViewById(R.id.workerId);

        typeSpinner = findViewById(R.id.workerTypeSpinner);
        typeSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types));

//        Toast.makeText(this, userID, Toast.LENGTH_SHORT).show();

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                wType = typeSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        chooseImg = findViewById(R.id.workerChooseImg);

        img = findViewById(R.id.workerImg);

        uploadBtn = findViewById(R.id.workerUploadImage);

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Filechooser();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadTask != null && uploadTask.isInProgress())
                {
                    Toast.makeText(WorkerSignUp.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(name.equals(null) || no.equals(null) || home.equals(null) || work.equals(null) || org.equals(null) || prof.equals(null) || id.equals(null) || imguri==null)
                    {
                        Toast.makeText(WorkerSignUp.this, "Ensure all fields are filled properly", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        FileUploader();
                        Intent intent = new Intent(WorkerSignUp.this, WorkerLocation.class);
                        intent.putExtra("name", name.getText().toString());
                        intent.putExtra("no", no.getText().toString());
                        intent.putExtra("home", home.getText().toString());
                        intent.putExtra("work", work.getText().toString());
                        intent.putExtra("type", wType);
                        intent.putExtra("org", org.getText().toString());
                        intent.putExtra("prof", prof.getText().toString());
                        intent.putExtra("id", id.getText().toString());

                        finish();
                        startActivity(intent);
                    }
                }
            }
        });

    }

    private void checkDb() {
//        Toast.makeText(getApplicationContext(), "Inside checkdb function", Toast.LENGTH_SHORT).show();

        DocumentReference docRef = db.collection("Worker").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

//                        Toast.makeText(getApplicationContext(), "Already registered", Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                        finish();
                        startActivity(new Intent(getApplicationContext(), WorkerTaskActivity.class));
                    } else {
                        Log.d(TAG, "No such document");

                        progressDialog.dismiss();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());

                    progressDialog.dismiss();

                    Toast.makeText(getApplicationContext(), "Unable to retrieve data from servers.If already registered retry after sometime...", Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(WorkerSignUp.this, "Photo uploaded to database!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...

                        Toast.makeText(WorkerSignUp.this, "Unable to upload photo to database!", Toast.LENGTH_SHORT).show();
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
