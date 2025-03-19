package com.example.finanseapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finanseapp.Entities.Entry;

import java.util.concurrent.Executors;

public class IncomeActivity extends AppCompatActivity {
    AppDatabase db;
    ActionBar actionBar;
    Button buttonAdd, buttonCancel;
    EditText nameEditText,amountEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_income);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(getApplicationContext());

        nameEditText = (EditText) findViewById(R.id.editTextName);
        amountEditText = (EditText) findViewById(R.id.editTextAmount);

        //-----TOP ACTION BAR
        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Add an income source");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        //-----ADD BUTTON
        buttonAdd = findViewById(R.id.addButton);
        if(buttonAdd != null)
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Add button logic
                    if (!nameEditText.getText().toString().isEmpty() ||
                        !amountEditText.getText().toString().isEmpty())
                    {
                        Entry newEntry = new Entry(
                                nameEditText.getText().toString(),
                                db.currentAccount,
                                0,
                                Float.parseFloat(amountEditText.getText().toString()),
                                2025);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            try {
                                db.entryDao().insert(newEntry);

                                runOnUiThread(() -> {
                                    Toast.makeText(IncomeActivity.this, "Successfully Added!", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                            } catch (Exception e) {
                                runOnUiThread(() ->
                                        Toast.makeText(IncomeActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                            }

                        });
                    }
                    else {
                        Toast.makeText(IncomeActivity.this, "Invalid inputs", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        //-----CANCEL BUTTON
        buttonCancel = findViewById(R.id.cancelButton);
        if(buttonCancel != null)
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