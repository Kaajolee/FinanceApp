package com.example.finanseapp;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.finanseapp.Daos.CategoryDao;
import com.example.finanseapp.Entities.Category;
import com.example.finanseapp.Entities.Entry;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddSourceActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST = 1001;
    private PreviewView previewView;
    private ProcessCameraProvider cameraProvider;
    private AppDatabase db;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private Button buttonAdd, buttonCancel, buttonCloseCamera, buttonCaptureImage;
    private ImageButton cameraImageButton;
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

        cameraImageButton = findViewById(R.id.imageButton2);
        previewView = findViewById(R.id.previewView);
        buttonCloseCamera = findViewById(R.id.buttonCloseCamera);
        buttonCaptureImage = findViewById(R.id.buttonCaptureImage);

        setUpCameraButtons();

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
                    updateTextColors(isChecked);
                }
            });
        }
    }
    private void setUpCameraButtons(){
        cameraImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(AddSourceActivity.this, Manifest.permission.CAMERA)
                   == PackageManager.PERMISSION_GRANTED)
                    startCamera();
                else
                    ActivityCompat.requestPermissions(AddSourceActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_REQUEST);
            }
        });

        buttonCloseCamera.setVisibility(View.GONE);
        buttonCaptureImage.setVisibility(View.GONE);

        buttonCloseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: sustabdyti preview


                findViewById(R.id.cameraOverlay).setVisibility(View.GONE);
                buttonCloseCamera.setVisibility(View.GONE);
                buttonCaptureImage.setVisibility(View.GONE);
                Toast.makeText(AddSourceActivity.this, "Cam closed", Toast.LENGTH_SHORT).show();
            }
        });

        buttonCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: nufotkint ir perdirbt duomenis

                Toast.makeText(AddSourceActivity.this, "Image captured", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void startCamera(){

        findViewById(R.id.cameraOverlay).setVisibility(View.VISIBLE);
        buttonCloseCamera.setVisibility(View.VISIBLE);
        buttonCaptureImage.setVisibility(View.VISIBLE);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                cameraProvider.bindToLifecycle(this, cameraSelector, preview);

            }
            catch (Exception e){
                Toast.makeText(this, "Failed to start the camera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults){

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CAMERA_PERMISSION_REQUEST){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startCamera();
            }
            else
                Toast.makeText(this, "Cam permission is req.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
        super.onDestroy();
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

}
