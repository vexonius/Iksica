package com.tstudioz.iksica.SignInScreen

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.iksica.Activities.HomeActivity
import com.tstudioz.iksica.Data.Models.User
import com.tstudioz.iksica.R
import kotlinx.android.synthetic.main.sign_in_layout.*
import timber.log.Timber


class SignInActivity : AppCompatActivity() {


    private var snack: Snackbar? = null
    private var animationDrawable: AnimationDrawable? = null
    private var viewmodel: MainViewModel? = null


    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)
        supportActionBar?.hide()
        setContentView(R.layout.sign_in_layout)

        animationDrawable = sign_in_relative?.background as AnimationDrawable

        animationDrawable?.setEnterFadeDuration(1500)
        animationDrawable?.setExitFadeDuration(1500)

        viewmodel = ViewModelProvider(this)[MainViewModel::class.java]

        //  startActivity(Intent(this, HomeActivity::class.java))

    }

    override fun onResume() {
        super.onResume()

        animationDrawable?.start()

        viewmodel?.getUserData()?.observe(this, Observer {
            it?.let {
                Timber.d("User in db id: ${it.id}")
                if (it.getuPassword() != null && it.getuMail() != null)
                    startActivity(Intent(this, HomeActivity::class.java))
            }
        })

        //  viewmodel?.isUserLogged()?.observe(this, Observer {
        //      if(it)
        //          startActivity(Intent(this, HomeActivity::class.java))
        //  })

        sign_in_button.setOnClickListener {
            viewmodel?.insertUserData(parseInputFields())
            sign_in_button.visibility = View.INVISIBLE
            progressBarLoading.visibility = View.VISIBLE
          //  viewmodel?.checkUser()
        }

    }

    private fun parseInputFields(): User {
        val user = User()
        user.id = 1
        user.setuMail(sign_in_username.text.toString())
        user.setuPassword(sign_in_password.text.toString())

        return user
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
