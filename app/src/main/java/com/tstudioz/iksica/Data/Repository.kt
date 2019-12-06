package com.tstudioz.iksica.Data

import androidx.lifecycle.MutableLiveData
import com.tstudioz.iksica.Data.Models.User
import com.tstudioz.iksica.Utils.DataParser
import com.tstudioz.iksica.Utils.NetworkService
import com.tstudioz.iksica.Utils.RealmSingleResultLiveData
import timber.log.Timber
import java.lang.NullPointerException

class Repository {

    val mUserData: MutableLiveData<User> = MutableLiveData()
    val dataParser: DataParser = DataParser()
    val service: NetworkService = NetworkService()
    val dbHelper: DatabaseHelper = DatabaseHelper.instance!!
    var token: String? = null
    var authToken: String? = null
    var loginToken: String? = null
    var responseToken: String? = null

    init {
        mUserData.value = loadUserFromDb()
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

    fun loginUser(): String? {
        if (responseToken == null) {
            Timber.d("Atempting to login...")
            val user = mUserData.value
            token = dataParser.parseSAMLToken(service.fetchSAMLToken())
            authToken = dataParser.parseAuthToken(service.getAuthState(token))
            loginToken = dataParser.parseLoginToken(service.postAuthState(user?.uMail, user?.uPassword, authToken))
            responseToken = dataParser.parseResponseToken(service.getResponseToken(loginToken))
            service.postResponseToken(responseToken)
        }

        return responseToken
    }

    fun scrapeUserInfo(): User? {
        Timber.d("Scraping...")
        var userData: User? = null
        try {
            userData = dataParser.parseUserInfo(service.getUserInfo())
        } catch (ex: NullPointerException) {
            Timber.e(ex)
        }

        return userData
    }

    fun loadUserFromDb(): User? {
        Timber.d("Fetching user...")
        return dbHelper.getUserBlocking()
    }

    fun getUserData(): MutableLiveData<User> {
        return mUserData
    }

    fun checkCachedCookies() {
        dataParser.parseUserInfo(service.getUserInfo())
    }

    fun insertUser(user: User) {
        dbHelper.insertOrUpdateUserInfo(user)
    }

    fun getUserDataBlocking(): MutableLiveData<User> {
        return mUserData
    }

    fun insertUserBlocking(user: User) {
        Timber.d("Inserting user...")
        dbHelper.createUserBlocking(user)
    }

    /** fun signOut() {
    val sp = getActivity()!!.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)
    val editor = sp.edit()
    editor.remove("korisnik_prijavljen")
    editor.commit()

    mRealm.executeTransaction(Realm.Transaction { mRealm.deleteAll() })

    getActivity()!!.startActivity(Intent(getActivity(), SignInActivity::class.java))
    getActivity()!!.finish()
    }

     */


}