package com.tstudioz.iksica.Data

import androidx.lifecycle.MutableLiveData
import com.tstudioz.iksica.Data.Models.PaperUser
import com.tstudioz.iksica.Data.Models.Transaction
import com.tstudioz.iksica.Data.Models.User
import com.tstudioz.iksica.Utils.DataParser
import com.tstudioz.iksica.Utils.NetworkService
import timber.log.Timber

class Repository {

    val dataParser: DataParser = DataParser()
    val service: NetworkService = NetworkService()
    val dbHelper: DatabaseHelper = DatabaseHelper.instance!!
    var token: String? = null
    var authToken: String? = null
    var loginToken: String? = null
    var responseToken: String? = null
    val isUserLogged: MutableLiveData<Boolean> = MutableLiveData()
    var userdata: MutableLiveData<PaperUser> = MutableLiveData()

    init {
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

    fun scrapeuserTransactions(data : PaperUser) : ArrayList<Transaction>{
        Timber.d("Transactions...")
        return dataParser.parseUserTransactions(service.getUserTransactions(data.oib, data.jmbag))
    }

    fun loadUserFromDb() {
        val user: PaperUser? = dbHelper.readUserFromPaper()
      //  Timber.d("Loading user: ${user?.id}")
        user?.let {
            userdata.value = it
        }
    }

    fun checkIsUserLogged(): MutableLiveData<Boolean> {
        // todo sharedprefs impl
        isUserLogged.value = false
        return isUserLogged
    }

    fun getUserData(): MutableLiveData<PaperUser> {
        return userdata
    }

    fun insertUser(user: PaperUser?) {
        if (user == null) return

        dbHelper.insertUserInPaper(user)
        loadUserFromDbAsync()
    }

    fun deleteUser() {
        dbHelper.deleteUserFromPaper()
    }

    fun checkIfUserIsAlreadyLogged() {
        isUserLogged.value = dbHelper.readBoolInSharedPrefs("user_logged")
    }

    fun getUserAlreadyLogged(): MutableLiveData<Boolean> {
        return isUserLogged
    }

    fun loginUser(): String? {
        if (responseToken == null) {
            Timber.d("Atempting to login...")
            token = dataParser.parseSAMLToken(service.fetchSAMLToken())
            authToken = dataParser.parseAuthToken(service.getAuthState(token))
            loginToken = dataParser.parseLoginToken(service.postAuthState(userdata.value?.mail, userdata.value?.password, authToken))
            responseToken = dataParser.parseResponseToken(service.getResponseToken(loginToken))
            service.postResponseToken(responseToken)
        }

        return responseToken ?: throw NullPointerException("User cannot be null")
    }

    fun loadUserFromDbAsync() {
        val user: PaperUser? = dbHelper.readUserFromPaper()
       // Timber.d("Loading user: ${user?.id}")
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


}