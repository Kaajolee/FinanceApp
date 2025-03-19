package com.example.finanseapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.example.finanseapp.Helpers.RecyclerViewAdapter;

import com.example.finanseapp.Entities.Account;
import com.example.finanseapp.Entities.Entry;
import com.example.finanseapp.Entities.User;
import com.example.finanseapp.Helpers.RecyclerViewAdapter;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    AppDatabase db;
    Button buttonIncome, buttonExpenses, buttonAddAccount, buttonAddCategory;
    TextView textViewBalance;
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

        db = AppDatabase.getInstance(getApplicationContext());

        generateData(db);
        printData(db);


        // --------INCOME BUTTON
        buttonIncome = findViewById(R.id.incomeButton);
        SetButtonOnClickToActivity(buttonIncome, IncomeActivity.class);

        // --------EXPENSES BUTTON
        buttonExpenses = findViewById(R.id.expensesButton);
        SetButtonOnClickToActivity(buttonExpenses, ExpensesActivity.class);

        // --------ADD ACCOUNT BUTTON
        //buttonAddAccount = findViewById(R.id.button2); //pakeist button2 i kita kai idesiu
        //SetButtonOnClickToActivity(buttonAddAccount, AddAccountActivity.class);

        // --------ADD CATEGORY BUTTON
        buttonAddCategory = findViewById(R.id.addCategoryButton);
        SetButtonOnClickToActivity(buttonAddCategory, AddCategoryActivity.class);

        //---------ACCOUNT BALANCE TEXT
        textViewBalance = findViewById(R.id.textViewBalance);
        //textViewBalance.setText((char) db.entryDao().getTotalAmountByAccount("0"));

        //--------RECYCLER VIEW
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setBackgroundResource(R.drawable.rounded_top_corners);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Executors.newSingleThreadExecutor().execute(() -> {
            recyclerView.setAdapter(new RecyclerViewAdapter(db.entryDao().getEntriesByAccountId("0")));
        });

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onStart() {
        super.onStart();
        printData(db);
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

    void generateData(AppDatabase db) {
        Executors.newSingleThreadExecutor().execute(() -> {
            //db.entryDao().deleteAll();
           //  db.accountDao().deleteAll();
            //db.userDao().deleteAll();

            //db.clearAllTables();


            if (db.userDao().getUserByUsername("admin") == null)
            {
                db.userDao().insert(new User("admin","root"));
            }

            if (db.accountDao().getAccountByName("saskaita1") == null)
            {
                db.accountDao().insert(new Account("saskaita1", db.userDao().getUserByUsername("admin").getId(), 20));
            }


           // Random random = new Random();
           // db.entryDao().insert(new Entry("skauda", db.accountDao().getAccountByName("saskaita1").getId(), 0, random.nextInt(100), 2025));


        });
    }

    void printData(AppDatabase db) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Print users
            List<User> users = db.userDao().getAllUsers();
            System.out.println("Users:");
            for (User user : users) {
                System.out.println("  ID: " + user.getId() + ", Username: " + user.getUsername());
            }

            // Print accounts
            List<Account> accounts = db.accountDao().getAllAccounts();
            System.out.println("\nAccounts:");
            for (Account account : accounts) {
                System.out.println("  ID: " + account.getId() + ", Name: " + account.getName() + ", User ID: " + account.getUserId() + ", Balance: " + account.getBalance());
            }

            // Print entries
            List<Entry> entries = db.entryDao().getAllEntries();
            System.out.println("\nEntries:");
            for (Entry entry : entries) {
                System.out.println("  Name: " + entry.getName() + "ID: " + entry.getId() + ", Account ID: " + entry.getAccountId() + ", Type: " + entry.getType() + ", Amount: " + entry.getAmount() + ", Year: " + entry.getDate());
            }
        });
    }
}