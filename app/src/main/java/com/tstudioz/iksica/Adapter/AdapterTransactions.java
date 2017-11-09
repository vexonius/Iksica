package com.tstudioz.iksica.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tstudioz.iksica.Model.Transaction;
import com.tstudioz.iksica.R;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by etino7 on 11-Oct-17.
 */

public class AdapterTransactions extends RecyclerView.Adapter<AdapterTransactions.DetailViewHolder> implements RealmChangeListener{
    private RealmResults<Transaction> transactions;

    public AdapterTransactions(RealmResults<Transaction> tranzakcije){
        this.transactions=tranzakcije;
        transactions.addChangeListener(this);

    }

    public class DetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView time, date, place, amount;

        public DetailViewHolder(final View view){
            super(view);
            time=(TextView)itemView.findViewById(R.id.transaction_time);
            place=(TextView)itemView.findViewById(R.id.transaction_place);
            amount=(TextView)itemView.findViewById(R.id.transaction_amount);
            date=(TextView)view.findViewById(R.id.transaction_date);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){

        }
    }

    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item_layout, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailViewHolder holder, int position){
        Transaction item = transactions.get(position);
        holder.time.setText(item.getVrijeme());
        holder.date.setText(item.getDatum());
        holder.place.setText(item.getRestoran());
        holder.amount.setText("-" + item.getSubvencija() + " kn");

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    @Override
    public void onChange(Object element){
        notifyDataSetChanged();
    }

}
