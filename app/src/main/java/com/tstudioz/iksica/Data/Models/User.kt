package com.tstudioz.iksica.Data.Models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by amarthus on 10-Oct-17.
 */

open class User : RealmObject() {

    @PrimaryKey
    var id: Int = 0

    var uMail: String? = null
    var uPassword: String? = null
    var uName: String? = null
    var srcLink: String? = null
    var cardNumber: String? = null
    var rightsLevel: String? = null
    var rightFrom: String? = null
    var rightTo: String? = null
    var uni: String? = null
    var currentSubvention: String? = null
    var spentToday: String? = null
    var oib: Long = 0
    var jmbag: Long = 0

    fun setuMail(uMail: String) {
        this.uMail = uMail
    }

    fun setuPassword(uPassword: String) {
        this.uPassword = uPassword
    }

    fun setuName(uName: String) {
        this.uName = uName
    }

    fun getuName(): String? {
        return uName
    }

    fun getuMail(): String? {
        return uMail
    }

    fun getuPassword(): String? {
        return uPassword
    }
}
