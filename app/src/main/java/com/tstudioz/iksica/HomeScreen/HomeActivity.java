package com.tstudioz.iksica.HomeScreen;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.tstudioz.iksica.R;


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

        AHBottomNavigationItem homeItem = new AHBottomNavigationItem("Iksica", R.drawable.ic_card, R.color.icon_inactive);
        AHBottomNavigationItem transItem = new AHBottomNavigationItem("Transakcije", R.drawable.ic_receipt, R.color.icon_inactive);
        AHBottomNavigationItem profileItem = new AHBottomNavigationItem("Korisnik", R.drawable.ic_user, R.color.icon_inactive);

        bNavigation.addItem(transItem);
        bNavigation.addItem(homeItem);
        bNavigation.addItem(profileItem);

        bNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.white));
        bNavigation.setAccentColor(getResources().getColor(R.color.colorAccent));
        bNavigation.setInactiveColor(getResources().getColor(R.color.icon_inactive));
        bNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bNavigation.setBehaviorTranslationEnabled(false);

        bNavigation.setCurrentItem(1);

        bNavigation.setOnTabSelectedListener((position, wasSelected) -> {
            switchFragment(position);
            return true;
        });
    }

    public void inicijalizacijaIksicaFragmenta(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        CardFragment cf = CardFragment.Companion.newInstance();
        ft.add(R.id.main_frame, cf);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchFragment(int position){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (position){
            case 0:
                TransactionsFragment tf = TransactionsFragment.Companion.newInstance();
                ft.add(R.id.main_frame, tf);
                ft.addToBackStack(null);
                ft.commit();
                getSupportActionBar().setTitle("Transakcije");
                getSupportActionBar().setElevation(0);
                break;
            case 1:
                CardFragment cf = CardFragment.Companion.newInstance();
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
                getSupportActionBar().setTitle("");
                getSupportActionBar().setElevation(0);
                break;
        }
    }


    @Override
    public void onBackPressed(){

        if (back_pressed + 2000 > System.currentTimeMillis()) {
            finish();
            return;
        }

        showExitSnack();
        back_pressed = System.currentTimeMillis();
    }

    public void showExitSnack(){
        snack = Snackbar.make(findViewById(R.id.relative_home), "Ponovno pritisnite nazadan za izaći", Snackbar.LENGTH_LONG);
        snack.show();
    }
}

