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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finanseapp.Daos.CategoryDao;
import com.example.finanseapp.Entities.Category;
import com.example.finanseapp.Entities.Entry;
import com.example.finanseapp.Enums.CategoryType;

import java.util.List;
import java.util.concurrent.Executors;

public class IncomeActivity extends AppCompatActivity {
    AppDatabase db;
    ActionBar actionBar;
    Button buttonAdd, buttonCancel;
    Spinner spinner;
    EditText nameEditText, amountEditText;

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

        nameEditText = findViewById(R.id.editTextName);
        amountEditText = findViewById(R.id.editTextAmount);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add an Income Source");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.topbar_box));
        }

        buttonAdd = findViewById(R.id.addButton);
        buttonCancel = findViewById(R.id.cancelButton);

        buttonAdd.setOnClickListener(v -> addIncome());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void addIncome() {
        Category selectedCategory = (Category) spinner.getSelectedItem();
        int typeId = selectedCategory.getType();

        if (!nameEditText.getText().toString().isEmpty()) {
            if (isNumber(amountEditText.getText().toString())) {
                Entry newEntry = new Entry(
                        nameEditText.getText().toString(),
                        db.currentAccount,
                        typeId,
                        Float.parseFloat(amountEditText.getText().toString()),
                        2025
                );
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
            } else {
                Toast.makeText(IncomeActivity.this, "Amount must be a positive number", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(IncomeActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadIncomeCategories() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            CategoryDao categoryDao = db.categoryDao();
            List<Category> categories = categoryDao.getIncomeCategories();

            runOnUiThread(() -> {
                if (spinner != null) {
                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(IncomeActivity.this,
                            R.layout.spinner_dropdown_main, categories);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown);
                    spinner.setAdapter(adapter);
                } else {
                    Toast.makeText(IncomeActivity.this, "Failed to load categories.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isNumber(String string) {

        if(string.isEmpty())
            return false;

        for (int i = 0; i < string.length(); i++) {
            if (!Character.isDigit(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
