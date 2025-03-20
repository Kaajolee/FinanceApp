package com.example.finanseapp;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.finanseapp.Entities.Category;
import com.example.finanseapp.Enums.CategoryType;

public class AddCategoryActivity extends AppCompatActivity {

    ActionBar actionBar;
    Button buttonAdd, buttonCancel;
    Spinner categoryTypeSpinner;
    EditText editTextName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        // Ensure system bar inset is handled correctly
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up spinner
        categoryTypeSpinner = findViewById(R.id.spinner4);
        ArrayAdapter<CategoryType> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CategoryType.values());
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        categoryTypeSpinner.setAdapter(adapter);

        // Action Bar setup
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add a Category");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.topbar_box));
        }

        // Initialize button references
        buttonAdd = findViewById(R.id.addButton);
        editTextName = findViewById(R.id.editTextName);

        // Add category button logic
        buttonAdd.setOnClickListener(v -> {
            String categoryName = editTextName.getText().toString().trim();
            if (categoryName.isEmpty()) {
                Toast.makeText(AddCategoryActivity.this, "Please enter a category name", Toast.LENGTH_SHORT).show();
                return;
            }

            CategoryType selectedType = (CategoryType) categoryTypeSpinner.getSelectedItem();
            int typeId = selectedType.ordinal();

            Category newCategory = new Category(categoryName, typeId);

            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                db.categoryDao().insert(newCategory);

                Category insertedCategory = db.categoryDao().getCategoryByName(categoryName);
                runOnUiThread(() -> {
                    if (insertedCategory != null) {
                        Toast.makeText(AddCategoryActivity.this, "Category added successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddCategoryActivity.this, "Failed to add category.", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                });
            }).start();
        });

        // Cancel button logic
        buttonCancel = findViewById(R.id.cancelButton);
        buttonCancel.setOnClickListener(v -> finish());
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
