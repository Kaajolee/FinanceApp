package com.example.finanseapp.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.finanseapp.R;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

public class DollarSignAnimation extends View {
    private List<DollarSign> dollarSigns = new ArrayList<>();
    private Bitmap dollarBitmap;
    public int dollarImageId = R.drawable.dollarsigngreen;
    private final int FRAME_RATE = 16; //60fps
    private Handler handler = new Handler();

    public DollarSignAnimation(Context context) {
        super(context);
        init();
    }
    public DollarSignAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public void setDollarImageId(int resId, int spriteAmount) {
        this.dollarImageId = resId;
        this.dollarBitmap = BitmapFactory.decodeResource(getResources(), resId);

        dollarSigns.clear();
        for (int i = 0; i < spriteAmount; i++) {
            dollarSigns.add(new DollarSign(dollarBitmap, getWidth(), getHeight()));
        }

        invalidate();
    }
    private void init(){
        dollarBitmap = BitmapFactory.decodeResource(getResources(), dollarImageId);
        postDelayed(updateRunnable, FRAME_RATE);
    }
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
            handler.postDelayed(this, FRAME_RATE);
        }
    };

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        dollarSigns.clear();
        //for (int i=0; i<10; i++){
        //    dollarSigns.add(new DollarSign(dollarBitmap, w, h));
        //}
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        for (DollarSign sign : dollarSigns){
            sign.Update(getWidth(), getHeight()/2);
            sign.Draw(canvas);
        }
    }

    public void addDollar() {
        if (dollarBitmap == null) {
            dollarBitmap = BitmapFactory.decodeResource(getResources(), dollarImageId);
        }
        dollarSigns.add(new DollarSign(dollarBitmap, getWidth(), getHeight()));
        invalidate();
    }
}
