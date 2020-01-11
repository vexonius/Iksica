package com.tstudioz.iksica.Data.Models

/**
 * Created by etino7 on 23-Oct-17.
 */
data class Transaction(val restourant: String = "",
                       val date: String = "",
                       val time: String = "",
                       val amount: String = "",
                       val subvention: String = "",
                       val authorization: String = "",
                       val linkOfReceipt: String = "")