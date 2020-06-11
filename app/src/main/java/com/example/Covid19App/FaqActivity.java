package com.example.Covid19App;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FaqActivity extends AppCompatActivity {

    CardView faqBasicCard, faqSpreadCard, faqProtectCard, faqChildrenCard, faqChildrenHealthCard, faqHomeCard, faqOutbreakCard, faqSymptomsCard, faqRiskCard, faqBpCard, faqPetCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        faqBasicCard = findViewById(R.id.faqBasicCard);
        faqSpreadCard = findViewById(R.id.faqSpreadCard);
        faqProtectCard = findViewById(R.id.faqProtectCard);
        faqChildrenCard = findViewById(R.id.faqChildrenCard);
        faqChildrenHealthCard = findViewById(R.id.faqChildrenHealthCard);
        faqHomeCard = findViewById(R.id.faqHomeCard);
        faqOutbreakCard = findViewById(R.id.faqOutbreakCard);
        faqSymptomsCard = findViewById(R.id.faqSymptomsCard);
        faqRiskCard = findViewById(R.id.faqRiskCard);
        faqBpCard = findViewById(R.id.faqBpCard);
        faqPetCard = findViewById(R.id.faqPetCard);

        faqBasicCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaqActivity.this, BasicsList.class);
                intent.putExtra("type", "basic");
                startActivity(intent);
            }
        });

        faqSpreadCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaqActivity.this, BasicsList.class);
                intent.putExtra("type", "spread");
                startActivity(intent);
            }
        });

        faqProtectCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaqActivity.this, BasicsList.class);
                intent.putExtra("type", "protect");
                startActivity(intent);
            }
        });

        faqChildrenCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaqActivity.this, BasicsList.class);
                intent.putExtra("type", "children");
                startActivity(intent);
            }
        });

        faqChildrenHealthCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaqActivity.this, BasicsList.class);
                intent.putExtra("type", "childhealth");
                startActivity(intent);
            }
        });

        faqHomeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaqActivity.this, BasicsList.class);
                intent.putExtra("type", "home");
                startActivity(intent);
            }
        });

        faqOutbreakCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaqActivity.this, BasicsList.class);
                intent.putExtra("type", "outbreak");
                startActivity(intent);
            }
        });

        faqSymptomsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaqActivity.this, BasicsList.class);
                intent.putExtra("type", "symptoms");
                startActivity(intent);
            }
        });

        faqRiskCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaqActivity.this, BasicsList.class);
                intent.putExtra("type", "risk");
                startActivity(intent);
            }
        });

        faqBpCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaqActivity.this, BasicsList.class);
                intent.putExtra("type", "bp");
                startActivity(intent);
            }
        });

        faqPetCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaqActivity.this, BasicsList.class);
                intent.putExtra("type", "pet");
                startActivity(intent);
            }
        });
    }
}
