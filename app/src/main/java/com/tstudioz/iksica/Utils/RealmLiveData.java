package com.tstudioz.iksica.Utils;

import androidx.lifecycle.LiveData;

import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by etino7 on 18/10/2019.
 */
public class RealmLiveData<T extends RealmModel> extends LiveData<RealmResults<T>> {

    private RealmResults<T>  results;

    public RealmLiveData(RealmResults<T> results){
        this.results = results;
    }

    private final RealmChangeListener<RealmResults<T>> listener = results -> setValue(results);

    @Override
    protected void onActive() {
        super.onActive();
        results.addChangeListener(listener);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        results.removeChangeListener(listener);
    }
}
