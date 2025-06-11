package com.example.finanseapp;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.finanseapp.Daos.CategoryDao;
import com.example.finanseapp.Entities.Category;
import com.example.finanseapp.Entities.Entry;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddSourceActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST = 1001;
    private PreviewView previewView;
    private ProcessCameraProvider cameraProvider;

    private AppDatabase db;

    private ImageCapture imageCapture;
    private Bitmap photoBitMap;
    private TextRecognizer recognizer;
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
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
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

        buttonCaptureImage.setOnClickListener(v -> {
            if (imageCapture != null) {
                imageCapture.takePicture(
                        executor,
                        new ImageCapture.OnImageCapturedCallback() {
                            //jei pavyko nufotkint
                            @Override
                            public void onCaptureSuccess(@NonNull ImageProxy image) {
                                // konvertuoja image i bitmap
                                photoBitMap = imageProxyToBitmap(image);
                                //?
                                InputImage imageConverted = InputImage.fromBitmap(photoBitMap, 0);
                                readAndProcessText(imageConverted);

                                image.close();

                                runOnUiThread(() -> {
                                    Toast.makeText(AddSourceActivity.this,
                                            "Image captured and converted to bitmap",
                                            Toast.LENGTH_SHORT).show();
                                });
                            }
                            // jei nepavyko
                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                runOnUiThread(() -> Toast.makeText(AddSourceActivity.this,
                                        "Capture failed: " + exception.getMessage(),
                                        Toast.LENGTH_SHORT).show());
                            }
                        }
                );
            }
        });


    }
    private void readAndProcessText(InputImage image){

        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    String fullText = visionText.getText();
                    extractValuesFromText(fullText);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Text recognition failed", Toast.LENGTH_SHORT).show();
                });

    }
    private void extractValuesFromText(String text) {
        String[] lines = text.split("\n");
        String suma = null;
        String shopName = null;

        Pattern companyPattern = Pattern.compile("^(uab|ab)\\s+.*", Pattern.CASE_INSENSITIVE);
        Pattern amountLinePattern = Pattern.compile("(suma|mok[eė]ti|mok[eė]ti eur)", Pattern.CASE_INSENSITIVE);
        Pattern amountValuePattern = Pattern.compile("(\\d+[.,]\\d{2})");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            // Detect company name
            if (shopName == null && companyPattern.matcher(line).find()) {
                shopName = line;
            }

            // Detect payment line
            if (amountLinePattern.matcher(line).find()) {
                // Try to extract amount from the same line
                Matcher amountMatcher = amountValuePattern.matcher(line);
                if (amountMatcher.find()) {
                    suma = amountMatcher.group(1);
                } else if (i + 1 < lines.length) {
                    // Try to extract amount from the next line
                    String nextLine = lines[i + 1].trim();
                    Matcher nextLineMatcher = amountValuePattern.matcher(nextLine);
                    if (nextLineMatcher.find()) {
                        suma = nextLineMatcher.group(1);
                    }
                }
            }
        }

        Log.d("OCR", "Shop: " + shopName);
        Log.d("OCR", "Suma: " + suma);

        String finalShopName = shopName;
        String finalSuma = suma;
        runOnUiThread(() -> Toast.makeText(this, "Shop: " + finalShopName + "\nSUMA: "
                + finalSuma, Toast.LENGTH_LONG).show());
    }




    private Bitmap imageProxyToBitmap(ImageProxy image) {
        //gettina plane'us(jie sudaro visa ft)
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        // dekoduoja i bitmap
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void startCamera() {

        //View matomumas
        findViewById(R.id.cameraOverlay).setVisibility(View.VISIBLE);
        buttonCloseCamera.setVisibility(View.VISIBLE);
        buttonCaptureImage.setVisibility(View.VISIBLE);

        //pagettina kameros valdikli kuris atsakingas uz lifecycle kameros(async)
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        //kai valdiklis ready prides eventa
        cameraProviderFuture.addListener(() -> {
            try {

                cameraProvider = cameraProviderFuture.get();

                //susetupina live preview vaizdo
                Preview preview = new Preview.Builder().build();
                //susetupina image capture kad galetum nufotkint
                imageCapture = new ImageCapture.Builder().build();

                //kuri kamera
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                //isclearina praeitus use case'us
                cameraProvider.unbindAll();
                //prideda kameros lifecycle prie activity lifecycle
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
                //prijungia kameros vaizda prie view kad matytum realiai
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

            } catch (Exception e) {
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
