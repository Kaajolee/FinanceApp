package com.example.finanseapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

public class IncomeActivity extends AppCompatActivity {
    ActionBar actionBar;
    Button buttonAdd, buttonCancel;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        // Ensure system bar inset is handled correctly
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Action Bar setup
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
            finish();  // Logic to add income source can be implemented here
        });

        // Cancel button logic
        buttonCancel.setOnClickListener(v -> finish());

        // Spinner initialization and category loading
        spinner = findViewById(R.id.spinner3);
        loadIncomeCategories();
    }

    private void loadIncomeCategories() {
        new Thread(() -> {
            // Get the database instance and DAO
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            CategoryDao categoryDao = db.categoryDao();

            // Fetch income categories (type 0 represents income)
            List<Category> categories = categoryDao.getIncomeCategories();

            List<String> categoryNames = new ArrayList<>();
            for (Category category : categories) {
                categoryNames.add(category.getName());
            }

            // Update Spinner on main thread
            runOnUiThread(() -> {
                if (spinner != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(IncomeActivity.this,
                            android.R.layout.simple_spinner_item, categoryNames);
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
