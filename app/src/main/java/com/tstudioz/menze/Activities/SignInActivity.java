package com.tstudioz.menze.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.tstudioz.menze.Fragments.CardFragment;
import com.tstudioz.menze.Model.User;
import com.tstudioz.menze.R;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by etino7 on 08-Oct-17.
 */

public class SignInActivity extends AppCompatActivity {

    Button signInButton;
    EditText editEmail, editPass;
    public Realm realm;
    private Snackbar snack;


    @Override
    protected void onCreate(Bundle savedInstanceBundle){
       super.onCreate(savedInstanceBundle);
        getSupportActionBar().hide();
        setContentView(R.layout.sign_in_layout);

        realm = Realm.getDefaultInstance();

        checkUser();

    }

    public  void checkUser(){
        SharedPreferences sp = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
        Boolean prijavljen = sp.getBoolean("korisnik_prijavljen", false);

        if (prijavljen==true){
            User u = realm.where(User.class).findFirst();
            if(u!=null) {
                startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                finish();
            }else {
                registerUser();
            }
        } else {
            registerUser();
        }
    }

    public void registerUser(){

        signInButton = (Button)findViewById(R.id.sign_in_button);
        editEmail=(EditText)findViewById(R.id.sign_in_username);
        editPass=(EditText)findViewById(R.id.sign_in_password);

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

                            SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("korisnik_prijavljen", true);
                            editor.commit();

                            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                            finish();
                        }
                    });
                } finally {
                    realm.close();
                }
            }
        });
    }

    public void showNetworkErrorSnack(){
        snack = Snackbar.make(findViewById(R.id.sign_in_relative), "Niste povezani", Snackbar.LENGTH_INDEFINITE);
        snack.setAction("PONOVI", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUser();
            }
        });
        snack.show();
    }

}
