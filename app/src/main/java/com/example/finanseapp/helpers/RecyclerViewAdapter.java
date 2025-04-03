package com.example.finanseapp.Helpers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finanseapp.AppDatabase;
import com.example.finanseapp.Entities.Entry;
import com.example.finanseapp.R;

import java.util.List;
import java.util.concurrent.Executors;

public class RecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final List<Entry> Data;

    public RecyclerViewAdapter(List<Entry> data) {
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

        holder.id = entry.getId();

        holder.textViewName.setText(entry.getName());
        holder.textViewCategory.setText(Long.toString((int) entry.getDate()));
        /*
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(holder.itemView.getContext(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Toast.makeText(holder.itemView.getContext(), "on Swiped ", Toast.LENGTH_SHORT).show();
                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
                //arrayList.remove(position);
                //adapter.notifyDataSetChanged();

            }
        };
        */
//income 0 expense 1 both 2
        if (entry.getType() == 0) {
            holder.textViewNumber.setText("+" + Float.toString((int) entry.getAmount()));
            holder.textViewNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.green_005));
        } else if (entry.getType() == 1) {

            holder.textViewNumber.setText(Float.toString((int) entry.getAmount()));
            holder.textViewNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.red));
        } else {
            holder.textViewNumber.setText(Float.toString((int) entry.getAmount()));
            holder.textViewNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.purple_200));
        }

        Log.i("FRONTEND", "Object added to recycler, " + holder.textViewName + " " + holder.textViewNumber);
    }

    public void removeItem(int position) {
        Log.i("DBBBBBBB", Integer.toString(Data.size()));

        if (Data.size() > 0) {

            Data.remove(position);
            notifyItemRemoved(position);
        } else {
            Log.i("DBBBBBBB", "Trying to delete from an ampty DATA object, size = 0");
        }


        Log.i("DBBBBBBB", Integer.toString(Data.size()));
    }


    @Override
    public int getItemCount() {
        return this.Data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AppDatabase db;
        private int id;
        private final TextView textViewName;
        private final TextView textViewCategory;
        private final TextView textViewNumber;
        private final LinearLayout layout;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            db = AppDatabase.getInstance(view.getContext());

            this.textViewName = view.findViewById(R.id.textview);
            this.textViewCategory = view.findViewById(R.id.textview1);
            this.textViewNumber = view.findViewById(R.id.textview2);
            this.layout = view.findViewById(R.id.linearlayoutlist);
        }

        @Override
        public void onClick(View v) {
            /*
            Executors.newSingleThreadExecutor().execute(() -> {
                    db.entryDao().delete(db.entryDao().getEntryById(id));
            });

            Intent intent = new Intent(v.getContext(), MainActivity.class);
            v.getContext().startActivity(intent);
            */
        }

        public void delete() {
            Executors.newSingleThreadExecutor().execute(() -> {
                db.entryDao().delete(db.entryDao().getEntryById(id));
            });


            //Intent intent = new Intent(itemView.getContext(), MainActivity.class);
            //itemView.getContext().startActivity(intent);
        }


    }
}
