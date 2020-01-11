package com.tstudioz.iksica.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tstudioz.iksica.Data.Models.UserInfoItem;
import com.tstudioz.iksica.R;

import java.util.List;

public class AdapterInfo extends RecyclerView.Adapter<AdapterInfo.InfoItemViewHolder> {
    private List<UserInfoItem> items;

    public AdapterInfo(List<UserInfoItem> items) {
        this.items = items;
    }

    public class InfoItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, desc;

        InfoItemViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.info_title_text);
            desc = (TextView) view.findViewById(R.id.info_desc_text);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    @Override
    public InfoItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_info_row, parent, false);
        return new InfoItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InfoItemViewHolder holder, int position) {
        UserInfoItem item = items.get(position);

        holder.title.setText(item.getLabel());
        holder.desc.setText(item.getItem());
    }

    public void updateData(List<UserInfoItem> items){
        this.items = items;
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;

        return items.size();
    }

}
