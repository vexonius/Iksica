package com.tstudioz.iksica.HomeScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.db.williamchart.view.ImplementsAlphaChart
import com.tstudioz.iksica.Adapter.AdapterTransactions
import com.tstudioz.iksica.Data.Models.Transaction
import com.tstudioz.iksica.R
import com.tstudioz.iksica.Utils.BottomSheetTransactionDetails
import com.tstudioz.iksica.Utils.DetailClickListener
import kotlinx.android.synthetic.main.transactions_layout.*

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
                adapter.updateItems(it)
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

        trans_swipe_layout.setOnRefreshListener {
            viewmodel?.loginUser()
        }

    }

    fun showBottomSheetDetail(){
        val btmSheet = BottomSheetTransactionDetails.newInstance()
        btmSheet.show(fragmentManager!!, "bottomsheet")
    }

    override fun onClicked(transaction: Transaction) {
        viewmodel?.updateCurrentTransactionDetails(transaction)
        showBottomSheetDetail()
    }


}