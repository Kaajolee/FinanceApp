package com.example.finanseapp.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import com.example.finanseapp.R;

import java.util.List;

public class RecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<String> Data;
    public RecyclerViewAdapter(List<String> data){
        this.Data = data;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.textView.setText(this.Data.get(position));
    }

    @Override
    public int getItemCount() {
        return this.Data.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textView;

        public ViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
            this.textView = view.findViewById(R.id.textview);
        }
        @Override
        public void onClick(View v) {

            Toast.makeText(v.getContext(), "Clicked on: " + getLayoutPosition(), Toast.LENGTH_SHORT).show();
        }
    }
}
