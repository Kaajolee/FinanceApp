package com.example.finanseapp;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
import androidx.core.app.ActivityCompat;
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
import com.example.finanseapp.Helpers.ShakingDetector;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<Entry> entries;

    private ShakingDetector shakeDetector;
    private ImageButton buttonCharts;
    private Paint paint;
    private ImageButton imgButtonIncome, imgbuttonAddCategory;
    private TextView textViewBalance;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private ActionBar actionBar;
    private DollarSignAnimation dollarAnimator;
    private RelativeLayout relativeLayout;

    private ActivityResultLauncher<Intent> resultLauncher;

    private int dollarGreenID, dollarRedID;

    public static String countryCodeGlobal = "LT";

    private void getCountryFromLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        countryCodeGlobal = addresses.get(0).getCountryCode();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override public void onProviderEnabled(String provider) {}
            @Override public void onProviderDisabled(String provider) {}
        }, null);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(getApplicationContext());

        getCountryFromLocation();

        initializeUI();
        //initializeHardware();
        initializeData();
        setUpRecyclerView();
        setUpDollarSignAnimation();
        setUpBalanceWiggle();
        setUpActivityResults();



    }


    @Override
    protected void onStart() {
        super.onStart();
        updateBalanceText();
        setUpRecyclerView();
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
                            entryAddedDialog();

                            //Toast.makeText(this, "na!", Toast.LENGTH_SHORT).show();

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
        imgButtonIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateDollar(v);
            }
        });
        //setButtonOnClickToActivityResult(imgButtonIncome, AddSourceActivity.class);

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

    private void initializeHardware(){
        shakeDetector = new ShakingDetector(this);
        shakeDetector.hearShake();
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        executor.execute(() -> {
            entries = db.entryDao().getEntriesByAccountId("1");

            runOnUiThread(() -> {
                DialogHelper editSourceDialogHelper = new DialogHelper(this);
                adapter = new RecyclerViewAdapter(entries, editSourceDialogHelper, MainActivity.countryCodeGlobal);
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


                    runOnUiThread(() -> adapter.removeItem(newHolder.getLayoutPosition()));
                    updateBalanceText();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;

                // load
                Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(),
                        R.drawable.baseline_restore_from_trash_24);


                Paint paint = new Paint();
                paint.setColor(Color.RED);


                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(),
                        itemView.getLeft() + dX, (float) itemView.getBottom(), paint);


                if (icon != null) {

                    icon.setTint(Color.WHITE);

                    // bounds
                    int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconLeft = itemView.getLeft() + iconMargin;

                    // alpha
                    float alpha = Math.min(Math.abs(dX) / (float) itemView.getWidth(), 1.0f);
                    icon.setAlpha((int) (alpha * 255));

                    // scale
                    float scale = Math.min(Math.abs(dX) / (float) itemView.getWidth(), 1.5f) + 0.5f;
                    int scaledWidth = (int) (icon.getIntrinsicWidth() * scale);
                    int scaledHeight = (int) (icon.getIntrinsicHeight() * scale);

                    // bounds
                    int scaledIconLeft = iconLeft;
                    int scaledIconTop = iconTop;
                    int scaledIconRight = scaledIconLeft + scaledWidth;
                    int scaledIconBottom = scaledIconTop + scaledHeight;

                    icon.setBounds(scaledIconLeft, scaledIconTop, scaledIconRight, scaledIconBottom);
                    icon.draw(c);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }});

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

    private boolean wiggleDirection = true;

    private void setUpBalanceWiggle() {
        textViewBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float start = wiggleDirection ? -5f : 5f;
                float end = wiggleDirection ? 5f : -5f;

                ObjectAnimator wiggle = ObjectAnimator.ofFloat(textViewBalance, "rotation", start, end, 0f);
                wiggle.setInterpolator(new AccelerateDecelerateInterpolator());
                wiggle.setDuration(500);
                wiggle.start();

                wiggleDirection = !wiggleDirection;
            }
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
                updateBalanceText();
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
    void animateDollar(View view){

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;

        ObjectAnimator rotate = new ObjectAnimator();
        rotate = ObjectAnimator.ofFloat(view, "rotationY", 720f);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());
        rotate.setDuration(400);


        ObjectAnimator translateX = new ObjectAnimator();
        translateX = ObjectAnimator.ofFloat(view, "translationX", screenWidth + 500);
        translateX.setInterpolator(new AccelerateDecelerateInterpolator());
        translateX.setDuration(500);


        AnimatorSet set = new AnimatorSet();

        set.playSequentially(rotate, translateX);

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {

                Intent intent = new Intent(getApplicationContext(), AddSourceActivity.class);
                startActivity(intent);

            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });

        set.start();
    }
}