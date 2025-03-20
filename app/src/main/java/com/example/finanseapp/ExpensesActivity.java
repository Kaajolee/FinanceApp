package com.example.finanseapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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

public class ExpensesActivity extends AppCompatActivity {
    AppDatabase db;
    ActionBar actionBar;
    Button buttonAdd, buttonCancel;
    Spinner spinner;

    EditText nameEditText,amountEditText;
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

        db = AppDatabase.getInstance(getApplicationContext());

        nameEditText = (EditText) findViewById(R.id.editTextName);
        amountEditText = (EditText) findViewById(R.id.editTextAmount);


        //-----TOP ACTION BAR
        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Add an expense");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //-----ADD BUTTON
        buttonAdd = findViewById(R.id.addButton);
        if(buttonAdd != null)
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Add button logic
                    finish();
                    Category selectedCategory = (Category) spinner.getSelectedItem();
                    int typeId = selectedCategory.getType();

                    //Add button logic
                    if (!nameEditText.getText().toString().isEmpty() ||
                            !amountEditText.getText().toString().isEmpty())
                    {
                        Entry newEntry = new Entry(
                                nameEditText.getText().toString(),
                                db.currentAccount,
                                typeId,
                                -1 * Float.parseFloat(amountEditText.getText().toString()),
                                2025);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            try {
                                db.entryDao().insert(newEntry);

                                runOnUiThread(() -> {
                                    Toast.makeText(ExpensesActivity.this, "Successfully Added!", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                            } catch (Exception e) {
                                runOnUiThread(() ->
                                        Toast.makeText(ExpensesActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                            }

                        });
                    }
                    else {
                        Toast.makeText(ExpensesActivity.this, "Invalid inputs", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        //-----CANCEL BUTTON
        buttonCancel = findViewById(R.id.cancelButton);
        if(buttonCancel != null)
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Cancel logic
                    finish();
                }
            });

        spinner = findViewById(R.id.spinner3);
        loadExpenseCategories();
    }

    // Method to load Expense categories from the database
    private void loadExpenseCategories() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            CategoryDao categoryDao = db.categoryDao();

            List<Category> categories = categoryDao.getExpensesCategories();

            runOnUiThread(() -> {
                if (spinner != null) {
                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(ExpensesActivity.this,
                            android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    Toast.makeText(ExpensesActivity.this, "Failed to load categories.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
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
