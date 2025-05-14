package com.example.finanseapp.Helpers;

import android.animation.ObjectAnimator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finanseapp.AppDatabase;
import com.example.finanseapp.Entities.Entry;
import com.example.finanseapp.R;

import java.util.List;
import java.util.concurrent.Executors;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Entry> data;
    private final DialogHelper editDialogHelper;

    public RecyclerViewAdapter(List<Entry> data, DialogHelper editDialogHelper) {
        this.data = data;
        this.editDialogHelper = editDialogHelper;

        editDialogHelper.saveButton.setOnClickListener(v -> {
            String name = editDialogHelper.sourceName.getText().toString();
            float amount = Float.parseFloat(editDialogHelper.sourceAmount.getText().toString());
            int type = editDialogHelper.ReturnSwitchStateInt();
            // TODO: prideti kategorija
            Entry newEntry = new Entry(name, 1, type, amount, 2025);

            updateDataEntry(newEntry, editDialogHelper.adapterPositionId);
            editDialogHelper.toggleDialog(false);
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(rowItem, editDialogHelper);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Entry entry = data.get(position);
        holder.id = entry.getId();

        holder.textViewName.setText(entry.getName());
        holder.textViewCategory.setText(Float.toString(entry.getDate()));

        String currency = holder.itemView.getContext().getString(R.string.currency_symbol);
        String displayAmount = Float.toString((int) entry.getAmount());
        int colorRes;

        switch (entry.getType()) {
            case 0: // Income
                displayAmount = "+" + displayAmount;
                colorRes = R.color.green_005;
                break;
            case 1: // Expense
                displayAmount = "-" + displayAmount;
                colorRes = R.color.red;
                break;
            default:
                colorRes = R.color.purple_200;
                break;
        }

        holder.textViewNumber.setText(displayAmount + currency);
        holder.textViewNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), colorRes));

        animateViewHolder(holder.rowItemView, 700, 550);

        Log.i("FRONTEND", "Object added to recycler: " + entry.getName() + ", " + displayAmount);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    void animateViewHolder(View view, int delay, int duration){

        DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
        int screenY = displayMetrics.heightPixels;

        view.setTranslationY(screenY);

        view.postDelayed(()->{

            ObjectAnimator animator;
            animator = ObjectAnimator.ofFloat(view, "translationY", 0f);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(duration);

            animator.start();

        }, delay);

    }
    public void updateDataEntry(Entry newEntry, int positionId) {
        data.set(positionId, newEntry);
        notifyItemChanged(positionId);
    }

    public void removeItem(int position) {
        if (!data.isEmpty() && position >= 0 && position < data.size()) {
            data.remove(position);
            notifyItemRemoved(position);
        } else {
            Log.w("RecyclerViewAdapter", "Attempt to remove item from empty or invalid position.");
        }
    }

    public void updateData(List<Entry> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final AppDatabase db;
        private final DialogHelper editDialogHelper;
        private final TextView textViewName, textViewCategory, textViewNumber;
        private final LinearLayout layout;
        public final View rowItemView;
        int id;

        public ViewHolder(View view, DialogHelper editDialogHelper) {
            super(view);
            this.editDialogHelper = editDialogHelper;
            this.db = AppDatabase.getInstance(view.getContext());

            textViewName = view.findViewById(R.id.textview);
            textViewCategory = view.findViewById(R.id.textview1);
            textViewNumber = view.findViewById(R.id.textview2);
            layout = view.findViewById(R.id.linearlayoutlist);
            rowItemView = view;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Log.i("RECYCLER VIEW ITEM", "Item clicked at position: " + position);
                editDialogHelper.setValues(db, id, position);
                editDialogHelper.toggleDialog(true);
            }
        }

        public void delete() {
            Executors.newSingleThreadExecutor().execute(() -> {
                db.entryDao().delete(db.entryDao().getEntryById(id));
            });
        }
    }
}
