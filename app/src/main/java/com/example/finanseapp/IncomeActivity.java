package com.example.finanseapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finanseapp.Daos.CategoryDao;
import com.example.finanseapp.Entities.Category;

import java.util.ArrayList;
import java.util.List;
import com.example.finanseapp.Entities.Entry;
import com.example.finanseapp.Enums.CategoryType;

import java.util.concurrent.Executors;

public class IncomeActivity extends AppCompatActivity {
    AppDatabase db;
    ActionBar actionBar;
    Button buttonAdd, buttonCancel;
    Spinner spinner;
    EditText nameEditText,amountEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(getApplicationContext());

        spinner = findViewById(R.id.spinner3);
        loadIncomeCategories();

        nameEditText = (EditText) findViewById(R.id.editTextName);
        amountEditText = (EditText) findViewById(R.id.editTextAmount);

        //-----TOP ACTION BAR
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add an Income Source");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Initialize buttons
        buttonAdd = findViewById(R.id.addButton);
        buttonCancel = findViewById(R.id.cancelButton);

        // Add button logic
        buttonAdd.setOnClickListener(v -> {
            finish();
        });

        // Cancel button logic
        buttonCancel.setOnClickListener(v -> finish());
        //-----ADD BUTTON
        buttonAdd = findViewById(R.id.addButton);
        if(buttonAdd != null)
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Add button logic

                    Category selectedCategory = (Category) spinner.getSelectedItem();
                    int typeId = selectedCategory.getType();

                    if (!nameEditText.getText().toString().isEmpty() ||
                            !amountEditText.getText().toString().isEmpty())
                    {
                        Entry newEntry = new Entry(
                                nameEditText.getText().toString(),
                                db.currentAccount,
                                typeId,
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
    }

    private void loadIncomeCategories() {
        new Thread(() -> {
            // Get the database instance and DAO
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            CategoryDao categoryDao = db.categoryDao();

            // Fetch income categories (type 0 represents income)
            List<Category> categories = categoryDao.getIncomeCategories();

            // Update Spinner on main thread
            runOnUiThread(() -> {
                if (spinner != null) {
                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(IncomeActivity.this,
                            android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    Toast.makeText(IncomeActivity.this, "Failed to load categories.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    // Handle top action bar (back button)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
