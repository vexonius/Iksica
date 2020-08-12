package com.tstudioz.iksica.Data

import androidx.lifecycle.MutableLiveData
import com.tstudioz.iksica.Data.Models.PaperUser
import com.tstudioz.iksica.Data.Models.Transaction
import com.tstudioz.iksica.Data.Models.TransactionDetails
import com.tstudioz.iksica.Utils.DataParser
import com.tstudioz.iksica.Utils.Exceptions.WrongCredsException
import com.tstudioz.iksica.Utils.NetworkService
import timber.log.Timber

class Repository constructor(private val service: NetworkService,
                             private val dbHelper: DatabaseHelper,
                             private val dataParser: DataParser) {

    companion object {
        private const val USER_LOGGED_IN = "user_logged"
    }

    private var token: String? = null
    private var authToken: String? = null
    private var loginToken: String? = null
    private var responseToken: String? = null

    private val isUserLogged: MutableLiveData<Boolean> = MutableLiveData()
    private val userdata: MutableLiveData<PaperUser> = MutableLiveData()

    private val transactionDetailsData: MutableLiveData<TransactionDetails> = MutableLiveData()

    init {
        loadUserFromDb()
        checkIfUserIsAlreadyLogged()
    }

    fun scrapeUserInfo(): PaperUser {
        Timber.d("Scraping...")
        return dataParser.parseUserInfo(service.getUserInfo())
    }

    fun scrapeUserTransactions(data: PaperUser): ArrayList<Transaction> {
        Timber.d("Transactions...")
        return dataParser.parseUserTransactions(service.getUserTransactions(data.oib))
    }

    private fun loadUserFromDb() {
        val user: PaperUser? = dbHelper.readUserFromPaper()
        Timber.d("Loading user: ${user?.id}")
        user?.let {
            userdata.value = it
        }
    }

    fun getUserData(): MutableLiveData<PaperUser> {
        return userdata
    }

    fun getTransactionDetails(): MutableLiveData<TransactionDetails> {
        return transactionDetailsData
    }

    fun insertUser(user: PaperUser?) {
        if (user == null) return

        dbHelper.insertUserInPaper(user)
        loadUserFromDbAsync()
    }

    private fun deleteUser() {
        userdata.value = null
        dbHelper.deleteUserFromPaper()
    }

    fun logOutUser() {
        dbHelper.writeBoolInSharedPrefs(USER_LOGGED_IN, false)
        isUserLogged.value = false
        dataParser.clearAllTokens()
        service.clearCookies()
        deleteAllRepoTokens()
        deleteUser()
    }

    private fun deleteAllRepoTokens() {
        token = null
        authToken = null
        loginToken = null
        responseToken = null
    }

    fun checkIfUserIsAlreadyLogged() {
        isUserLogged.postValue(dbHelper.readBoolInSharedPrefs(USER_LOGGED_IN))
    }

    fun getUserAlreadyLogged(): MutableLiveData<Boolean> {
        return isUserLogged
    }


    @Throws(WrongCredsException::class)
    fun verifyUserInput(mail: String, psswd: String): Boolean {

        Timber.d("Verifing user...")
        token = dataParser.parseSAMLToken(service.fetchSAMLToken())
        authToken = dataParser.parseAuthToken(service.getAuthState(token))

        loginToken = dataParser.parseLoginToken(service.postAuthState(mail, psswd, authToken))
        if (token == null) return false

        responseToken = dataParser.parseResponseToken(service.getResponseToken(loginToken))
        service.postResponseToken(responseToken)

        return true

    }

    fun loginUser(): String? {
        Timber.d("Atempting to login...")

        if (responseToken == null) {
            token = dataParser.parseSAMLToken(service.fetchSAMLToken())
            authToken = dataParser.parseAuthToken(service.getAuthState(token))

            loginToken = dataParser.parseLoginToken(
                    service.postAuthState(userdata.value?.mail, userdata.value?.password, authToken))
                    ?: throw WrongCredsException()

            responseToken = dataParser.parseResponseToken(service.getResponseToken(loginToken))
            service.postResponseToken(responseToken)
        }

        return responseToken ?: throw WrongCredsException()
    }

    private fun loadUserFromDbAsync() {
        val user: PaperUser? = dbHelper.readUserFromPaper()
        Timber.d("Loading user: ${user?.id}")
        user?.let {
            userdata.postValue(it)
        }
    }

    fun setUserLogged(key: String, value: Boolean) {
        dbHelper.writeBoolInSharedPrefs(key, value)
        checkIfUserIsAlreadyLogged()
    }

    fun updateUserData(freshData: PaperUser) {
        val user: PaperUser? = dbHelper.readUserFromPaper()
        user?.let {
            insertUser(PaperUser(
                    user.id,
                    user.mail,
                    user.password,
                    freshData.name,
                    freshData.cardNumber,
                    freshData.subventionAmount,
                    freshData.spentTodayAmount,
                    freshData.rightsLevel,
                    freshData.rightsFrom,
                    freshData.rightsTo,
                    freshData.university,
                    freshData.avatarLink,
                    freshData.oib,
                    freshData.jmbag))
        }
    }

    fun fetchTransactionDetails(linkOfReceipt: String) {
        val tranDet = dataParser
                .parseTransactionDetails(service.fetchTransactiondetails(linkOfReceipt))

        transactionDetailsData.postValue(tranDet)
        Timber.d(tranDet.subventionTotal)
    }

    fun clearTransactionDetails() {
        transactionDetailsData.postValue(null)
    }

}