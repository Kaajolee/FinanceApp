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
import androidx.core.content.ContextCompat;
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
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.topbar_box));
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
                    if (!nameEditText.getText().toString().isEmpty()) {
                        if (isNumber(amountEditText.getText().toString())) {


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
                        } else {
                            Toast.makeText(ExpensesActivity.this, "Amount must be a positive number", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ExpensesActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
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
                            R.layout.spinner_dropdown_main, categories);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown);
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
    private boolean isNumber(String string) {

        if(string.isEmpty())
            return false;
        if(string.charAt(0) == '-')
            return false;

        for (int i = 0; i < string.length(); i++) {
            if (!Character.isDigit(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }



}
