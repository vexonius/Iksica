package com.tstudioz.menze.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tstudioz.menze.Adapter.AdapterInfo;
import com.tstudioz.menze.Model.User;
import com.tstudioz.menze.Model.UserInfoItem;
import com.tstudioz.menze.R;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by etino7 on 18-Oct-17.
 */

public class ProfileFragment extends Fragment {

    private RecyclerView rv;
    Realm mRealm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedBundleInstance){
        View view = inflater.inflate(R.layout.profile_layout, parent, false);

        mRealm = Realm.getDefaultInstance();
        User user = mRealm.where(User.class).findFirst();

        CircularImageView image = (CircularImageView) view.findViewById(R.id.circularImageView);

        Glide.with(this).load(user.getSrcLink()).into(image);

        TextView imeStudenta = (TextView)view.findViewById(R.id.name_surename);
        imeStudenta.setText(user.getuName());

        rv = (RecyclerView)view.findViewById(R.id.user_info_recycler);
        showInfoRecyclerView();

        return view;
    }

    public void showInfoRecyclerView(){

        RealmResults<UserInfoItem> informacije = mRealm.where(UserInfoItem.class).findAll();

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        AdapterInfo ah = new AdapterInfo(informacije);
        rv.setAdapter(ah);
    }
}
