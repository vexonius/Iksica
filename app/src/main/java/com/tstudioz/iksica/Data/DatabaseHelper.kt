package com.tstudioz.iksica.Data

import android.content.Context
import android.content.SharedPreferences
import com.orhanobut.hawk.Hawk
import com.tstudioz.iksica.Data.Models.PaperUser

/**
 * Created by etino7 on 12/10/2019.
 */
class DatabaseHelper constructor(context: Context) {
    private val prefs: SharedPreferences

    init {
        prefs = context.getSharedPreferences("DEFAULT_PREFS", Context.MODE_PRIVATE)
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

}
