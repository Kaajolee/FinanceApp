package com.example.finanseapp.Helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class DollarSign {
    float x, y;
    float dx, dy;
    int width, height;
    Bitmap bitmap;

    public DollarSign(Bitmap bitmap, int screenWidth, int screenHeight){
        this.bitmap = bitmap;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();

        x = (float)(Math.random() * (screenWidth - width));
        y = (float)(Math.random() * (screenHeight - height));
        dx = (float)(Math.random() * 6 - 3); // greittis tarp -3 3
        dy = (float)(Math.random() * 6 - 3);


    }
    public void Update(int screenWidth, int screenHeight){
        x += dx;
        y += dy;

        if (x < 0 || x + width > screenWidth) dx *= -1;
        if (y < 0 || y + height > screenHeight) dy *= -1;


    }
    public void Draw(Canvas canvas){
        canvas.drawBitmap(bitmap, x, y,null);
    }
}
