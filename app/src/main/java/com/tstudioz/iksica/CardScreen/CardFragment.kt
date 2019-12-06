package com.tstudioz.iksica.CardScreen

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.iksica.R
import com.tstudioz.iksica.SignInScreen.MainViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.iksica_layout.*


class CardFragment : Fragment() {

    private var snack: Snackbar? = null
    private var animationDrawable: AnimationDrawable? = null
    private var viewmodel: MainViewModel? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.iksica_layout,
                container, false)

        viewmodel = ViewModelProvider(activity!!)[MainViewModel::class.java]

        viewmodel?.loginUser()


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animationDrawable = iksica_card_layout.getBackground() as AnimationDrawable
        animationDrawable?.setEnterFadeDuration(2000)
        animationDrawable?.setExitFadeDuration(2000)

        root_refreshing.setOnRefreshListener {
            viewmodel?.loginUser()
        }

        viewmodel?.isLayoutRefreshing()?.observe(viewLifecycleOwner, Observer {
            root_refreshing.isRefreshing = it == true
        })



    }

    override fun onStart() {
        super.onStart()

        showUserCard()
        // loadAds()
    }

    fun showUserCard() {

        progressBar.setVisibility(View.INVISIBLE)
        iksica_card_layout.setVisibility(View.VISIBLE)

        viewmodel?.getUserData()?.observe(viewLifecycleOwner, Observer {
            progressBar.setVisibility(View.INVISIBLE)
            iksica_card_layout.setVisibility(View.VISIBLE)
            user_name.setText(it.getuName())
            card_number.setText(it.cardNumber)
            pare.setText(it.currentSubvention)
            potroseno_danas_value.setText(it.spentToday)
        })

    }

    fun showNetworkErrorSnack() {
        snack = Snackbar.make(activity!!.findViewById(R.id.relative_home), "Niste povezani", Snackbar.LENGTH_INDEFINITE)
        snack?.setAction("PONOVI") { showUserCard() }
        snack?.show()
    }

    fun showErrorSnack(message: String) {
        if (relative_home != null) {
            snack = Snackbar.make(relative_home, message, Snackbar.LENGTH_INDEFINITE)
            snack?.show()
        }
    }


    fun loadAds() {
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }


    override fun onResume() {
        super.onResume()
        animationDrawable?.start()
    }

    override fun onStop() {
        super.onStop()
        animationDrawable?.stop()
    }

}
