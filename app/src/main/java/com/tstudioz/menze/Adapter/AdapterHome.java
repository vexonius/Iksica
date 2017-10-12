package com.tstudioz.menze.Adapter;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tstudioz.menze.Activities.DetailActivity;
import com.tstudioz.menze.Model.MenzaItem;
import com.tstudioz.menze.R;

import java.util.List;

public class AdapterHome extends RecyclerView.Adapter<AdapterHome.MenzaItemViewHolder>  {
    private List<MenzaItem> menze;

    public AdapterHome(List<MenzaItem> menza){
        this.menze=menza;
    }

    public class MenzaItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;
        RelativeLayout relativeLayout;

        public MenzaItemViewHolder(View view){
            super(view);

            textView=(TextView)view.findViewById(R.id.relative_recycler_home_text);
            relativeLayout=(RelativeLayout)view.findViewById(R.id.relative_recycler_home_item);

            view.setOnClickListener(this);
        }


        public void onClick(View view){
            Activity activity = (Activity) view.getContext();
            Intent in = new Intent(view.getContext(), DetailActivity.class);
            in.putExtra("name", menze.get(getAdapterPosition()).getImeMenze());
            activity.startActivity(in);
        }
    }

    @Override
    public MenzaItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_home_item, parent, false);
        return new MenzaItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MenzaItemViewHolder holder, int position){
        MenzaItem item = menze.get(position);
        holder.textView.setText(item.getImeMenze());
    }

    @Override
    public int getItemCount(){
        return menze.size();
    }

}
