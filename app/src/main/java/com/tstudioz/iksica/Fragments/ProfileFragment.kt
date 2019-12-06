package com.tstudioz.iksica.Fragments

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.tstudioz.iksica.SignInScreen.SignInActivity
import com.tstudioz.iksica.R
import com.tstudioz.iksica.SignInScreen.MainViewModel
import io.realm.Realm
import kotlinx.android.synthetic.main.profile_layout.*

/**
 * Created by etino7 on 18-Oct-17.
 */

class ProfileFragment : Fragment() {

    internal var mRealm: Realm? = null

    var viewModel : MainViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedBundleInstance: Bundle?): View? {
        val view = inflater.inflate(R.layout.profile_layout, parent, false)

        viewModel = ViewModelProvider(activity!!)[MainViewModel::class.java]



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel?.getUserData()?.observe(viewLifecycleOwner, Observer {
            Glide.with(view.context)
                    .load(it?.srcLink)
                    .placeholder(ColorDrawable(ContextCompat.getColor(view.context, R.color.dirty_white)))
                    .into(circularImageView)

            name_surname.setText(it?.getuName())
        })

        showInfoRecyclerView()

    }

    fun showInfoRecyclerView() {
//        val informacije = mRealm!!.where<UserInfoItem>(UserInfoItem::class.java!!).findAll()
/**
        rv!!.setHasFixedSize(true)
        rv!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        val ah = AdapterInfo(informacije)
        rv!!.adapter = ah
    */
        }

    fun signOut() {
        val sp = activity!!.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.remove("korisnik_prijavljen")
        editor.commit()

        mRealm!!.executeTransaction { mRealm!!.deleteAll() }

        activity!!.startActivity(Intent(activity, SignInActivity::class.java))
        if (activity != null)
            activity!!.finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mRealm != null)
            mRealm!!.close()
    }
}
