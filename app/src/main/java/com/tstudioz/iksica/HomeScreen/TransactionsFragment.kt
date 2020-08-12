package com.tstudioz.iksica.HomeScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.tstudioz.iksica.Adapter.AdapterTransactions
import com.tstudioz.iksica.Data.Models.Transaction
import com.tstudioz.iksica.R
import com.tstudioz.iksica.Utils.BottomSheetTransactionDetails
import com.tstudioz.iksica.Utils.DetailClickListener
import kotlinx.android.synthetic.main.transactions_layout.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


class TransactionsFragment : Fragment(), DetailClickListener {

    private val viewmodel: MainViewModel by sharedViewModel()

    companion object {
        private const val BTM_SHT_FRAGMENT_TAG = "bottomsheet"

        fun newInstance(): TransactionsFragment {
            return TransactionsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedBundleInstance: Bundle?): View? {
        return inflater.inflate(R.layout.transactions_layout, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AdapterTransactions(null, this)

        recyclertransactions.layoutManager =
                LinearLayoutManager(recyclertransactions.context, LinearLayoutManager.VERTICAL, false)
        recyclertransactions.adapter = adapter

        viewmodel.getUserTransactions()?.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.updateItems(it)
            }
        })

        viewmodel.getTransactionDataMapped().observe(viewLifecycleOwner, Observer {
            it?.let {
                chart.animate(it)
            }
        })

        viewmodel.isTransactionsLayoutRefreshing().observe(viewLifecycleOwner, Observer {
            it?.let {
                trans_swipe_layout.isRefreshing = it
            }
        })

        trans_swipe_layout.setOnRefreshListener {
            viewmodel.loginUser()
        }

    }

    private fun showBottomSheetDetail() {
        val btmSheet = BottomSheetTransactionDetails.newInstance()
        btmSheet.show(childFragmentManager, BTM_SHT_FRAGMENT_TAG)
    }

    override fun onClicked(transaction: Transaction) {
        viewmodel.updateCurrentTransactionDetails(transaction)
        showBottomSheetDetail()
    }

}