package com.tstudioz.iksica.Data

import androidx.lifecycle.MutableLiveData
import com.tstudioz.iksica.Data.Models.PaperUser
import com.tstudioz.iksica.Data.Models.Transaction
import com.tstudioz.iksica.Data.Models.TransactionDetails
import com.tstudioz.iksica.Utils.DataParser
import com.tstudioz.iksica.Utils.NetworkService
import com.tstudioz.iksica.Utils.Exceptions.WrongCredsException
import timber.log.Timber

class Repository {

    val dataParser: DataParser = DataParser()
    val service: NetworkService = NetworkService()
    val dbHelper: DatabaseHelper

    var token: String? = null
    var authToken: String? = null
    var loginToken: String? = null
    var responseToken: String? = null

    val isUserLogged: MutableLiveData<Boolean> = MutableLiveData()
    val userdata: MutableLiveData<PaperUser> = MutableLiveData()

    val transactionDetailsData: MutableLiveData<TransactionDetails> = MutableLiveData()

    init {
        dbHelper = DatabaseHelper.instance!!
        loadUserFromDb()
        checkIfUserIsAlreadyLogged()
    }

    companion object {

        private var INSTANCE: Repository? = null

        @JvmStatic
        fun getInstance(): Repository {
            return INSTANCE ?: Repository().apply { INSTANCE = this }
        }


        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }

    fun scrapeUserInfo(): PaperUser {
        Timber.d("Scraping...")
        return dataParser.parseUserInfo(service.getUserInfo())
    }

    fun scrapeUserTransactions(data: PaperUser): ArrayList<Transaction> {
        Timber.d("Transactions...")
        return dataParser.parseUserTransactions(service.getUserTransactions(data.oib))
    }

    fun loadUserFromDb() {
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

    fun deleteUser() {
        userdata.value = null
        dbHelper.deleteUserFromPaper()
    }

    fun logOutUser() {
        dbHelper.writeBoolInSharedPrefs("user_logged", false)
        isUserLogged.value = false
        dataParser.clearAllTokens()
        deleteAllRepoTokens()
        deleteUser()
    }

    fun deleteAllRepoTokens() {
        token = null
        authToken = null
        loginToken = null
        responseToken = null
    }

    fun checkIfUserIsAlreadyLogged() {
        isUserLogged.postValue(dbHelper.readBoolInSharedPrefs("user_logged"))
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
        if(token==null) return false

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

        return responseToken
                ?: throw WrongCredsException()
    }

    fun loadUserFromDbAsync() {
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

    fun updateUserData(freshdata: PaperUser) {
        val user: PaperUser? = dbHelper.readUserFromPaper()
        user?.let {
            insertUser(
                    PaperUser(
                            user.id,
                            user.mail,
                            user.password,
                            freshdata.name,
                            freshdata.cardNumber,
                            freshdata.subventionAmount,
                            freshdata.spentTodayAmount,
                            freshdata.rightsLevel,
                            freshdata.rightsFrom,
                            freshdata.rightsTo,
                            freshdata.university,
                            freshdata.avatarLink,
                            freshdata.oib,
                            freshdata.jmbag))
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