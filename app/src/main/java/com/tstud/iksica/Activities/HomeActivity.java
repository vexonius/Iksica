package com.tstud.iksica.Activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.tstud.iksica.Fragments.CardFragment;
import com.tstud.iksica.Fragments.ProfileFragment;
import com.tstud.iksica.Fragments.TransactionsFragment;
import com.tstud.iksica.R;


public class HomeActivity extends AppCompatActivity {

    public AHBottomNavigation bNavigation;
    public long back_pressed;
    public Snackbar snack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Iksica");
        setContentView(R.layout.activity_home);

        inicijalizacijaBottomNavigation();
        inicijalizacijaIksicaFragmenta();
    }

    public void inicijalizacijaBottomNavigation(){
        bNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem homeItem = new AHBottomNavigationItem("Iksica", R.drawable.kartica_icon, R.color.icon_inactive);
        AHBottomNavigationItem transItem = new AHBottomNavigationItem("Transakcije", R.drawable.racun_icon, R.color.icon_inactive);
        AHBottomNavigationItem profileItem = new AHBottomNavigationItem("Korisnik", R.drawable.korisnik_icon, R.color.icon_inactive);

        bNavigation.addItem(transItem);
        bNavigation.addItem(homeItem);
        bNavigation.addItem(profileItem);

        bNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.white));
        bNavigation.setAccentColor(getResources().getColor(R.color.light_grey));
        bNavigation.setInactiveColor(getResources().getColor(R.color.icon_inactive));
        bNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bNavigation.setBehaviorTranslationEnabled(false);

        bNavigation.setCurrentItem(1);

        bNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switchFragment(position);
                return true;
            }
        });
    }

    public void inicijalizacijaIksicaFragmenta(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        CardFragment cf = new CardFragment();
        ft.add(R.id.main_frame, cf);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchFragment(int position){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (position){
            case 0:
                TransactionsFragment tf = new TransactionsFragment();
                ft.add(R.id.main_frame, tf);
                ft.addToBackStack(null);
                ft.commit();
                getSupportActionBar().setTitle("Transakcije");
                getSupportActionBar().setElevation(0);
                break;
            case 1:
                CardFragment cf = new CardFragment();
                ft.add(R.id.main_frame, cf);
                ft.addToBackStack(null);
                ft.commit();
                getSupportActionBar().setTitle("Iksica");
                getSupportActionBar().setElevation(8);
                break;
            case 2:
                ProfileFragment pf = new ProfileFragment();
                ft.add(R.id.main_frame, pf);
                ft.addToBackStack(null);
                ft.commit();
                getSupportActionBar().setTitle("Korisnik");
                getSupportActionBar().setElevation(0);
                break;
        }
    }



    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onBackPressed(){

        if (back_pressed + 2000 > System.currentTimeMillis()){
            finish();
        } else {
            showExitSnack();
        }
        back_pressed= System.currentTimeMillis();
    }

    public void showExitSnack(){
        snack = Snackbar.make(findViewById(R.id.relative_home), "Ponovno pritisnite nazadan za izaći", Snackbar.LENGTH_LONG);
        snack.show();
    }
}

