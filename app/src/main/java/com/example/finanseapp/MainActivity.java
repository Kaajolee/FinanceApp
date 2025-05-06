package com.example.finanseapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.finanseapp.Entities.Account;
import com.example.finanseapp.Entities.Category;
import com.example.finanseapp.Entities.Entry;
import com.example.finanseapp.Entities.User;
import com.example.finanseapp.Helpers.DialogHelper;
import com.example.finanseapp.Helpers.DollarSignAnimation;
import com.example.finanseapp.Helpers.RecyclerViewAdapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<Entry> entries;

    private Button buttonCharts;
    private ImageButton imgButtonIncome, imgbuttonAddCategory;
    private TextView textViewBalance;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private ActionBar actionBar;
    private DollarSignAnimation dollarAnimator;
    private RelativeLayout relativeLayout;

    private ActivityResultLauncher<Intent> resultLauncher;

    private int dollarGreenID, dollarRedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(getApplicationContext());

        initializeUI();
        initializeData();
        setUpRecyclerView();
        setUpDollarSignAnimation();
        setUpActivityResults();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateBalanceText();
        updateRecyclerView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setUpActivityResults() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getBooleanExtra("entry_added", false)) {
                            entryAddedDialog(); // Show your animated dialog here
                            //Toast.makeText(this, "niqqa!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void initializeUI() {

        relativeLayout = findViewById(R.id.splashOverlay);
        actionBar = getSupportActionBar();
        actionBar.hide();
        new Handler().postDelayed(() -> {

            AlphaAnimation alphaAnim = new AlphaAnimation(1, 0);
            alphaAnim.setDuration(500);
            alphaAnim.setFillAfter(true);
            relativeLayout.startAnimation(alphaAnim);
            relativeLayout.setVisibility(View.GONE);
            actionBar.show();
        }, 2000);



        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.topbar_box));
        }


        imgButtonIncome = findViewById(R.id.imageButton);
        setButtonOnClickToActivityResult(imgButtonIncome, AddSourceActivity.class);

        imgbuttonAddCategory = findViewById(R.id.imageButton3);
        setButtonOnClickToActivity(imgbuttonAddCategory, AddCategoryActivity.class);

        buttonCharts = findViewById(R.id.button);
        setButtonOnClickToActivity(buttonCharts, GraphsActivity.class);

        textViewBalance = findViewById(R.id.textViewBalance);
    }

    private void initializeData() {
        executor.execute(() -> {
            generateData(db);
            updateBalanceText();
        });
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        executor.execute(() -> {
            entries = db.entryDao().getEntriesByAccountId("1");

            runOnUiThread(() -> {
                DialogHelper editSourceDialogHelper = new DialogHelper(this);
                adapter = new RecyclerViewAdapter(entries, editSourceDialogHelper);
                recyclerView.setAdapter(adapter);
            });
        });

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.END) {
                    int pos = viewHolder.getAdapterPosition();
                    RecyclerViewAdapter.ViewHolder newHolder = (RecyclerViewAdapter.ViewHolder) viewHolder;

                    newHolder.delete();
                    updateBalanceText();

                    runOnUiThread(() -> adapter.removeItem(newHolder.getLayoutPosition()));
                }
            }
        });

        helper.attachToRecyclerView(recyclerView);
    }

    private void setUpDollarSignAnimation() {
        dollarGreenID = R.drawable.dollarsigngreen;
        dollarRedID = R.drawable.dollarsignred;
        dollarAnimator = findViewById(R.id.dollaranimator);

        executor.execute(() -> {
            float moneyAmount = db.entryDao().getTotalAmountByAccount(Integer.toString(db.currentAccount));

            int spriteAmount = 1;
            int dollarImageID = (moneyAmount >= 0) ? dollarGreenID : dollarRedID;
            dollarAnimator.setDollarImageId(dollarImageID, spriteAmount);

            dollarAnimator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dollarAnimator.addDollar();
                }
            });
        });
    }

    private void setButtonOnClickToActivity(View view, Class<? extends AppCompatActivity> destination) {
        if (view != null) {
            Intent intent = new Intent(getApplicationContext(), destination);
            view.setOnClickListener(v -> startActivity(intent));
        } else {
            Log.e("BUTTON", "View reference is null");
        }
    }

    private void setButtonOnClickToActivityResult(View view, Class<? extends AppCompatActivity> destination) {
        if (view != null) {
            view.setOnClickListener(v ->  {
                Intent intent = new Intent(getApplicationContext(), destination);
                resultLauncher.launch(intent);
            });
        } else {
            Log.e("BUTTON", "View reference is null");
        }
    }

    private void generateData(AppDatabase db) {
        executor.execute(() -> {
            if (db.userDao().getUserByUsername("admin") == null) {
                db.userDao().insert(new User("admin", "root"));
            }

            if (db.accountDao().getAccountByName("saskaita1") == null) {
                db.accountDao().insert(new Account("saskaita1", db.userDao().getUserByUsername("admin").getId(), 20));
            }

            if (db.categoryDao().getCategoryByName("Other") == null) {
                db.categoryDao().insert(new Category("Other", 1));
                db.categoryDao().insert(new Category("Other ", 0));
            }
        });
    }

    private void updateBalanceText() {
        executor.execute(() -> {
            String balanceText = Float.toString(db.entryDao().getTotalAmountByAccount(Integer.toString(db.currentAccount))) + "€";
            runOnUiThread(() -> textViewBalance.setText(balanceText));
        });
    }

    private void updateRecyclerView() {
        executor.execute(() -> {
            entries = db.entryDao().getEntriesByAccountId("1");

            runOnUiThread(() -> {
                adapter.updateData(entries);
            });
        });
    }

    void entryAddedDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.entry_added);
        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_all_corners_small_nontrans);
        dialog.getWindow().setDimAmount(0);

        ImageView newImageView = (ImageView) dialog.findViewById(R.id.imageView);

        Drawable drawable = newImageView.getDrawable();
        if (drawable instanceof AnimatedVectorDrawable) {
            AnimatedVectorDrawable avd = (AnimatedVectorDrawable) drawable;
            avd.start();
        } else if (drawable instanceof AnimatedVectorDrawableCompat) {
            AnimatedVectorDrawableCompat avdc = (AnimatedVectorDrawableCompat) drawable;
            avdc.start();
        }


        dialog.setCancelable(false);

        dialog.show();

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss(); // Dismiss the dialog
            }
        }, 2000);
    }
}