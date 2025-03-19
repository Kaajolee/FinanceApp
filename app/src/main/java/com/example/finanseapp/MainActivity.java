package com.example.finanseapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finanseapp.Helpers.RecyclerViewAdapter;

public class MainActivity extends AppCompatActivity {
    Button buttonIncome, buttonExpenses, buttonAddAccount, buttonAddCategory;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/


        // --------INCOME BUTTON
        buttonIncome = findViewById(R.id.incomeButton);
        SetButtonOnClickToActivity(buttonIncome, IncomeActivity.class);

        // --------EXPENSES BUTTON
        buttonExpenses = findViewById(R.id.expensesButton);
        SetButtonOnClickToActivity(buttonExpenses, ExpensesActivity.class);

        // --------ADD ACCOUNT BUTTON
        //buttonAddAccount = findViewById(R.id.button2); //pakeist button2 i kita kai idesiu
        SetButtonOnClickToActivity(buttonAddAccount, AddAccountActivity.class);

        // --------ADD CATEGORY BUTTON
        buttonAddCategory = findViewById(R.id.addCategoryButton);
        SetButtonOnClickToActivity(buttonAddCategory, AddCategoryActivity.class);

        //--------RECYCLER VIEW
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(new RecyclerViewAdapter(cia data));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }


    private void SetButtonOnClickToActivity(Button button, Class<? extends AppCompatActivity> destination){

        if(button != null){

            Intent intent = new Intent(getApplicationContext(), destination);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intent);
                }
            });

        }
        else{
            Log.e("BUTTON", "Button reference is null");
        }
    }
}