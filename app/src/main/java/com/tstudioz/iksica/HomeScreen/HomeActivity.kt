package com.tstudioz.iksica.HomeScreen

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.iksica.R

class HomeActivity : AppCompatActivity() {

    private var bNavigation: AHBottomNavigation? = null
    private var back_pressed: Long = 0
    private var snack: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Iksica"

        setContentView(R.layout.activity_home)

        inicijalizacijaBottomNavigation()
        inicijalizacijaIksicaFragmenta()
    }

    fun inicijalizacijaBottomNavigation() {

        bNavigation = findViewById<View>(R.id.bottom_navigation) as AHBottomNavigation

        val homeItem = AHBottomNavigationItem("Iksica", R.drawable.ic_card, R.color.icon_inactive)
        val transItem = AHBottomNavigationItem("Transakcije", R.drawable.ic_receipt, R.color.icon_inactive)
        val profileItem = AHBottomNavigationItem("Korisnik", R.drawable.ic_user, R.color.icon_inactive)

        bNavigation?.apply {
            addItems(listOf(transItem, homeItem, profileItem))

            defaultBackgroundColor = resources.getColor(R.color.white)
            accentColor = resources.getColor(R.color.colorAccent)
            inactiveColor = resources.getColor(R.color.icon_inactive)
            titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
            isBehaviorTranslationEnabled = false
            currentItem = 1
        }

        bNavigation?.setOnTabSelectedListener { position: Int, wasSelected: Boolean ->
            switchFragment(position)
            true
        }
    }

    fun inicijalizacijaIksicaFragmenta() {
        val ft = supportFragmentManager.beginTransaction()
        val cf = CardFragment.newInstance()
        ft.add(R.id.main_frame, cf)
        ft.addToBackStack(null)
        ft.commit()
    }

    fun switchFragment(position: Int) {
        val ft = supportFragmentManager.beginTransaction()

        when (position) {
            0 -> {
                val tf = TransactionsFragment.newInstance()
                ft.add(R.id.main_frame, tf)
                ft.addToBackStack(null)
                ft.commit()

                supportActionBar?.setTitle("Transakcije")
                supportActionBar?.elevation = 0f
            }
            1 -> {
                val cf = CardFragment.newInstance()
                ft.add(R.id.main_frame, cf)
                ft.addToBackStack(null)
                ft.commit()

                supportActionBar?.setTitle("Iksica")
                supportActionBar?.elevation = 8f
            }
            2 -> {
                val pf = ProfileFragment()
                ft.add(R.id.main_frame, pf)
                ft.addToBackStack(null)
                ft.commit()

                supportActionBar?.setTitle("")
                supportActionBar?.elevation = 0f
            }
        }
    }

    override fun onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            finish()
            return
        }
        showExitSnack()
        back_pressed = System.currentTimeMillis()
    }

    fun showExitSnack() {
        snack = Snackbar.make(findViewById(R.id.relative_home), "Ponovno pritisnite nazad za izaći", Snackbar.LENGTH_LONG)
        snack?.show()
    }
}