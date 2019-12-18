package com.tstudioz.iksica.Utils.Exceptions

/**
 * Created by etino7 on 12/18/2019.
 */
class EmptyInputFields : Exception() {
    override val message: String?
        get() = "Polja za unos ne smiju biti prazna. Provjerite unesene podatke"
}