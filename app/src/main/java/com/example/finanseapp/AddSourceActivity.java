package com.example.finanseapp;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.finanseapp.Daos.CategoryDao;
import com.example.finanseapp.Entities.Category;
import com.example.finanseapp.Entities.Entry;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddSourceActivity extends AppCompatActivity {

    private AppDatabase db;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private Button buttonAdd, buttonCancel;
    private ImageView coinImage;
    private boolean canSpin = true;
    private Spinner spinner;
    private EditText nameEditText, amountEditText;
    private TextView incomeText, expenseText;
    private SwitchCompat switchCompat;
    private String selectedCountryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);
        
        setupWindowInsets();

        db = AppDatabase.getInstance(getApplicationContext());

        initializeUI();

        loadIncomeCategories();

        selectedCountryCode = MainActivity.COUNTRY_CODE;
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeUI() {
        spinner = findViewById(R.id.spinner3);

        coinImage = findViewById(R.id.imageView8);
        setupCoinFlip();

        nameEditText = findViewById(R.id.editTextName);
        amountEditText = findViewById(R.id.editTextAmount);

        incomeText = findViewById(R.id.textViewIncome);
        expenseText = findViewById(R.id.textViewExpense);

        switchCompat = findViewById(R.id.customSwitch);
        setupSwitchListener();

        setupActionBar();

        buttonAdd = findViewById(R.id.addButton);
        buttonCancel = findViewById(R.id.cancelButton);
        buttonAdd.setOnClickListener(v -> addSource());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void setupSwitchListener() {
        if (switchCompat != null) {
            boolean state = switchCompat.isChecked();
            updateTextColors(state);

            //switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> updateTextColors(isChecked), rotateEuroCoin());
            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rotateEuroCoin(isChecked);
                    updateTextColors(isChecked);
                }
            });
        }
    }

    private void updateTextColors(boolean isExpense) {
        int incomeColor = isExpense ? R.color.black : R.color.green_005;
        int expenseColor = isExpense ? R.color.red : R.color.black;
        incomeText.setTextColor(getColor(incomeColor));
        expenseText.setTextColor(getColor(expenseColor));
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add a Source");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.topbar_box));
        }
    }
    private void setupCoinFlip(){

        coinImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator spin = ObjectAnimator.ofFloat(coinImage, "rotationY",
                        0f, 1440f);
                spin.setInterpolator(new AccelerateDecelerateInterpolator());
                spin.setDuration(1500);
                spin.start();
            }
        });

    }

    private void addSource() {
        Category selectedCategory = (Category) spinner.getSelectedItem();
        int typeId = switchCompat.isChecked() ? 1 : 0;
        selectedCountryCode = MainActivity.COUNTRY_CODE;
        if (!nameEditText.getText().toString().isEmpty() && isNumber(amountEditText.getText().toString())) {
            if (selectedCountryCode == null) selectedCountryCode = "US";

            Entry newEntry = new Entry(
                    nameEditText.getText().toString(),
                    db.currentAccount,
                    typeId,
                    Float.parseFloat(amountEditText.getText().toString()),
                    2025,
                    selectedCountryCode
            );

            executor.execute(() -> {
                try {
                    db.entryDao().insert(newEntry);
                    runOnUiThread(() -> {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("entry_added", true);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(AddSourceActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });
        } else {
            setResult(RESULT_CANCELED);
            Toast.makeText(AddSourceActivity.this, "Name and Amount cannot be empty or invalid", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadIncomeCategories() {
        executor.execute(() -> {
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
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private boolean isNumber(String string) {
        try {
            Float.parseFloat(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    boolean turn_side = true;
    final int RED_COLOR = Color.RED;
    final int GREEN_COLOR = Color.GREEN;
    void rotateEuroCoin(boolean isChecked) {

        int start_color = isChecked ? GREEN_COLOR : RED_COLOR;
        int end_color = isChecked ? RED_COLOR : GREEN_COLOR;

        float start = isChecked ? 0f : 180f;
        float end = isChecked ? 180f : 0f;

        ObjectAnimator spin = ObjectAnimator.ofFloat(coinImage, "rotationY",
                start, end);
        spin.setInterpolator(new AccelerateDecelerateInterpolator());
        spin.setDuration(1000);
        spin.start();

        ArgbEvaluator colorEvaluator = new ArgbEvaluator();
        ObjectAnimator changeColor = ObjectAnimator.ofObject(coinImage, "colorFilter", colorEvaluator, start_color, end_color);
        changeColor.setInterpolator(new AccelerateDecelerateInterpolator());
        changeColor.setDuration(1000);
        changeColor.start();

        //coinImage.getDrawable()
        Drawable drawable = coinImage.getDrawable();
        if (drawable instanceof AnimatedVectorDrawable) {
            AnimatedVectorDrawable avd = (AnimatedVectorDrawable) drawable;
            avd.start();
        } else if (drawable instanceof AnimatedVectorDrawableCompat) {
            AnimatedVectorDrawableCompat avdc = (AnimatedVectorDrawableCompat) drawable;
            avdc.start();
        }
    }
}
