package com.example.Covid19App;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminTaskCompletionList extends AppCompatActivity {

    private static final String TAG = "AdminTaskCompletionList:";
    ArrayList<String> brief = new ArrayList<>();
    ArrayList<String> volId = new ArrayList<>();
    ArrayList<String> docId = new ArrayList<>();

    String userId;
    int position;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView taskView;
    AdminTaskLiskAdapter adminTaskLiskAdapter;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ACTUALUSERID = "actualuserid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_task_completion_list);

        loadUserId();

        taskView = findViewById(R.id.adminTaskRecyclerView);
        taskView.setLayoutManager(new LinearLayoutManager(this));

        adminTaskLiskAdapter = new AdminTaskLiskAdapter(this, brief, volId, new AdminTaskLiskAdapter.AdminTaskListClick() {
            @Override
            public void onEvent(int pos) {
                position = pos;
                acceptVolunteerCompletion();
            }
        });
        taskView.setAdapter(adminTaskLiskAdapter);

        getStoredData();

        initialiseDbListener();
    }

    private void acceptVolunteerCompletion() {

        db.collection("DataStorage").document("Admin").collection("AdminCollection").document(userId).collection("Tasks").document(docId.get(position))
                .update("CompletionRequest", 0)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        db.collection("DataStorage").document("Admin").collection("AdminCollection").document(userId).collection("Tasks").document(docId.get(position))
                .update("Acknowledgement", 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        db.collection("DataStorage").document("Admin").collection("AdminCollection").document(userId).collection("Tasks").document(docId.get(position))
                .update("Completed", 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        db.collection("VolunteerID").document(volId.get(position))
                .update("Assigned", 0)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                        Toast.makeText(AdminTaskCompletionList.this, "Task completion request accepted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });



    }

    private void initialiseDbListener() {

        db.collection("DataStorage").document("Admin").collection("AdminCollection").document(userId).collection("Tasks")
                .whereEqualTo("CompletionRequest", 1)
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
                            brief.clear();
                            volId.clear();
                            docId.clear();


                            for (QueryDocumentSnapshot document : snapshot) {
                                if(document.get("Brief") != null && document.get("VolunteerID") != null && document.get("AdminID").toString().equals(userId)) {
                                    docId.add(document.getId());
                                    brief.add(document.get("Brief").toString());
                                    volId.add(document.get("VolunteerID").toString());

                                }
                            }

                            recyclerViewInitialise();

//                            storeListAdapter.notifyDataSetChanged();
                        }

                    }
                });
    }

    private void getStoredData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Completion Requests");
        progressDialog.show();

        db.collection("DataStorage").document("Admin").collection("AdminCollection").document(userId).collection("Tasks")
                .whereEqualTo("CompletionRequest", 1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            docId.clear();
                            brief.clear();
                            volId.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                if(document.get("Brief") != null && document.get("VolunteerID") != null) {
                                    docId.add(document.getId());
                                    brief.add(document.get("Brief").toString());
                                    volId.add(document.get("VolunteerID").toString());

                                }
                            }

                            recyclerViewInitialise();

                            progressDialog.dismiss();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    private void recyclerViewInitialise() {


        adminTaskLiskAdapter = new AdminTaskLiskAdapter(this, brief, volId, new AdminTaskLiskAdapter.AdminTaskListClick() {
            @Override
            public void onEvent(int pos) {
                position = pos;
                acceptVolunteerCompletion();
            }
        });
        taskView.setAdapter(adminTaskLiskAdapter);
        Log.d(TAG, "Recycler view initialisation");
        taskView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadUserId() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getString(ACTUALUSERID, "");

    }
}
