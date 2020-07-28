package com.example.Covid19App;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FoodLocationActivity extends AppCompatActivity {

    EditText name, no, address, type, items;
    Button selectLocBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_location);

        name = findViewById(R.id.foodUserName);
        no = findViewById(R.id.foodUserNo);
        address = findViewById(R.id.foodUserAddress);
        type = findViewById(R.id.foodUserType);
        items = findViewById(R.id.foodUserItems);

        selectLocBtn = findViewById(R.id.foodSelectLocBtn);

        selectLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString() == null || no.getText().toString() == null || address.getText().toString() == null || type.getText().toString() == null || items.getText().toString() == null )
                {
                    Toast.makeText(FoodLocationActivity.this, "Fill in all the details!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), FoodAddressActivity.class);
                    intent.putExtra("Name", name.getText().toString());
                    intent.putExtra("No", no.getText().toString());
                    intent.putExtra("Address", address.getText().toString());
                    intent.putExtra("Type", type.getText().toString());
                    intent.putExtra("Items", items.getText().toString());

                    finish();
                    startActivity(intent);
                }
            }
        });
    }
}
