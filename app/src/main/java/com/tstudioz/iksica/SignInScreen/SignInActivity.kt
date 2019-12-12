package com.tstudioz.iksica.SignInScreen

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.iksica.Activities.HomeActivity
import com.tstudioz.iksica.Data.Models.PaperUser
import com.tstudioz.iksica.Data.Models.User
import com.tstudioz.iksica.R
import kotlinx.android.synthetic.main.sign_in_layout.*


class SignInActivity : AppCompatActivity() {


    private var snack: Snackbar? = null
    private var animationDrawable: AnimationDrawable? = null
    private var viewmodel: SignInViewModel? = null


    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)

        supportActionBar?.hide()
        setContentView(R.layout.sign_in_layout)

        viewmodel = ViewModelProvider(this)[SignInViewModel::class.java]

        startAnimation()
        checkIfIsUserLogged()
    }

    override fun onResume() {
        super.onResume()

        animationDrawable?.start()

        sign_in_button.setOnClickListener {
            viewmodel?.insertUserData(parseInputFields())
            sign_in_button.visibility = View.INVISIBLE
            progressBarLoading.visibility = View.VISIBLE
        }

    }

    private fun parseInputFields(): PaperUser {
        val user = PaperUser(1, sign_in_username.text.toString(), sign_in_password.text.toString())
        return user
    }

    private fun checkIfIsUserLogged() {
        viewmodel?.isUserLoggedAlready()?.observe(this, Observer {
            it?.let {
                if (it) startActivity(Intent(this, HomeActivity::class.java))
            }
        })
    }

    fun startAnimation() {
        animationDrawable = sign_in_relative?.background as AnimationDrawable

        animationDrawable?.setEnterFadeDuration(1500)
        animationDrawable?.setExitFadeDuration(1500)
    }


    override fun onStop() {
        super.onStop()
        animationDrawable?.stop()
    }

    fun showErrorSnack(message: String) {
        snack = Snackbar.make(coord_sign_in, message, Snackbar.LENGTH_LONG)
        snack?.show()
    }


}
