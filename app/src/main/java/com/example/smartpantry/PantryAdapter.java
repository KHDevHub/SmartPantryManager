package com.example.smartpantry;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Shows the pantry items in the RecyclerView
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    // MainActivity uses this to find out which button was pressed
    public interface Listener {
        void onEdit(Ingredient item);

        void onDelete(Ingredient item);
    }

    private List<Ingredient> items;
    private final Listener listener;
    private boolean showExpiryAlerts = true;

    public PantryAdapter(List<Ingredient> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void setItems(List<Ingredient> items, boolean showExpiryAlerts) {
        this.items = items;
        this.showExpiryAlerts = showExpiryAlerts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient item = items.get(position);

        holder.textName.setText(item.getName());
        holder.textAmount.setText(item.getQuantityText() + " " + item.getUnit());

        if (item.getExpiry() == null || item.getExpiry().isEmpty()) {
            holder.textExpiry.setText("No expiry date");
            holder.textExpiry.setTextColor(Color.GRAY);
        } else {
            holder.textExpiry.setText("Expires: " + item.getExpiry());
            if (showExpiryAlerts && expiresSoon(item.getExpiry())) {
                holder.textExpiry.setTextColor(Color.RED);
            } else {
                holder.textExpiry.setTextColor(Color.GRAY);
            }
        }

        holder.buttonEdit.setOnClickListener(v -> listener.onEdit(item));
        holder.buttonDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // True if the item expires within 3 days (or has already expired)
    private boolean expiresSoon(String expiry) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = format.parse(expiry);
            long days = (date.getTime() - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
            return days <= 3;
        } catch (ParseException e) {
            return false;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textAmount, textExpiry;
        Button buttonEdit, buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textAmount = itemView.findViewById(R.id.textAmount);
            textExpiry = itemView.findViewById(R.id.textExpiry);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
