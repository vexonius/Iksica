package com.tstudioz.menze.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tstudioz.menze.Model.MeniItem;
import com.tstudioz.menze.R;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by amarthus on 11-Oct-17.
 */

public class AdapterDetail extends RecyclerView.Adapter<AdapterDetail.DetailViewHolder> implements RealmChangeListener{
    private RealmResults<MeniItem> meniji;

    public AdapterDetail(RealmResults<MeniItem> menies){
        this.meniji=menies;
        meniji.addChangeListener(this);

    }

    public class DetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public DetailViewHolder(final View view){
            super(view);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){

        }
    }

    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meni_row_item, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailViewHolder holder, int position){
        MeniItem meni = meniji.get(position);

    }

    @Override
    public int getItemCount() {
        return meniji.size();
    }

    @Override
    public void onChange(Object element){
        notifyDataSetChanged();
    }

}
