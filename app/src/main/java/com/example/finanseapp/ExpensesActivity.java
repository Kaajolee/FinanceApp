package com.example.finanseapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

public class ExpensesActivity extends AppCompatActivity {
    ActionBar actionBar;
    Button buttonAdd, buttonCancel;
    Spinner spinner;

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
        if(buttonAdd != null)
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Add button logic
                    finish();
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

        spinner = findViewById(R.id.spinner3); // Ensure this is the correct Spinner ID
        loadExpenseCategories();
    }

    // Method to load Expense categories from the database
    private void loadExpenseCategories() {
        new Thread(() -> {
            // Get the database instance
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            CategoryDao categoryDao = db.categoryDao();

            // Fetch expense categories from the database (where type = 1)
            List<Category> categories = categoryDao.getExpensesCategories();

            List<String> categoryNames = new ArrayList<>();
            for (Category category : categories) {
                categoryNames.add(category.getName());  // Add category names to the list
            }

            // Update the Spinner on the main thread
            runOnUiThread(() -> {
                // Ensure the spinner is initialized
                if (spinner != null) {
                    // Create an ArrayAdapter to bind category names to the Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ExpensesActivity.this,
                            android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Set dropdown view
                    spinner.setAdapter(adapter);  // Set the adapter to the Spinner
                } else {
                    // Handle failure case if spinner is not initialized
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
