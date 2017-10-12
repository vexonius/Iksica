package com.tstudioz.menze.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.tstudioz.menze.Adapter.AdapterHome;
import com.tstudioz.menze.Fragments.CardFragment;
import com.tstudioz.menze.Fragments.SignInFragment;
import com.tstudioz.menze.Model.MenzaItem;
import com.tstudioz.menze.R;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private List<MenzaItem> menze;
    private RelativeLayout underFrameLayout;
    private Button prijavaButton;
    private Snackbar snack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Prikazivanje liste menza
        inicijalizacijaListe();
        showHomeRecycler();


        checkUser();
    }

    public void showHomeRecycler(){
        RecyclerView rv = (RecyclerView)findViewById(R.id.recycler_home);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, LinearLayoutManager.VERTICAL, false));
        AdapterHome ah = new AdapterHome(menze);
        rv.setAdapter(ah);
    }

    public  void showUserCard(){

           if (isNetworkAvailable()){
            underFrameLayout = (RelativeLayout)findViewById(R.id.log_in_relative);
            underFrameLayout.setVisibility(View.INVISIBLE);

            CardFragment cf = new CardFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.iksica_frame, cf);
            ft.addToBackStack(null);
            ft.commit();
        }else {
            showNetworkErrorSnack();
        }

    }

    public  void showLoginLayout(){
        underFrameLayout = (RelativeLayout)findViewById(R.id.log_in_relative);
        underFrameLayout.setVisibility(View.VISIBLE);

        prijavaButton = (Button)findViewById(R.id.login_button);
        prijavaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                underFrameLayout = (RelativeLayout)findViewById(R.id.log_in_relative);
                underFrameLayout.setVisibility(View.INVISIBLE);

                SignInFragment sf = new SignInFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.iksica_frame, sf);
                ft.commit();
            }
        });
    }

    public  void checkUser(){
        SharedPreferences sp = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
        Boolean prijavljen = sp.getBoolean("korisnik_prijavljen", false);

        if (prijavljen==true){
            //Prikazivanje layouta s iksicom
            showUserCard();
        }else {
            showLoginLayout();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public void showNetworkErrorSnack(){
        snack = Snackbar.make(findViewById(R.id.root_relative_home), "Niste povezani", Snackbar.LENGTH_INDEFINITE);
        snack.setAction("PONOVI", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUser();
            }
        });
        snack.show();
    }

    public void inicijalizacijaListe(){
        menze=new ArrayList<>();
        menze.add(new MenzaItem("FESB", "http://www.scst.unist.hr/jelovnik/"));
        menze.add(new MenzaItem("KAMPUS", "http://www.scst.unist.hr/jelovnik/"));
        menze.add(new MenzaItem("EKONOMIJA", "http://www.scst.unist.hr/jelovnik/"));
        menze.add(new MenzaItem("GRAĐEVINA", "http://www.scst.unist.hr/jelovnik/"));
        menze.add(new MenzaItem("MEDICINA", "http://www.scst.unist.hr/jelovnik/"));
        menze.add(new MenzaItem("HOSTEL", "http://www.scst.unist.hr/jelovnik/"));
        //menze.add(new MenzaItem("INDEKS", "http://www.scst.unist.hr/jelovnik/"));
    }

    @Override
    public void onStop(){
        super.onStop();

        if(snack!=null){
            snack.dismiss();
        }
    }
}
