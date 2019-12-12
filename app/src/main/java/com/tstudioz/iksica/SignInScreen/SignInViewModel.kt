package com.tstudioz.iksica.SignInScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.iksica.Data.Models.PaperUser
import com.tstudioz.iksica.Data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by etino7 on 12/8/2019.
 */
class SignInViewModel : ViewModel() {

    val repository: Repository
    val userLogged: MutableLiveData<Boolean>
    val userData: MutableLiveData<PaperUser>

    init {
        repository = Repository.getInstance()
        userData = repository.getUserData()
        userLogged = repository.getUserAlreadyLogged()
    }

    fun authenticateCredentials() {
        // TODO: authentication logic
        repository.setUserLogged("user_logged", true)
    }


    fun insertUserData(user: PaperUser) {
        viewModelScope.launch(context = Dispatchers.Main) {
            async(context = Dispatchers.IO) { repository.insertUser(user) }.await()
            authenticateCredentials()
        }
    }


    fun isUserLoggedAlready(): LiveData<Boolean> {
        Timber.d("userlogged ${userLogged.value}")
        return userLogged
    }

}