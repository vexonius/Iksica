package com.tstudioz.menze.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.tstudioz.menze.Model.User;
import com.tstudioz.menze.R;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by amarthus on 08-Oct-17.
 */

public class SignInFragment extends Fragment {

    Button signInButton;
    EditText editEmail, editPass;
    public Realm realm;


    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedBundleInstance){
        View view = inflater.inflate(R.layout.sign_in_layout, container, false);

        signInButton = (Button)view.findViewById(R.id.sign_in_button);
        editEmail=(EditText)view.findViewById(R.id.sign_in_username);
        editPass=(EditText)view.findViewById(R.id.sign_in_password);

        realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            User user = realm.createObject(User.class);
                            user.setuMail(editEmail.getText().toString());
                            user.setuPassword(editPass.getText().toString());

                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("korisnik_prijavljen", true);
                            editor.commit();

                            CardFragment cf = new CardFragment();
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.iksica_frame, cf);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    });
                } finally {
                    realm.close();
                }
            }
        });

        return  view;
    }


}
