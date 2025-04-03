package com.example.finanseapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finanseapp.Daos.CategoryDao;
import com.example.finanseapp.Entities.Category;
import com.example.finanseapp.Entities.Entry;

import java.util.List;
import java.util.concurrent.Executors;

public class AddSourceActivity extends AppCompatActivity {
    AppDatabase db;
    ActionBar actionBar;
    Button buttonAdd, buttonCancel;
    Spinner spinner;
    EditText nameEditText, amountEditText;
    TextView incomeText, expenseText;
    SwitchCompat switchCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(getApplicationContext());

        //Spinner
        spinner = findViewById(R.id.spinner3);
        loadIncomeCategories();

        //EditText
        nameEditText = findViewById(R.id.editTextName);
        amountEditText = findViewById(R.id.editTextAmount);

        //TextView
        incomeText = findViewById(R.id.textViewIncome);
        expenseText = findViewById(R.id.textViewExpense);

        //Switch
        switchCompat = findViewById(R.id.customSwitch);
        if (switchCompat != null) {
            boolean state = switchCompat.isChecked();

            //expense
            if (state) {
                ChangeTextColors(R.color.black, R.color.red);
            }
            //income
            else {
                ChangeTextColors(R.color.green_005, R.color.black);
            }

            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ChangeTextColors(R.color.black, R.color.red);
                        //switchCompat.setTrackTintList(ColorStateList.valueOf(getColor(R.color.red)));
                    }
                    //income
                    else {
                        ChangeTextColors(R.color.green_005, R.color.black);
                        //switchCompat.setTrackTintList(ColorStateList.valueOf(getColor(R.color.green_005)));
                    }
                }
            });
        }

        //Action bar
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add a Source");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.topbar_box));
        }

        //Buttons
        buttonAdd = findViewById(R.id.addButton);
        buttonCancel = findViewById(R.id.cancelButton);

        buttonAdd.setOnClickListener(v -> addSource());
        buttonCancel.setOnClickListener(v -> finish());
    }


    private void addSource() {
        Category selectedCategory = (Category) spinner.getSelectedItem();

        //int typeId = selectedCategory.getType();
        int typeId = ReturnSwitchStateInt();

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
                            Toast.makeText(AddSourceActivity.this, "Successfully Added!", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } catch (Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(AddSourceActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    }
                });
            } else {
                Toast.makeText(AddSourceActivity.this, "Amount must be a positive number", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AddSourceActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadIncomeCategories() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            CategoryDao categoryDao = db.categoryDao();
            List<Category> categories = categoryDao.getIncomeCategories();

            runOnUiThread(() -> {
                if (spinner != null) {
                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(AddSourceActivity.this,
                            R.layout.spinner_dropdown_main, categories);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown);
                    spinner.setAdapter(adapter);
                } else {
                    Toast.makeText(AddSourceActivity.this, "Failed to load categories.", Toast.LENGTH_SHORT).show();
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

        if (string.isEmpty())
            return false;

        for (int i = 0; i < string.length(); i++) {
            if (!Character.isDigit(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private void ChangeTextColors(int incomeColorID, int expenseColorID) {
        incomeText.setTextColor(getColor(incomeColorID));
        expenseText.setTextColor(getColor(expenseColorID));
    }

    private int ReturnSwitchStateInt() {

        int stateToInt;
        return stateToInt = switchCompat.isChecked() ? 1 : 0;
    }

}
