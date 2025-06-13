package com.example.finanseapp;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
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
import android.widget.FrameLayout;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.finanseapp.Daos.CategoryDao;
import com.example.finanseapp.Entities.Category;
import com.example.finanseapp.Entities.Entry;
import com.example.finanseapp.Helpers.RecyclerViewImagesAdapter;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddSourceActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST = 1001;
    private PreviewView previewView;
    private FrameLayout cameraOverlay;
    private ProcessCameraProvider cameraProvider;

    private AppDatabase db;

    private ImageCapture imageCapture;
    private Bitmap photoBitMap;
    private Set<String> imagePaths = new HashSet<>();
    private SharedPreferences prefs;
    private TextRecognizer recognizer;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private Button buttonAdd, buttonCancel, buttonAddPhoto, buttonClearImages,
            buttonCloseCamera, buttonCaptureImage;
    private boolean canSpin = true;
    private Spinner spinner;
    private EditText nameEditText, amountEditText;
    private RecyclerView recyclerView;
    private RecyclerViewImagesAdapter imagesAdapter;
    private TextView incomeText, expenseText;
    private SwitchCompat switchCompat;
    private LinearLayout linearRecycler;
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

        prefs = getSharedPreferences("images", MODE_PRIVATE);

        imagePaths.addAll(prefs.getStringSet("paths", new HashSet<>()));

        previewView = findViewById(R.id.previewView);


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

        linearRecycler = findViewById(R.id.linearrecycler);
        linearRecycler.setBackgroundResource(R.drawable.rounded_all_corners_small);

        setupRecyclerView();
    }

    private void setupRecyclerView(){

        buttonAddPhoto = findViewById(R.id.buttonAddPhoto);

        buttonAddPhoto.setOnClickListener(v -> {

                if (ContextCompat.checkSelfPermission(AddSourceActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                } else {
                    ActivityCompat.requestPermissions(AddSourceActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_REQUEST);
                }
        });

        buttonClearImages = findViewById(R.id.buttonClearPhotos);
        buttonClearImages.setOnClickListener(v -> {
            imagePaths.clear();
            prefs.edit().remove("paths").apply();
            imagesAdapter.clearImages();
        });

        recyclerView = findViewById(R.id.photoRecyclerView);

        int numberOfColumns = 2; // or 2, depending on how many columns you want
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        imagesAdapter = new RecyclerViewImagesAdapter(this);
        recyclerView.setAdapter(imagesAdapter);
        loadImages();
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
    private void setUpCameraButtons() {

        buttonCloseCamera = findViewById(R.id.buttonCloseCamera);
        buttonCaptureImage = findViewById(R.id.buttonCaptureImage);
        cameraOverlay = findViewById(R.id.cameraOverlay);

        buttonCloseCamera.setOnClickListener(v -> {
            buttonCloseCamera.setVisibility(View.GONE);
            buttonCaptureImage.setVisibility(View.GONE);
            Toast.makeText(AddSourceActivity.this, "Camera closed", Toast.LENGTH_SHORT).show();
        });

        buttonCaptureImage.setOnClickListener(v -> {
            if (imageCapture != null) {
                imageCapture.takePicture(
                        executor,
                        new ImageCapture.OnImageCapturedCallback() {
                            @Override
                            public void onCaptureSuccess(@NonNull ImageProxy image) {
                                Bitmap bitmap = imageProxyToBitmap(image);
                                String path = saveImageToInternalStorage(bitmap);
                                addImagePath(path);

                                runOnUiThread(() -> {
                                    photoBitMap = bitmap;
                                    imagesAdapter.addImage(photoBitMap);
                                    Toast.makeText(AddSourceActivity.this, "Image captured", Toast.LENGTH_SHORT).show();

                                    buttonCloseCamera.setVisibility(View.GONE);
                                    buttonCaptureImage.setVisibility(View.GONE);
                                    cameraOverlay.setVisibility(View.GONE);

                                    if (cameraProvider != null) {
                                        cameraProvider.unbindAll();
                                    }
                                });
                            }

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


    private String saveImageToInternalStorage(Bitmap bitmap) {
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
        File directory = wrapper.getDir("images", Context.MODE_PRIVATE);
        File file = new File(directory, System.currentTimeMillis() + ".jpg");

        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath(); // Save this path for later
    }
    private void addImagePath(String path) {
        imagePaths.add(path);
        prefs.edit().putStringSet("paths", imagePaths).apply();
    }
    private void loadImages() {
        for (String path : imagePaths) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                imagesAdapter.addImage(bitmap);
            }
        }
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        @SuppressLint("UnsafeOptInUsageError")
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);


        int rotationDegrees = image.getImageInfo().getRotationDegrees();
        if (rotationDegrees != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationDegrees);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
        }

        image.close();
        return bitmap;
    }

    private void startCamera() {

        //View matomumas
        cameraOverlay.setVisibility(View.VISIBLE);
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
    private void saveAllImagesToFolder(int folderId) {
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
        File baseDir = wrapper.getDir("images", Context.MODE_PRIVATE);
        File folder = new File(baseDir, String.valueOf(folderId));

        if (!folder.exists()) folder.mkdir();

        File[] oldFiles = folder.listFiles();
        if (oldFiles != null) {
            for (File f : oldFiles) f.delete();
        }

        List<Bitmap> images = imagesAdapter.getAllImages();

        int idx = 0;
        for (Bitmap bitmap : images) {
            File imgFile = new File(folder, "img_" + idx + ".jpg");
            try (FileOutputStream out = new FileOutputStream(imgFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // logint failo size mb
            long fileSizeBytes = imgFile.length();
            double fileSizeMB = fileSizeBytes / (1024.0 * 1024.0);
            Log.i("ImageSave", "Saved img_" + idx + ".jpg (" + String.format("%.2f", fileSizeMB) + " MB)");

            idx++;
        }
    }
    public List<Bitmap> loadImagesFromFolder(long folderId) {
        List<Bitmap> bitmaps = new ArrayList<>();
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
        File baseDir = wrapper.getDir("images", Context.MODE_PRIVATE);
        File folder = new File(baseDir, String.valueOf(folderId));

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    if (bitmap != null) {
                        bitmaps.add(bitmap);
                        Log.i("IMAGE", file.getName());
                    }
                }
            }
        }
        return bitmaps;
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
        // Prepare entry fields
        Category selectedCategory = (Category) spinner.getSelectedItem();
        int typeId = switchCompat.isChecked() ? 1 : 0;
        String name = nameEditText.getText().toString();
        String amountStr = amountEditText.getText().toString();
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = time.format(formatter);

        if (!name.isEmpty() && isNumber(amountStr)) {
            if (selectedCountryCode == null) selectedCountryCode = "US";
            float amount = Float.parseFloat(amountEditText.getText().toString());
                if(typeId == 1){
                    amount *= -1;
                }
            Entry newEntry = new Entry(
                    name,
                    db.currentAccount,
                    typeId,
                    selectedCategory.getName(),
                    amount,
                    formattedDateTime,
                    selectedCountryCode,
                    -1);

            executor.execute(() -> {
                try {
                    // Insert entry - get generated id
                    long insertedId = db.entryDao().insert(newEntry);

                    // Save images to folder named by insertedId
                    int folderId = (int) insertedId;
                    saveAllImagesToFolder(folderId);

                    // Update photoFolderId in Entry and save
                    newEntry.setId(folderId);
                    newEntry.setPhotoFolderId(folderId);
                    db.entryDao().update(newEntry);

                    runOnUiThread(() -> {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("entry_added", true);
                        setResult(RESULT_OK, resultIntent);

                        loadImagesFromFolder(insertedId);


                        finish();
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(AddSourceActivity.this, "Error saving entry: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });

        } else {
            Toast.makeText(this, "Name and Amount cannot be empty or invalid", Toast.LENGTH_SHORT).show();
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
