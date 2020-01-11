package com.tstudioz.iksica.Utils

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tstudioz.iksica.Adapter.TransactionDetailsAdapter
import com.tstudioz.iksica.Data.Models.TransactionDetails
import com.tstudioz.iksica.Data.Models.TransactionItem
import com.tstudioz.iksica.HomeScreen.MainViewModel
import com.tstudioz.iksica.R


/**
 * Created by etino7 on 1/11/2020.
 */
class BottomSheetTransactionDetails : BottomSheetDialogFragment() {
    var viewModel: MainViewModel? = null


    companion object {
        public fun newInstance(): BottomSheetTransactionDetails {
            return BottomSheetTransactionDetails()

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(activity!!)[MainViewModel::class.java]
    }


    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView: View = View.inflate(context, R.layout.transaction_details_layout, null)
        dialog.setContentView(contentView)

        val location = contentView.findViewById(R.id.details_location) as TextView
        val time = contentView.findViewById(R.id.details_time) as TextView
        val total = contentView.findViewById(R.id.total) as TextView
        val recycler = contentView.findViewById(R.id.details_recycler) as RecyclerView

        recycler.layoutManager = LinearLayoutManager(contentView.context)
        val adapter = TransactionDetailsAdapter(ArrayList<TransactionItem>())
        recycler.adapter = adapter

        viewModel?.getCurrentTransaction()?.observe(this, Observer {transaction ->
            transaction?.let {
                location.text = it.restourant
                time.text = "${it.date}, ${it.time}"
                total.text = "${it.subvention} kn"
            }
        })

        viewModel?.getCurrentTransactionItems()?.observe(this, Observer {transactionDetails ->
            transactionDetails?.let {
                total.text = "${it.subventionTotal} kn"
                adapter.updateItems(transactionDetails.items)
            }
        })


    }

}