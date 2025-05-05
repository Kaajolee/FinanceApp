package com.example.finanseapp.Helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.example.finanseapp.AppDatabase;
import com.example.finanseapp.Entities.Entry;
import com.example.finanseapp.R;

import java.util.concurrent.Executors;

public class DialogHelper {

    private final Dialog editSourceDialog;
    public final EditText sourceName, sourceAmount;
    public final Button cancelButton, saveButton;
    public final Spinner categorySpinner;
    public final SwitchCompat switchCompat;
    private final TextView incomeLabel, expenseLabel;

    public int adapterPositionId;

    public DialogHelper(Context context) {
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
    }

    public void setValues(AppDatabase db, int id, int adapterId) {
        adapterPositionId = adapterId;
        
        Executors.newSingleThreadExecutor().execute(() -> {
            Entry entry = db.entryDao().getEntryById(id);
            if(entry != null) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    sourceName.setText(entry.getName());
                    sourceAmount.setText(Float.toString(entry.getAmount()));
                    switchCompat.setChecked(entry.getType() != 0);
                    configureSwitch();
                });
            }
            else
                Log.i("DIALOG HELPER", "entry is null in setValue");
        });
    }

    public void toggleDialog(boolean state) {
        if (state) {
            editSourceDialog.show();
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

    }
}
