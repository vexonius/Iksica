package com.tstudioz.iksica.Utils

import com.tstudioz.iksica.Data.Models.User
import io.realm.RealmModel
import io.realm.RealmResults

/**
 * Created by etino7 on 18/10/2019.
 */

class RealmUtil {

    companion object {
        @JvmStatic
        fun <T: RealmModel> RealmResults<T>.asLiveData() = RealmLiveData<T>(this)

        @JvmStatic
        fun <T : RealmModel> RealmResults<User>.asSingleLiveData() = RealmSingleResultLiveData<User>(this)
    }
}