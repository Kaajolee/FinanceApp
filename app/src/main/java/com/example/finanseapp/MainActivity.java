package com.example.finanseapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finanseapp.Entities.Account;
import com.example.finanseapp.Entities.Category;
import com.example.finanseapp.Entities.Entry;
import com.example.finanseapp.Entities.User;
import com.example.finanseapp.Helpers.DollarSignAnimation;
import com.example.finanseapp.Helpers.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    AppDatabase db;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    List<Entry> entries;
    Button buttonAddAccount, buttonCharts;
    ImageButton imgButtonIncome, imgbuttonAddCategory;
    TextView textViewBalance;
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    ActionBar actionBar;
    DollarSignAnimation dollarAnimator;
    int dollarGreenID, dollarRedID;
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
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.topbar_box));

        }

        // --------INCOME BUTTON
        imgButtonIncome = findViewById(R.id.imageButton);
        SetButtonOnClickToActivity(imgButtonIncome, AddSourceActivity.class);

        // --------ADD ACCOUNT BUTTON
        //buttonAddAccount = findViewById(R.id.button2); //pakeist button2 i kita kai idesiu
        //SetButtonOnClickToActivity(buttonAddAccount, AddAccountActivity.class);

        // --------ADD CATEGORY BUTTON
        imgbuttonAddCategory = findViewById(R.id.imageButton3);
        SetButtonOnClickToActivity(imgbuttonAddCategory, AddCategoryActivity.class);

        // --------CHARTS BUTTON
        buttonCharts = findViewById(R.id.button);
        SetButtonOnClickToActivity(buttonCharts, GraphsActivity.class);

        //---------ACCOUNT BALANCE TEXT
        textViewBalance = findViewById(R.id.textViewBalance);

        executor.execute(() -> {
            textViewBalance.setText(Float.toString(db.entryDao().getTotalAmountByAccount((Integer.toString(db.currentAccount)))));
        });


        //--------RECYCLER VIEW
        UpdateRecyclerView();

        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

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
                                RecyclerViewAdapter.ViewHolder newHolder = (RecyclerViewAdapter.ViewHolder) viewHolder;

                                newHolder.delete();

                                executor.execute(() -> {
                                    textViewBalance.setText(Float.toString(db.entryDao().getTotalAmountByAccount((Integer.toString(db.currentAccount)))) +"€");
                                });

                                runOnUiThread(() -> {

                                    adapter.removeItem(newHolder.getLayoutPosition());
                                    //adapter.notifyDataSetChanged();
                                    //refreshRecyclerView();


                                });


                                //refreshRecyclerView();
                                break;
                        }
                    }
                }
        );

        helper.attachToRecyclerView(recyclerView);

        //------DOLLAR ANIMATOR
        dollarGreenID = R.drawable.dollarsigngreen;
        dollarRedID = R.drawable.dollarsignred;

        dollarAnimator = findViewById(R.id.dollaranimator);

        boolean isPositive = false;
        int spriteAmount = 10;

        if (isPositive)
            dollarAnimator.setDollarImageId(dollarGreenID, spriteAmount);
        else
            dollarAnimator.setDollarImageId(dollarRedID, spriteAmount);



       /* Executors.newSingleThreadExecutor().execute(() -> {

            int currentMoneyAmount;

            currentMoneyAmount = (int) db.entryDao().getTotalAmountByAccount((Integer.toString(db.currentAccount)));



        });*/



    }

    @Override
    protected void onStart() {
        super.onStart();
        printData(db);

        //---------ACCOUNT BALANCE TEXT
        textViewBalance = findViewById(R.id.textViewBalance);

        executor.execute(() -> {
            textViewBalance.setText(Float.toString(db.entryDao().getTotalAmountByAccount((Integer.toString(db.currentAccount))))+"€");
        });


        //--------RECYCLER VIEW
        UpdateRecyclerView();
    }

    private void SetButtonOnClickToActivity(View view, Class<? extends AppCompatActivity> destination) {

        if (view != null) {

            Intent intent = new Intent(getApplicationContext(), destination);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intent);
                }
            });

        } else {
            Log.e("BUTTON", "View reference is null");
        }
    }


    void generateData(AppDatabase db) {
        executor.execute(() -> {
            //db.entryDao().deleteAll();
            //  db.accountDao().deleteAll();
            //db.userDao().deleteAll();

            //db.clearAllTables();


            if (db.userDao().getUserByUsername("admin") == null) {
                db.userDao().insert(new User("admin", "root"));
            }

            if (db.accountDao().getAccountByName("saskaita1") == null) {
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
        executor.execute(() -> {
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

    private void UpdateRecyclerView() {
        //--------RECYCLER VIEW
        recyclerView = findViewById(R.id.recyclerview);
        //recyclerView.setBackgroundResource(R.drawable.rounded_top_corners);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        executor.execute(() -> {

            entries = new ArrayList<>();
            entries = db.entryDao().getEntriesByAccountId("1");

            runOnUiThread(() -> {
                adapter = new RecyclerViewAdapter(entries);
                recyclerView.setAdapter(adapter);
            });

            Log.i("NUM", "TEST LOGGGGGGGGGGGGGGGGGGGGGGGGGG");
        });
    }
}