package com.example.finanseapp.Helpers;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finanseapp.AppDatabase;
import com.example.finanseapp.Entities.Category;
import com.example.finanseapp.Entities.Entry;
import com.example.finanseapp.MainActivity;
import com.example.finanseapp.R;

import java.util.List;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class DialogHelper {
    private int entryId = -1;
    public String entryCountryCode;
    public String entryDate;
    public String entryCategory;
    private final Dialog editSourceDialog;
    public final EditText sourceName, sourceAmount;
    public final Button cancelButton, saveButton;
    public final Spinner categorySpinner;
    public final SwitchCompat switchCompat;
    public RecyclerView recyclerView;
    private final TextView incomeLabel, expenseLabel;
    public int adapterPositionId;
    private final SharedPreferences prefs;
    private final Set<String> imagePaths = new HashSet<>();
    private RecyclerViewImagesAdapter imagesAdapter;
    private final Context context;

    public Runnable onBalanceUpdate;

    AppDatabase db;


    public DialogHelper(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("images", Context.MODE_PRIVATE);
        db = AppDatabase.getInstance(context.getApplicationContext());

        editSourceDialog = new Dialog(context);
        editSourceDialog.setContentView(R.layout.dialog_edit_source);
        editSourceDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_all_corners_small_nontrans);
 
        sourceName = editSourceDialog.findViewById(R.id.editTextNameDialog);
        sourceAmount = editSourceDialog.findViewById(R.id.editTextAmountDialog);

        categorySpinner = editSourceDialog.findViewById(R.id.spinnerCategoryDialog);
        switchCompat = editSourceDialog.findViewById(R.id.customSwitchDialog);

        incomeLabel = editSourceDialog.findViewById(R.id.textViewIncomeDialog);
        expenseLabel = editSourceDialog.findViewById(R.id.textViewExpenseDialog);

        cancelButton = editSourceDialog.findViewById(R.id.buttonCancelDialog);
        saveButton = editSourceDialog.findViewById(R.id.buttonUpdateDialog);

        recyclerView = editSourceDialog.findViewById(R.id.photoRecyclerView);
        LinearLayout linearRecycler = editSourceDialog.findViewById(R.id.linearrecycler);
        linearRecycler.setBackgroundResource(R.drawable.rounded_all_corners_small);

        setupRecyclerView(context);
        configureButtons();
        configureSpinner(context);
    }

    private void configureSpinner(Context context) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Category> categories = db.categoryDao().getAllCategories();
            ArrayAdapter<Category> spinnerAdapter = new ArrayAdapter<>(context,
                    R.layout.spinner_dropdown_main, categories);
            spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
            categorySpinner.setAdapter(spinnerAdapter);

            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    entryCategory = parent.getItemAtPosition(position).toString();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        });
    }

    public void setValues(AppDatabase db, int id, int adapterId) {
        adapterPositionId = adapterId;
        
        Executors.newSingleThreadExecutor().execute(() -> {
            Entry entry = db.entryDao().getEntryById(id);
            List<Category> categories = db.categoryDao().getAllCategories();
            if(entry != null) {
                long folderId = entry.getPhotoFolderId();

                new Handler(Looper.getMainLooper()).post(() -> {
                    entryId = id;
                    entryCountryCode = entry.getCountryCode();
                    entryDate = entryDate;
                    Log.i("", "ID INDETAS BLET:" + entryId);
                    sourceName.setText(entry.getName());
                    sourceAmount.setText(Float.toString(entry.getAmount()));
                    switchCompat.setChecked(entry.getType() != 0);
                    configureSwitch();

                    for (int i = 0; i < categories.size(); i++) {
                        if (categories.get(i).getName().equals(entry.getCategory())) {
                            categorySpinner.setSelection(i);
                            break;
                        }
                    }

                    loadImagesFromFolder(folderId);
                });

                Log.i("IMAGE", "PHOTO FOLDER ID: " + Integer.toString(entry.getPhotoFolderId()));
            }
            else
                Log.i("DIALOG HELPER", "entry is null in setValue");
        });
    }

    public void toggleDialog(boolean state) {
        if (state) {
            editSourceDialog.show();
            animateDialogIn();
        } else {
            editSourceDialog.hide();
        }
    }
    private void setupRecyclerView(Context context){

        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));
        imagesAdapter = new RecyclerViewImagesAdapter(context);
        recyclerView.setAdapter(imagesAdapter);
    }
    private void configureSwitch() {
        updateLabelColors(switchCompat.isChecked());

        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateLabelColors(isChecked);
        });
    }
    public void loadImagesFromFolder(long folderId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Bitmap> bitmaps = new ArrayList<>();
            ContextWrapper wrapper = new ContextWrapper(context);
            File baseDir = wrapper.getDir("images", Context.MODE_PRIVATE);
            File folder = new File(baseDir, String.valueOf(folderId));

            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        if (bitmap != null) {
                            bitmaps.add(bitmap);
                        }
                    }
                }
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                imagesAdapter.clearImages();
                for (Bitmap bitmap : bitmaps) {
                    imagesAdapter.addImage(bitmap);
                }
            });
        });
    }
    private void updateLabelColors(boolean isExpense) {
        int incomeColor = isExpense ? Color.BLACK : Color.GREEN;
        int expenseColor = isExpense ? Color.RED : Color.BLACK;
        incomeLabel.setTextColor(incomeColor);
        expenseLabel.setTextColor(expenseColor);
    }

    public int ReturnSwitchStateInt() {
        return switchCompat.isChecked() ? 1 : 0;
    }

    private void configureButtons() {
        cancelButton.setOnClickListener(v -> toggleDialog(false));
        //saveButton.setOnClickListener(v -> updateEntry());
    }

    public void updateEntry() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Entry entry = db.entryDao().getEntryById(entryId);
            if(entry != null) {

                entry.setName(sourceName.getText().toString());
                entry.setAccountId(entry.getAccountId());
                entry.setType(ReturnSwitchStateInt());
                entry.setAmount(Float.parseFloat(sourceAmount.getText().toString()));
                entry.setDate(entry.getDate());
                entry.setCountryCode(entry.getCountryCode());
                entry.setCategory(entryCategory);

                db.entryDao().update(entry);

                onBalanceUpdate.run();

                Log.i("DUOMBAZ",  entry.getName() + " " + entry.getName() + " " + entry.getAmount());
                Log.i("DUOMBAZ", "SUVEIKE");
                Log.i("DUOMBAZ", "SUVEIKE");
                Log.i("DUOMBAZ", "SUVEIKE");
                Log.i("DUOMBAZ", "SUVEIKE");
            }
            else
                Log.i("DIALOG HELPER", "entry is null in setValue");
        });
    }

    private void animateDialogIn() {
        FrameLayout layout = editSourceDialog.findViewById(R.id.dialogOuterContainer);
        if (layout == null) return;

        layout.setTranslationY(-1000f);
        layout.setAlpha(0f);

        ObjectAnimator slideDown = ObjectAnimator.ofFloat(layout, "translationY", -1000f, 0f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(layout, "alpha", 0f, 1f);

        slideDown.setDuration(1000);
        fadeIn.setDuration(500);
        slideDown.setInterpolator(new DecelerateInterpolator());
        fadeIn.setInterpolator(new DecelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(slideDown, fadeIn);
        animatorSet.start();
    }

}
