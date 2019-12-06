package com.tstudioz.iksica.Utils;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.tstudioz.iksica.Data.Models.User;

import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;


public class RealmSingleResultLiveData<T extends RealmModel> extends LiveData<User> {

    private RealmResults<User> results;
    private User user;

    public RealmSingleResultLiveData(RealmResults<User> results){
        this.results = results;
        this.user = (User) results.first();
    }

    private final RealmChangeListener<RealmResults<User>> listener = new RealmChangeListener<RealmResults<User>>() {
        @Override
        public void onChange(RealmResults<User> results) {
            user = (User) results.first();
            setVualue(user);
        }
    };


    protected void setVualue(User value) {
        super.setValue(value);
    }

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
