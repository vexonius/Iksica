package com.tstudioz.iksica.Data

import android.content.Context
import android.util.Log

import com.orhanobut.hawk.Hawk
import com.tstudioz.iksica.Data.Models.User
import com.tstudioz.iksica.Utils.RealmSingleResultLiveData
import com.tstudioz.iksica.Utils.RealmUtil
import com.vicpin.krealmextensions.equalToValue
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save

import java.io.File
import java.security.SecureRandom

import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber

/**
 * Created by etino7 on 12/10/2019.
 */
class DatabaseHelper private constructor(context: Context) {
    private val defaultRealmConfig: RealmConfiguration
    private var realm: Realm? = null

    init {
        defaultRealmConfig = RealmConfiguration.Builder()
                .name("encrypted.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .encryptionKey(getRealmKey(context))
                .build()

        Realm.setDefaultConfiguration(defaultRealmConfig)
        realm = Realm.getDefaultInstance()

        checkIfOldRealmExists(context)
    }


    fun insertOrUpdateUserInfo(user: User) {
       User().queryFirst { equalToValue("id", 1)}?.let {
            it.uName = user.uName
            it.cardNumber = user.cardNumber
            it.currentSubvention = user.currentSubvention
            it.jmbag = user.jmbag
            it.oib = user.oib
            it.rightFrom = user.rightFrom
            it.rightTo = user.rightTo
            it.rightsLevel = user.rightsLevel
            it.srcLink = user.srcLink
            it.spentToday = user.spentToday
            it.uni = user.uni
            it.save()
        }

        Timber.d("Updating user ${user.id}")

    }

    fun getUserBlocking(): User? {
        val user : User? = User().queryFirst { equalToValue("id", 1) }
        return user
    }

    fun createUserBlocking(user: User) {
        user.save()
        Timber.d("Realm extensions user ${user.id} successfully saved")
    }

    private fun getRealmKey(context: Context): ByteArray {

        Hawk.init(context).build()

        if (Hawk.contains("masterKey")) {
            return Hawk.get("masterKey")
        }

        val bytes = ByteArray(64)
        SecureRandom().nextBytes(bytes)

        Hawk.put("masterKey", bytes)

        return bytes
    }


    private fun checkIfOldRealmExists(context: Context) {
        val newRealmFile = File(defaultRealmConfig.path)
        if (!newRealmFile.exists()) {
            // Migrate old Realm and delete old
            val old = RealmConfiguration.Builder()
                    .name("myRealm.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build()

            val realm = Realm.getInstance(old)
            realm.writeEncryptedCopyTo(newRealmFile, getRealmKey(context))
            realm.close()
            Realm.deleteRealm(old)
        }
    }

    private fun destroyInstance() {
        realm?.close()

        realm = null
    }

    companion object {

        var instance: DatabaseHelper? = null
            private set

        fun createInstance(context: Context) {
            if (instance == null)
                instance = DatabaseHelper(context)
        }
    }


}
