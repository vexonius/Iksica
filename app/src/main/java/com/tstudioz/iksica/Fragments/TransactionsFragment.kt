package com.tstudioz.iksica.Fragments

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
import com.tstudioz.iksica.CardScreen.MainViewModel
import com.tstudioz.iksica.R
import kotlinx.android.synthetic.main.transactions_layout.*

@ImplementsAlphaChart
class TransactionsFragment : Fragment() {

    private var viewmodel: MainViewModel? = null

    companion object {
        fun newInstance(): TransactionsFragment {
            return TransactionsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedBundleInstance: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.transactions_layout, parent, false)

        viewmodel = ViewModelProvider(activity!!)[MainViewModel::class.java]



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclertransactions.layoutManager = LinearLayoutManager(recyclertransactions.context, LinearLayoutManager.VERTICAL, false)
        val adapter = AdapterTransactions(null)
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


    }


}