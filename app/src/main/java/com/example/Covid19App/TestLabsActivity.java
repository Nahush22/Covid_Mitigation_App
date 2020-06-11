package com.example.Covid19App;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;

public class TestLabsActivity extends AppCompatActivity {

    private static final String TAG = "TestLabsActivity";
    RecyclerView labView;
    TestLabsAdapter testLabsAdapter;

    ProgressBar progressBar;

    Spinner spinner;

    String type = "CoVID-19 Testing Lab";

    ArrayList<LabClass> labs = new ArrayList<>();

    String types[] = {"CoVID-19 Testing Lab", "Delivery [Vegetables, Fruits, Groceries, Medicines, etc.]", "Free Food", "Government Helpline", "Accommodation and Shelter Homes", "Hospitals and Centers"
    , "Community Kitchen", "Mental well being and Emotional Support", "Quarantine Facility"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_labs);

        labView = findViewById(R.id.labRecyclerView);
        labView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.testLabProgressBar);

        spinner = findViewById(R.id.tl_spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = spinner.getSelectedItem().toString();
                progressBar.setVisibility(View.VISIBLE);
                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getData();
        showRecyclerView();


    }

    private void showRecyclerView() {
        testLabsAdapter = new TestLabsAdapter(this, labs);
        labView.setAdapter(testLabsAdapter);
    }

    private void getData() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String url = "https://api.covid19india.org/resources/resources.json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);

                if(response != null)
                {
                    Log.d(TAG, response);

                    try {
//                        https://stackoverflow.com/questions/32624166/how-to-get-json-array-within-json-object/32624372

                        JSONObject jsonObject = new JSONObject(response);
                        Log.d(TAG, jsonObject.toString());

                        JSONArray jsonArray = jsonObject.getJSONArray("resources");

                        int length = jsonArray.length();

                        labs.clear();

                        for(int i = 0; i < length; i++)
                        {
                            JSONObject jObj = jsonArray.getJSONObject(i);

                            if(jObj.getString("category").equals(type))
                            {
                                String category = jObj.getString("category");
                                String city = jObj.getString("city");
                                String contact = jObj.getString("contact");
                                String desc = jObj.getString("descriptionandorserviceprovided");
                                String name = jObj.getString("nameoftheorganisation");
                                String number = jObj.getString("phonenumber");
                                String state = jObj.getString("state");

                                LabClass labClass = new LabClass(category, city, contact, desc, name, number, state);
                                labs.add(labClass);


                            }
                        }

                        showRecyclerView();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TestLabsActivity.this, "Unable to get lab data", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);

    }
}
