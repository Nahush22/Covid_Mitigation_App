package com.example.Covid19App;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class DoctorReqStatus extends AppCompatActivity {

    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.material_doctor_req_status);

        status = findViewById(R.id.materialDocStatusTxt);

        Bundle b = getIntent().getExtras();
        if(b == null)
        {
            Toast.makeText(this, "Unable to get status.Come back again", Toast.LENGTH_SHORT).show();
        }
        else {
            String stat = b.getString("Status");
            status.setText(stat);
        }
    }
}
