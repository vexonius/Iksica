package com.tstudioz.menze.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tstudioz.menze.Adapter.AdapterInfo;
import com.tstudioz.menze.Adapter.AdapterTransactions;
import com.tstudioz.menze.Model.Transaction;
import com.tstudioz.menze.Model.UserInfoItem;
import com.tstudioz.menze.R;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by etino7 on 18-Oct-17.
 */

public class TransactionsFragment extends Fragment {

    private RecyclerView tRv;
    private Realm mRealm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedBundleInstance){
        View view = inflater.inflate(R.layout.transactions_layout, parent, false);

        mRealm = Realm.getDefaultInstance();
        tRv = (RecyclerView)view.findViewById(R.id.recycler_transactions);
        showInfoRecyclerView();

        return view;
    }

    public void showInfoRecyclerView(){

        RealmResults<Transaction> transakcije = mRealm.where(Transaction.class).findAll();
        tRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        AdapterTransactions at = new AdapterTransactions(transakcije);
        tRv.setAdapter(at);
    }
}
