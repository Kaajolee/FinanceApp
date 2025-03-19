package com.example.finanseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button buttonIncome;
    Intent intentIncome;
    Button buttonExpenses;
    Intent intentExpenses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/


        // --------INCOME BUTTON
        intentIncome = new Intent(getApplicationContext(), IncomeActivity.class);
        buttonIncome = findViewById(R.id.button);
        if(buttonIncome != null){

            buttonIncome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intentIncome);
                }
            });
        }

        // --------EXPENSES BUTTON
        intentExpenses = new Intent(getApplicationContext(), ExpensesActivity.class);
        buttonExpenses = findViewById(R.id.button2);
        if(buttonExpenses != null){

            buttonExpenses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intentExpenses);
                }
            });
        }
    }
}