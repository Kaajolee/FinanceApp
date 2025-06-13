package com.example.finanseapp.Helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finanseapp.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewImagesAdapter extends RecyclerView.Adapter<RecyclerViewImagesAdapter.PhotoViewHolder> {

    private Context context;
    private List<Bitmap> imageBitmaps = new ArrayList<>();

    public RecyclerViewImagesAdapter(Context context) {
        this.context = context;
    }

    public void addImage(Bitmap bitmap) {
        if(bitmap == null) {
            Log.e("RECYCLERVIEWIMAGEADAPTER", "addImage received null bitmap");
            return;
        }
        Bitmap resized = getResizedBitmap(bitmap, 400, 400);
        imageBitmaps.add(resized);
        notifyItemInserted(imageBitmaps.size() - 1);
    }

    public void clearImages() {
        for (Bitmap b : imageBitmaps) {
            b.recycle();
        }
        imageBitmaps.clear();
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_image_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        holder.imageView.setImageBitmap(imageBitmaps.get(position));
    }

    @Override
    public int getItemCount() {
        return imageBitmaps.size();
    }
    public List<Bitmap> getAllImages (){
        return imageBitmaps;
    }
    public Bitmap getResizedBitmap(Bitmap original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();

        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;

        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float)maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float)maxWidth / ratioBitmap);
        }

        return Bitmap.createScaledBitmap(original, finalWidth, finalHeight, true);
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewPhoto);

            imageView.setOnClickListener(v -> {

                Drawable drawable = imageView.getDrawable();
                if (drawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    showImageDialog(v.getContext(), bitmap);
                }
            });

        }
        public void showImageDialog(Context context, Bitmap imageBitmap) {
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_show_image);

            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_all_corners_small_nontrans);

            ImageView imageView = dialog.findViewById(R.id.dialogImageView);
            imageView.setImageBitmap(imageBitmap);

            Button closeBtn = dialog.findViewById(R.id.buttonCloseDialog);
            closeBtn.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        }
    }
}

