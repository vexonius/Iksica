package com.tstudioz.iksica.Data.Models

/**
 * Created by etino7 on 1/10/2020.
 */
data class TransactionDetails(var total: String, var subventionTotal: String, var items: ArrayList<TransactionItem>?) {
}