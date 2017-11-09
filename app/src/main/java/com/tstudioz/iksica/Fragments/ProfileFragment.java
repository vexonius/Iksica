package com.tstudioz.iksica.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tstudioz.iksica.Activities.SignInActivity;
import com.tstudioz.iksica.Adapter.AdapterInfo;
import com.tstudioz.iksica.Model.User;
import com.tstudioz.iksica.Model.UserInfoItem;
import com.tstudioz.iksica.R;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by etino7 on 18-Oct-17.
 */

public class ProfileFragment extends Fragment {

    private RecyclerView rv;
    private Button signOutButton;
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

        signOutButton = (Button)view.findViewById(R.id.button_logout);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        return view;
    }

    public void showInfoRecyclerView(){
        RealmResults<UserInfoItem> informacije = mRealm.where(UserInfoItem.class).findAll();

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        AdapterInfo ah = new AdapterInfo(informacije);
        rv.setAdapter(ah);
    }

    public void signOut(){
        SharedPreferences sp = getActivity().getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("korisnik_prijavljen");
        editor.commit();

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.deleteAll();
            }
        });

        getActivity().startActivity(new Intent(getActivity(), SignInActivity.class));
        getActivity().finish();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(mRealm!=null)
            mRealm.close();
    }
}