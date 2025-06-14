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
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.example.finanseapp.Helpers.BlurOnFlipManager;
import com.example.finanseapp.Helpers.DialogHelper;
import com.example.finanseapp.Helpers.DollarSignAnimation;
import com.example.finanseapp.Helpers.LocationHelper;
import com.example.finanseapp.Helpers.RecyclerViewAdapter;
import com.example.finanseapp.Helpers.ShakingDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

public class MainActivity extends AppCompatActivity{

    private AppDatabase db;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<Entry> entries;

    private ShakingDetector shakeDetector;
    private LocationHelper locationHelper;
    private ImageButton buttonCharts;
    private Paint paint;
    private ImageButton imgButtonIncome, imgbuttonAddCategory;
    private TextView textViewBalance;
    private TextView textViewBalanceConverted;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private ActionBar actionBar;
    private DollarSignAnimation dollarAnimator;
    private RelativeLayout relativeLayout;

    private Spinner spinnerCategory;

    private ActivityResultLauncher<Intent> resultLauncher;

    private BlurOnFlipManager blurManager;
    private int dollarGreenID, dollarRedID;
    public static String COUNTRY_CODE = "LT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(getApplicationContext());

        initializeUI();
        initializeHardware();
        initializeData();
        setUpRecyclerView();
        setUpDollarSignAnimation();
        setUpBalanceWiggle();
        setUpActivityResults();
        setCategoriesFilter();

