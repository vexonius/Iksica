package com.tstudioz.iksica.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tstudioz.iksica.Data.Models.TransactionItem;
import com.tstudioz.iksica.R;

import java.util.List;

/**
 * Created by etino7 on 11-Oct-17.
 */

public class TransactionDetailsAdapter extends RecyclerView.Adapter<TransactionDetailsAdapter.DetailViewHolder> {
    private List<TransactionItem> items;

    public TransactionDetailsAdapter(List<TransactionItem> items) {
        this.items = items;
    }

    public class DetailViewHolder extends RecyclerView.ViewHolder {
        private TextView quantity, name, price, subvention;


        public DetailViewHolder(final View view) {
            super(view);

            quantity = (TextView) itemView.findViewById(R.id.item_quantity);
            name = (TextView) itemView.findViewById(R.id.item_name);
            price = (TextView) itemView.findViewById(R.id.item_price);
            subvention = (TextView) view.findViewById(R.id.subvention_amount);
        }
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.billing_item_layout, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailViewHolder holder, int position) {
        TransactionItem item = items.get(position);
        holder.quantity.setText(item.getQuantity() + "X");
        holder.name.setText(item.getName());
        holder.price.setText(item.getTotal());
        holder.subvention.setText("-" + item.getItemSubvention());

    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    public void updateItems(List<TransactionItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

}
