package com.example.finanseapp.Helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.example.finanseapp.AppDatabase;
import com.example.finanseapp.Entities.Entry;
import com.example.finanseapp.R;

import java.util.concurrent.Executors;

public class DialogHelper{
    Dialog editSourceDialog;
    EditText sourceName, sourceAmount;
    public Button cancelButton, saveButton;
    TextView incomeLabel, expenseLabel;
    Spinner categorySpinner;
    SwitchCompat switchCompat;

    public DialogHelper(Context context){

        editSourceDialog = new Dialog(context);
        editSourceDialog.setContentView(R.layout.dialog_edit_source);

        sourceName = editSourceDialog.findViewById(R.id.editTextNameDialog);
        sourceAmount = editSourceDialog.findViewById(R.id.editTextAmountDialog);
        categorySpinner = editSourceDialog.findViewById(R.id.spinnerCategoryDialog);
        switchCompat = editSourceDialog.findViewById(R.id.customSwitchDialog);
        incomeLabel = editSourceDialog.findViewById(R.id.textViewIncomeDialog);
        expenseLabel = editSourceDialog.findViewById(R.id.textViewExpenseDialog);
        cancelButton = editSourceDialog.findViewById(R.id.buttonCancelDialog);
        saveButton = editSourceDialog.findViewById(R.id.buttonUpdateDialog);

        configureButtons();

        editSourceDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_all_corners_small_nontrans);

    }
    public void setValues(AppDatabase db, int id){

        // pagetint ir nusetint values

        Executors.newSingleThreadExecutor().execute(()->{
            Entry entry = db.entryDao().getEntryById(id);

            new android.os.Handler(Looper.getMainLooper()).post(() -> {
                sourceName.setText(entry.getName());
                sourceAmount.setText(Float.toString(entry.getAmount()));

                int switchState = entry.getType();
                switchCompat.setChecked(switchState != 0);

                configureSwitch();
            });
        });
    }
    public void toggleDialog(boolean state){

        if(state)
            editSourceDialog.show();
        else
            editSourceDialog.hide();
    }
    void configureSwitch(){
        boolean state = switchCompat.isChecked();

        //expense
        if (state) {
            ChangeTextColors(Color.BLACK, Color.RED);
        }
        //income
        else {
            ChangeTextColors(Color.GREEN, Color.BLACK);
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ChangeTextColors(Color.BLACK, Color.RED);
                    //switchCompat.setTrackTintList(ColorStateList.valueOf(getColor(R.color.red)));
                }
                //income
                else {
                    ChangeTextColors(Color.GREEN, Color.BLACK);
                    //switchCompat.setTrackTintList(ColorStateList.valueOf(getColor(R.color.green_005)));
                }
            }
        });
    }
    void ChangeTextColors(int incomeColor, int expenseColor) {
        incomeLabel.setTextColor(incomeColor);
        expenseLabel.setTextColor(expenseColor);
    }
    void configureButtons(){

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ikelt i db
                toggleDialog(false);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDialog(false);
            }
        });
    }
}

