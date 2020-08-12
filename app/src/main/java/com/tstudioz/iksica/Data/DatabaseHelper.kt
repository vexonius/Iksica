package com.tstudioz.iksica.Data

import android.content.Context
import android.content.SharedPreferences
import com.orhanobut.hawk.Hawk
import com.tstudioz.iksica.Data.Models.PaperUser

/**
 * Created by etino7 on 12/10/2019.
 */
class DatabaseHelper(context: Context) {

    companion object {
        private const val USER_KEY = "user"
        private const val SHARED_PREFS_FILE = "DEFAULT_PREFS"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)

    fun insertUserInPaper(user: PaperUser) {
        Hawk.put(USER_KEY, user)
    }

    fun readUserFromPaper(): PaperUser? {
        return Hawk.get(USER_KEY)
    }

    fun deleteUserFromPaper() {
        Hawk.delete(USER_KEY)
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

}
