package com.example.finanseapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.example.finanseapp.Entities.Category;
import com.example.finanseapp.helpers.RecyclerViewAdapter;

import com.example.finanseapp.Entities.Account;
import com.example.finanseapp.Entities.Entry;
import com.example.finanseapp.Entities.User;

import java.util.List;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    AppDatabase db;
    Button buttonIncome, buttonAddAccount, buttonAddCategory;
    TextView textViewBalance;
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    ActionBar actionBar;
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
        //printData(db);


        //-----TOP ACTION BAR
        actionBar = getSupportActionBar();
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.topbar_box));

        }

        // --------INCOME BUTTON
        buttonIncome = findViewById(R.id.incomeButton);
        SetButtonOnClickToActivity(buttonIncome, AddSourceActivity.class);

        // --------ADD ACCOUNT BUTTON
        //buttonAddAccount = findViewById(R.id.button2); //pakeist button2 i kita kai idesiu
        //SetButtonOnClickToActivity(buttonAddAccount, AddAccountActivity.class);

        // --------ADD CATEGORY BUTTON
        buttonAddCategory = findViewById(R.id.addCategoryButton);
        SetButtonOnClickToActivity(buttonAddCategory, AddCategoryActivity.class);

        //---------ACCOUNT BALANCE TEXT
        textViewBalance = findViewById(R.id.textViewBalance);

        Executors.newSingleThreadExecutor().execute(() -> {
            textViewBalance.setText(Float.toString(db.entryDao().getTotalAmountByAccount((Integer.toString(db.currentAccount)))));
        });


        //--------RECYCLER VIEW
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setBackgroundResource(R.drawable.rounded_top_corners);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Entry> entries = db.entryDao().getEntriesByAccountId("1");

            runOnUiThread(() -> {
                adapter = new RecyclerViewAdapter(entries);
                recyclerView.setAdapter(adapter);
            });

            Log.i("NUM", "TEST LOGGGGGGGGGGGGGGGGGGGGGGGGGG");
        });

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.Callback() {
                    @Override
                    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                        //return 0;
                        return makeMovementFlags(
                                0,
                                ItemTouchHelper.END
                        );
                    }

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        //refreshRecyclerView();
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        switch (direction) {
                            case ItemTouchHelper.END:

                                    int pos = viewHolder.getAdapterPosition();
                                    RecyclerViewAdapter.ViewHolder newHolder = (RecyclerViewAdapter.ViewHolder)viewHolder;



                                    runOnUiThread(() -> {

                                        adapter.removeItem(pos);
                                        refreshRecyclerView();

                                    });

                                    newHolder.delete();
                                    //refreshRecyclerView();
                                break;
                        }
                    }
                }
        );

        helper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        printData(db);

        //---------ACCOUNT BALANCE TEXT
        textViewBalance = findViewById(R.id.textViewBalance);

        Executors.newSingleThreadExecutor().execute(() -> {
            textViewBalance.setText(Float.toString(db.entryDao().getTotalAmountByAccount((Integer.toString(db.currentAccount)))));
        });


        //--------RECYCLER VIEW
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setBackgroundResource(R.drawable.rounded_top_corners);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Executors.newSingleThreadExecutor().execute(() -> {
            recyclerView.setAdapter(new RecyclerViewAdapter(db.entryDao().getEntriesByAccountId("1")));
            Log.i("NUM", "TEST LOGGGGGGGGGGGGGGGGGGGGGGGGGG");
        });
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

            if (db.categoryDao().getCategoryByName("Other") == null &&
                    db.categoryDao().getCategoryByName("Other ") == null) {
                db.categoryDao().insert(new Category("Other ", 0));
                db.categoryDao().insert(new Category("Other", 1));
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

    public void refreshRecyclerView(){

        //---------ACCOUNT BALANCE TEXT
        textViewBalance = findViewById(R.id.textViewBalance);

        Executors.newSingleThreadExecutor().execute(() -> {
            textViewBalance.setText(Float.toString(db.entryDao().getTotalAmountByAccount((Integer.toString(db.currentAccount)))));
        });


        //--------RECYCLER VIEW
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setBackgroundResource(R.drawable.rounded_top_corners);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Executors.newSingleThreadExecutor().execute(() -> {
            recyclerView.setAdapter(new RecyclerViewAdapter(db.entryDao().getEntriesByAccountId("1")));
            //Log.i("NUM", "TEST LOG");
        });


    }
}