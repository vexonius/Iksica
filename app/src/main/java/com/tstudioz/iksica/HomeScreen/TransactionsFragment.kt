package com.tstudioz.iksica.HomeScreen

import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.db.williamchart.view.ImplementsAlphaChart
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tstudioz.iksica.Adapter.AdapterTransactions
import com.tstudioz.iksica.Data.Models.Transaction
import com.tstudioz.iksica.R
import com.tstudioz.iksica.Utils.DetailClickListener
import kotlinx.android.synthetic.main.transactions_layout.*
import timber.log.Timber

@ImplementsAlphaChart
class TransactionsFragment : Fragment(), DetailClickListener {

    private var viewmodel: MainViewModel? = null

    companion object {
        fun newInstance(): TransactionsFragment {
            return TransactionsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedBundleInstance: Bundle?): View? {
        val view = inflater.inflate(R.layout.transactions_layout, parent, false)

        viewmodel = ViewModelProvider(activity!!)[MainViewModel::class.java]



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclertransactions.layoutManager = LinearLayoutManager(recyclertransactions.context, LinearLayoutManager.VERTICAL, false)
        val adapter = AdapterTransactions(null, this)
        recyclertransactions.adapter = adapter

        viewmodel?.getUserTransactions()?.observe(viewLifecycleOwner, Observer {
            it?.let {
                (recyclertransactions.adapter as AdapterTransactions).updateItems(it)
            }
        })

        viewmodel?.getTransactionDataMapped()?.observe(viewLifecycleOwner, Observer {
            it?.let {
                chart.animate(it)
            }
        })

        viewmodel?.isTransactionsLayoutRefreshing()?.observe(viewLifecycleOwner, Observer {
            it?.let {
                trans_swipe_layout.isRefreshing = it
            }
        })


    }

    fun showBottomSheetDetail(){
        val btmSheet = BottomSheetDialog(context!!)
        btmSheet.setTitle("Račun")
        btmSheet.show()
    }

    override fun onClicked(position: Int, transaction: Transaction) {
        viewmodel?.getTransactiondetails(transaction.linkOfReceipt)
        Timber.d("CLICKED ITEM NUMBER ${position}")
    }


}