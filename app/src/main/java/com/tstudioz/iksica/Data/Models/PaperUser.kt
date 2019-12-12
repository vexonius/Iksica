package com.tstudioz.iksica.Data.Models

/**
 * Created by etino7 on 12/11/2019.
 */
data class PaperUser(val id: Int = 0,
                     val mail: String = "",
                     val password: String = "",
                     val name: String = "",
                     val cardNumber: String = "",
                     val subventionAmount: String = "",
                     val spentTodayAmount: String = "",
                     val rightsLevel: Int = 0,
                     val rightsFrom: String = "",
                     val rightsTo: String = "",
                     val university: String = "",
                     val avatarLink: String = "",
                     val oib: Long = 0,
                     val jmbag: Long = 0) {
}