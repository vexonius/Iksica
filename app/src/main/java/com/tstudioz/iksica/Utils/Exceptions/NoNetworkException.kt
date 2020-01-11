package com.tstudioz.iksica.Utils.Exceptions

import java.io.IOException

class NoNetworkException : IOException() {
    override val message: String?
        get() = "Niste povezani. \nProvjerite internetsku vezu i pokušajte ponovno"
}