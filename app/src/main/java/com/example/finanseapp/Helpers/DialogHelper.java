package com.example.finanseapp.Helpers;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
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

import com.example.finanseapp.AppDatabase;
import com.example.finanseapp.Entities.Category;
import com.example.finanseapp.Entities.Entry;
import com.example.finanseapp.MainActivity;
import com.example.finanseapp.R;

import java.util.List;
import java.util.concurrent.Executors;

public class DialogHelper {
    private int entryId = -1;
    public String entryCountryCode;
    public String entryCategory;
    public long entryDate;
    private final Dialog editSourceDialog;
    public final EditText sourceName, sourceAmount;
    public final Button cancelButton, saveButton;
    public final Spinner categorySpinner;
    public final SwitchCompat switchCompat;
    private final TextView incomeLabel, expenseLabel;
    public int adapterPositionId;

    public Runnable onBalanceUpdate;

    AppDatabase db;


    public DialogHelper(Context context) {
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

                });
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

    private void configureSwitch() {
        updateLabelColors(switchCompat.isChecked());

        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateLabelColors(isChecked);
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
