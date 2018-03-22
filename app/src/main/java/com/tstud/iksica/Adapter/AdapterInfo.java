package com.tstud.iksica.Adapter;



import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tstud.iksica.Model.UserInfoItem;
import com.tstud.iksica.R;


import io.realm.RealmResults;

public class AdapterInfo extends RecyclerView.Adapter<AdapterInfo.InfoItemViewHolder>  {
    private RealmResults<UserInfoItem> infos;

    public AdapterInfo(RealmResults<UserInfoItem> info){
        this.infos=info;
    }

    public class InfoItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title, desc;

        public InfoItemViewHolder(View view){
            super(view);

            title=(TextView)view.findViewById(R.id.info_title_text);
            desc=(TextView)view.findViewById(R.id.info_desc_text);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    @Override
    public InfoItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_info_row, parent, false);
        return new InfoItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InfoItemViewHolder holder, int position){
        UserInfoItem item = infos.get(position);

        holder.title.setText(item.getItemTitle());
        holder.desc.setText(item.getItemDesc());
    }

    @Override
    public int getItemCount(){
        return infos.size();
    }

}
