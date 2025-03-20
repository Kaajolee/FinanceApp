package com.example.finanseapp.helpers;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.example.finanseapp.Entities.Entry;
import com.example.finanseapp.R;

public class RecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Entry> Data;
    public RecyclerViewAdapter(List<Entry> data){
        this.Data = data;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {

        Entry entry = this.Data.get(position);
        holder.layout.setBackgroundResource(R.drawable.rounded_all_corners_small);

        holder.textViewName.setText((String) entry.getName());
        holder.textViewCategory.setText(Long.toString((int) entry.getDate()));

//income 0 expense 1 both 2
        if(entry.getType() == 0){

            holder.textViewNumber.setText("+" + Float.toString((int)entry.getAmount()));
            holder.textViewNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                                                                      R.color.green_005));
        }
        else if(entry.getType() == 1){

            holder.textViewNumber.setText("-" + Float.toString((int)entry.getAmount()));
            holder.textViewNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                                                                      R.color.red));
        }
        else {
            holder.textViewNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.purple_200));
        }

        Log.i("FRONTEND", "Object added to recycler, " + holder.textViewName + " " + holder.textViewNumber);
    }

    @Override
    public int getItemCount() {
        return this.Data.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textViewName;
        private TextView textViewCategory;
        private TextView textViewNumber;
        private LinearLayout layout;
        public ViewHolder(View view){
            super(view);
            view.setOnClickListener(this);

            this.textViewName = view.findViewById(R.id.textview);
            this.textViewCategory = view.findViewById(R.id.textview1);
            this.textViewNumber = view.findViewById(R.id.textview2);
            this.layout = view.findViewById(R.id.linearlayoutlist);
        }
        @Override
        public void onClick(View v) {

            Toast.makeText(v.getContext(), "Clicked on: " + getLayoutPosition(), Toast.LENGTH_SHORT).show();
        }
    }
}
