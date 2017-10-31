package com.tstudioz.menze.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.animation.Animation;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.tstudioz.menze.Adapter.AdapterTransactions;
import com.tstudioz.menze.Model.Transaction;
import com.tstudioz.menze.R;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by etino7 on 18-Oct-17.
 */

public class TransactionsFragment extends Fragment {

    private RecyclerView tRv;
    private Realm mRealm;
    public float[] labelsY ;
    public String[] labelsX ;
    public LineChartView lineChart;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedBundleInstance){
        View view = inflater.inflate(R.layout.transactions_layout, parent, false);

        mRealm = Realm.getDefaultInstance();

        tRv = (RecyclerView)view.findViewById(R.id.recycler_transactions);
        showInfoRecyclerView();

        lineChart = (LineChartView)view.findViewById(R.id.chart);
        inicijalizacijaPodataka();

        return view;
    }

    public void showInfoRecyclerView(){
        RealmResults<Transaction> transakcije = mRealm.where(Transaction.class).findAll();
        tRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        AdapterTransactions at = new AdapterTransactions(transakcije);
        tRv.setAdapter(at);
    }

    public void inicijalizacijaPodataka(){
        RealmResults<Transaction> transakcije = mRealm.where(Transaction.class).findAll();
        int max = transakcije.size();
        labelsY = new float[transakcije.size()];
        labelsX = new String[transakcije.size()];

        int i = max-1;
        for(Transaction t : transakcije){
                String iznos = t.getSubvencija();
                labelsY[i] = Float.parseFloat(iznos.replace(",", "."));
                labelsX[i] = t.getDatum();
                i--;
        }

        inicijalizacijaCharta();
    }

    public void inicijalizacijaCharta(){
        LineSet dataset = new LineSet(labelsX, labelsY);
        dataset.setColor(getResources().getColor(R.color.line_color))
                .setFill(getResources().getColor(R.color.dirty_white))
                .setDotsColor(getResources().getColor(R.color.colorAccent))
                .setDotsRadius(10)
                .setThickness(7);

        Animation anim = new Animation(800);
        lineChart.addData(dataset);
        lineChart.setAxisColor(getResources().getColor(R.color.icon_inactive));
        lineChart.setYAxis(true);
        lineChart.setAxisBorderValues(0,30);
        lineChart.setStep(10);
        lineChart.show(anim);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(mRealm!=null)
            mRealm.close();
    }

}
