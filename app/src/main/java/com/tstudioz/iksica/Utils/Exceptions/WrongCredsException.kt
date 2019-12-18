package com.tstudioz.iksica.Utils.Exceptions

import kotlin.Exception


class WrongCredsException : Exception() {
    override val message: String?
        get() = "Došlo je do pogreške prilikom prijave. Provjerite unsene podatke i pokušajte ponovno."
}