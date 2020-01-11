package com.tstudioz.iksica.Data

import android.content.Context
import android.content.SharedPreferences
import com.orhanobut.hawk.Hawk
import com.tstudioz.iksica.Data.Models.PaperUser
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.security.SecureRandom

/**
 * Created by etino7 on 12/10/2019.
 */
class DatabaseHelper private constructor(context: Context) {
    private val defaultRealmConfig: RealmConfiguration
    private val prefs: SharedPreferences

    init {
        defaultRealmConfig = RealmConfiguration.Builder()
                .name("encrypted.realm")
                .schemaVersion(2)
                .deleteRealmIfMigrationNeeded()
                .encryptionKey(getRealmKey())
                .build()

        Realm.setDefaultConfiguration(defaultRealmConfig)
        prefs = context.getSharedPreferences("DEFAULT_PREFS", Context.MODE_PRIVATE)

        checkIfOldRealmExists(context)
    }

    companion object {
        var instance: DatabaseHelper? = null
            private set

        fun createInstance(context: Context) {
            if (instance == null)
                instance = DatabaseHelper(context)
        }
    }

    fun insertUserInPaper(user: PaperUser) {
        Hawk.put("user", user)
    }

    fun readUserFromPaper(): PaperUser? {
        return Hawk.get("user")
    }

    fun deleteUserFromPaper() {
        Hawk.delete("user")
    }

    fun writeStringInSharedPrefs(key: String, value: String) {
        with(prefs.edit()) {
            putString(key, value)
            commit()
        }
    }

    fun readStringInSharedPrefs(key: String): String? {
        return prefs.getString(key, null)
    }

    fun writeBoolInSharedPrefs(key: String, value: Boolean) {
        with(prefs.edit()) {
            putBoolean(key, value)
            commit()
        }
    }

    fun readBoolInSharedPrefs(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    private fun getRealmKey(): ByteArray {

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
            realm.writeEncryptedCopyTo(newRealmFile, getRealmKey())
            realm.close()
            Realm.deleteRealm(old)
        }

        try {
            newRealmFile.delete()
            Timber.d("realm file deleted")
        } catch (e: Exception) {
            Timber.d(e)
        }


    }

}
