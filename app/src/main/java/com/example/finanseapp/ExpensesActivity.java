package com.example.finanseapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ExpensesActivity extends AppCompatActivity {
    ActionBar actionBar;
    Button buttonAdd;
    Button buttonCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expenses);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //-----TOP ACTION BAR
        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Add an expense");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        //-----ADD BUTTON
        buttonAdd = findViewById(R.id.addButton);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add button logic
                finish();
            }
        });


        //-----CANCEL BUTTON
        buttonCancel = findViewById(R.id.cancelButton);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cancel logic
                finish();
            }
        });
    }


    //-----TOP ACTION BAR
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //---HOME YRA DEFAULT ID KAD SUGRIZT I DEFAULT ACTIVITY
        if(item.getItemId() == android.R.id.home){

            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}