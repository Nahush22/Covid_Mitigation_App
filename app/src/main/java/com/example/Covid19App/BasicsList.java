package com.example.Covid19App;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;

public class BasicsList extends AppCompatActivity {

    RecyclerView recyclerView;
    BasicsListAdapter basicsListAdapter;

    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> content = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basics_list);

        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {

                newString= extras.getString("type");

                if(newString.equals("basic"))
                {
                    title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.basic_title)));
                    content = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.basic_text)));
                }
                if(newString.equals("spread"))
                {
                    title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.spread_title)));
                    content = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.spread_text)));
                }

                if(newString.equals("protect"))
                {
                    title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.protect_title)));
                    content = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.protect_text)));
                }

                if(newString.equals("children"))
                {
                    title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.children_title)));
                    content = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.children_text)));
                }

                if(newString.equals("childhealth"))
                {
                    title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.childhealth_title)));
                    content = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.childhealth_text)));
                }

                if(newString.equals("home"))
                {
                    title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.home_title)));
                    content = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.home_text)));
                }

                if(newString.equals("outbreak"))
                {
                    title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.outbreak_title)));
                    content = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.outbreak_text)));
                }

                if(newString.equals("symptoms"))
                {
                    title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.symptoms_title)));
                    content = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.symptoms_text)));
                }

                if(newString.equals("risk"))
                {
                    title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.risk_title)));
                    content = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.risk_text)));
                }

                if(newString.equals("bp"))
                {
                    title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.bp_title)));
                    content = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.bp_text)));
                }

                if(newString.equals("pet"))
                {
                    title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.pet_title)));
                    content = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.pet_text)));
                }

            }
        } else {
            newString= (String) savedInstanceState.getSerializable("type");
        }


        recyclerView = findViewById(R.id.basicsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        basicsListAdapter = new BasicsListAdapter(this, title, content);

        recyclerView.setAdapter(basicsListAdapter);
    }
}
