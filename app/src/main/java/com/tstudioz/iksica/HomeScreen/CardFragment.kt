package com.tstudioz.iksica.HomeScreen

import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.iksica.R
import kotlinx.android.synthetic.main.iksica_layout.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


class CardFragment : Fragment() {

    private var snack: Snackbar? = null
    private var animationDrawable: AnimationDrawable? = null
    private val viewModel: MainViewModel by sharedViewModel()

    companion object {
        fun newInstance(): CardFragment {
            return CardFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.iksica_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animationDrawable = iksica_card_layout.background as AnimationDrawable
        animationDrawable?.setEnterFadeDuration(1500)
        animationDrawable?.setExitFadeDuration(1100)

        root_refreshing.setOnRefreshListener {
            viewModel.loginUser()
        }

        viewModel.isLayoutRefreshing().observe(viewLifecycleOwner, Observer {
            root_refreshing.isRefreshing = it == true
        })
    }

    override fun onStart() {
        super.onStart()
        loadAds()
    }

    override fun onResume() {
        super.onResume()
        animationDrawable?.start()

        showUserCard()

        viewModel.getErrors()?.observe(viewLifecycleOwner, Observer {
            it?.let { showErrorSnack(it) }
        })
    }

    private fun showUserCard() {
        progressBar.visibility = View.INVISIBLE
        iksica_card_layout.visibility = View.VISIBLE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            potroseno_cardview.translationZ = 8f
        }

        viewModel.getUserData()?.observe(viewLifecycleOwner, Observer {
            it?.let {
                progressBar.visibility = View.INVISIBLE
                iksica_card_layout.visibility = View.VISIBLE
                user_name.text = it.name
                card_number.text = it.cardNumber
                pare.text = it.subventionAmount
                potroseno_danas_value.text = it.spentTodayAmount
            }
        })
    }

    private fun showErrorSnack(message: String) {
        snack = Snackbar.make(root_refreshing, message, Snackbar.LENGTH_INDEFINITE)
        snack?.setAction(getString(R.string.retry)) {
            viewModel.loginUser()
        }
        snack?.show()
    }

    private fun loadAds() {
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun onStop() {
        super.onStop()
        animationDrawable?.stop()
    }

}
