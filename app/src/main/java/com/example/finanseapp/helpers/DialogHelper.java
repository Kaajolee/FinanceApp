package com.example.finanseapp.Helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.example.finanseapp.AppDatabase;
import com.example.finanseapp.R;

public class DialogHelper{
    Dialog editSourceDialog;
    EditText sourceName, sourceAmount;
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

        editSourceDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_all_corners_small_nontrans);

    }
    public void setValues(int id){

        // pagetint ir nusetint values

        sourceName.setText("Source name test text");
        sourceAmount.setText("987452");
        switchCompat.setChecked(false); // nusetint is db ar expense ar income
        configureSwitch();
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
}

