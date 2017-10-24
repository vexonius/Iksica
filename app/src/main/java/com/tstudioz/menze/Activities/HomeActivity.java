package com.tstudioz.menze.Activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.tstudioz.menze.Fragments.CardFragment;
import com.tstudioz.menze.Fragments.ProfileFragment;
import com.tstudioz.menze.Fragments.TransactionsFragment;
import com.tstudioz.menze.R;


public class HomeActivity extends AppCompatActivity {

    AHBottomNavigation bNavigation;


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
                break;
            case 1:
                CardFragment cf = new CardFragment();
                ft.add(R.id.main_frame, cf);
                ft.addToBackStack(null);
                ft.commit();
                getSupportActionBar().setTitle("Iksica");
                break;
            case 2:
                ProfileFragment pf = new ProfileFragment();
                ft.add(R.id.main_frame, pf);
                ft.addToBackStack(null);
                ft.commit();
                getSupportActionBar().setTitle("Korisnik");
                break;
        }
    }



    @Override
    public void onStop(){
        super.onStop();
    }
}
