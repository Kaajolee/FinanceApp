package com.example.finanseapp.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
                Log.e("RecyclerViewImagesAdapter", "addImage received null bitmap");
                return;
            }
            imageBitmaps.add(bitmap);
            notifyItemInserted(imageBitmaps.size() - 1);

    }

    public void clearImages() {
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
    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewPhoto);
        }
    }
}

