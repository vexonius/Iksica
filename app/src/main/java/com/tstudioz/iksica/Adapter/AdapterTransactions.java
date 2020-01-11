package com.tstudioz.iksica.Adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tstudioz.iksica.Data.Models.Transaction;
import com.tstudioz.iksica.R;
import com.tstudioz.iksica.Utils.DetailClickListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by etino7 on 11-Oct-17.
 */

public class AdapterTransactions extends RecyclerView.Adapter<AdapterTransactions.DetailViewHolder> {
    private List<Transaction> transactions;
    private DetailClickListener detailClickListener;

    public AdapterTransactions(List<Transaction> transactions, DetailClickListener detailClickListener) {
        this.transactions = transactions;
        this.detailClickListener = detailClickListener;
    }

    public class DetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView time, date, place, amount;
        private DetailClickListener detailClickListener;


        public DetailViewHolder(final View view, DetailClickListener detailClickListener) {
            super(view);
            this.detailClickListener = detailClickListener;
            time = (TextView) itemView.findViewById(R.id.transaction_time);
            place = (TextView) itemView.findViewById(R.id.transaction_place);
            amount = (TextView) itemView.findViewById(R.id.transaction_amount);
            date = (TextView) view.findViewById(R.id.transaction_date);

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            detailClickListener.onClicked(transactions.get(getAdapterPosition()));
        }
    }

    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item_layout, parent, false);
        return new DetailViewHolder(view, detailClickListener);
    }

    @Override
    public void onBindViewHolder(DetailViewHolder holder, int position) {
        Transaction item = transactions.get(position);
        holder.time.setText(item.getTime());
        holder.date.setText(item.getDate());
        holder.amount.setText("-" + item.getAmount() + "  kn");
        holder.place.setText(item.getRestourant());

    }

    @Override
    public int getItemCount() {
        if (transactions == null) return 0;
        return transactions.size();
    }

    public void updateItems(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

}