        blurManager = new BlurOnFlipManager(this, textViewBalance, textViewBalanceConverted);
    }

    @Override
    protected void onResume() {
        super.onResume();
        blurManager.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        blurManager.unregister();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeLocation();
        updateBalanceText();
        updateBalanceConvertText();
        setUpRecyclerView();
        updateCategoriesSpinner();
    }

    private void setCategoriesFilter(){
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Category> categoryList = db.categoryDao().getAllCategories();
            List<String> categories = new ArrayList<>();
            categories.add("All");
            for (Category category : categoryList) {
                categories.add(category.getName());
            }
            runOnUiThread(() -> {
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(MainActivity.this,
                        R.layout.spinner_dropdown_main, categories);
                spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
                spinnerCategory.setAdapter(spinnerAdapter);

                spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedCategory = parent.getItemAtPosition(position).toString();

                        Executors.newSingleThreadExecutor().execute(() -> {
                            if (selectedCategory.equals("All")) {
                                List<Entry> allEntries = db.entryDao().getAllEntries();
                                runOnUiThread(() -> adapter.updateData(allEntries));
                            } else {
                                Category selectedCat = db.categoryDao().getCategoryByName(selectedCategory);
                                if (selectedCat == null) {
                                    return;
                                }

                                List<Entry> filtered = new ArrayList<>();
                                for (Entry e : db.entryDao().getAllEntries()) {
                                    if (e.getCategory().equals(selectedCat.getName())) {
                                        filtered.add(e);
                                    }
                                }

                                runOnUiThread(() -> {
                                    adapter.updateData(filtered);
                                });
                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            });
        });
    }

    private void updateCategoriesSpinner(){
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Category> categoryList = db.categoryDao().getAllCategories();
            List<String> categories = new ArrayList<>();
            categories.add("All");
            for (Category category : categoryList) {
                categories.add(category.getName());
            }
            runOnUiThread(() -> {
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(MainActivity.this,
                        R.layout.spinner_dropdown_main, categories);
                spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
                spinnerCategory.setAdapter(spinnerAdapter);

            });
        });
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
      
        //getCountryFromLocation();

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
        textViewBalanceConverted = findViewById(R.id.textViewBalanceConverted);

        spinnerCategory = findViewById(R.id.spinnerCategory);
    }

    private void initializeData() {
        executor.execute(() -> {
            generateData(db);
            updateBalanceText();
        });
    }

    private void initializeHardware(){
        shakeDetector = new ShakingDetector(this);
        shakeDetector.startShakeDetection();
    }

    private void initializeLocation(){
        locationHelper = new LocationHelper(this);

        locationHelper.detectCountry(this, new LocationHelper.OnCountryDetectedListener() {
            @Override
            public void onCountryDetected(String countryCode) {
                //COUNTRY_CODE = countryCode;


                //TextView locationText = findViewById(R.id.locationText);
               // locationText.setText("Detected country: " + countryCode);
                Toast.makeText(MainActivity.this, "Detected country: " + countryCode, Toast.LENGTH_SHORT).show();
                Log.d("COUNTRY", "GPS country ISO: " + countryCode + " " + COUNTRY_CODE);

                if (!COUNTRY_CODE.equals(countryCode)) {
                    COUNTRY_CODE = countryCode;



                    //Intent intent = getIntent();
                    //finish();
                    //startActivity(intent);
                    updateBalanceConvertText();



                }
            }
        });

    }

    double convertBalance(double balance) {
        MonetaryAmount balanceE = Monetary.getDefaultAmountFactory().setCurrency("EUR").setNumber(balance).create();

        CurrencyConversion conversion;

        Set<String> euroCountries = new HashSet<>(Arrays.asList(
                "AT", "BE", "CY", "EE", "FI", "FR", "DE", "GR", "IE", "IT",
                "LV", "LT", "LU", "MT", "NL", "PT", "SK", "SI", "ES"
        ));

        if (euroCountries.contains(COUNTRY_CODE)) {
            conversion = MonetaryConversions.getConversion("EUR");
            return balanceE.getNumber().doubleValue();
        }

        switch (COUNTRY_CODE) {
            case "GB":
                conversion = MonetaryConversions.getConversion("GBP");
                break;
            case "PL":
                conversion = MonetaryConversions.getConversion("PLN");
                break;
            default:
                conversion = MonetaryConversions.getConversion("USD");
                break;
        }

        MonetaryAmount convertedAmountEURtoUSD = balanceE.with(conversion);

        return convertedAmountEURtoUSD.getNumber().doubleValue();
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        executor.execute(() -> {
            entries = db.entryDao().getEntriesByAccountId("1");

            runOnUiThread(() -> {
                DialogHelper editSourceDialogHelper = new DialogHelper(this);

                editSourceDialogHelper.onBalanceUpdate = () -> {
                    updateBalanceText();
                    updateBalanceConvertText();
                };

                adapter = new RecyclerViewAdapter(entries, editSourceDialogHelper, MainActivity.COUNTRY_CODE);
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
                    updateBalanceConvertText();
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

            if (db.categoryDao().getCategoryByName("Default") == null) {
                db.categoryDao().insert(new Category("Default", 0));
            }
        });
    }

    private void updateBalanceText() {
        executor.execute(() -> {
            //String currency = getCurrencySymbol(COUNTRY_CODE);
            String currency = getString(R.string.currency_symbol_euro);
            String balanceText = Float.toString(db.entryDao().getTotalAmountByAccount(Integer.toString(db.currentAccount))) + currency;
            runOnUiThread(() -> textViewBalance.setText(balanceText));
        });
    }

    private void updateBalanceConvertText() {
        executor.execute(() -> {
            String currency = getCurrencySymbol(COUNTRY_CODE);
            String balanceText = Float.toString(db.entryDao().getTotalAmountByAccount(Integer.toString(db.currentAccount)));
            if (COUNTRY_CODE != "LT") {
                //String tekstas = String.valueOf(Math.round(, 2)) + currency;
                String tekstas = String.format("%.1f", convertBalance(Double.parseDouble(balanceText))) + currency;
                runOnUiThread(() -> textViewBalanceConverted.setText(tekstas));
            }
        });
    }

    private void updateRecyclerView() {
        executor.execute(() -> {
            entries = db.entryDao().getEntriesByAccountId("1");

            runOnUiThread(() -> {
                adapter.updateData(entries);
                updateBalanceText();
                updateBalanceConvertText();
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

    public String getCurrencySymbol(String countryCode) {
        Set<String> euroCountries = new HashSet<>(Arrays.asList(
                "AT", "BE", "CY", "EE", "FI", "FR", "DE", "GR", "IE", "IT",
                "LV", "LT", "LU", "MT", "NL", "PT", "SK", "SI", "ES"
        ));

        if (euroCountries.contains(countryCode)) {
            return getString(R.string.currency_symbol_euro);
        }

        switch (countryCode) {
            case "GB":
                return getString(R.string.currency_symbol_pounds);
            case "PL":
                return getString(R.string.currency_symbol_zloty);
            default:
                return getString(R.string.currency_symbol_dollar);
        }
    }
}