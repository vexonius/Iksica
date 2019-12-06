package com.tstudioz.iksica.SignInScreen

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.*
import com.tstudioz.iksica.Activities.HomeActivity
import com.tstudioz.iksica.Data.Models.User
import com.tstudioz.iksica.Data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.IOException

/**
 * Created by etino7 on 16/10/2019.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository : Repository
    val mUserData : MutableLiveData<User>
    val application: Context
    val isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        this.application = application
        repository = Repository.getInstance()
        mUserData = repository.getUserDataBlocking()
    }

    fun getUserData() : LiveData<User> {
        return mUserData
    }

    fun isLayoutRefreshing() : LiveData<Boolean>{
        return isRefreshing
    }


    fun insertUserData(user : User){
        repository.insertUserBlocking(user)
    }

    fun loginUser() {
        viewModelScope.launch(context = Dispatchers.Main) {
            try {
                isRefreshing.value = true
                async(context = Dispatchers.IO) { repository.loginUser() } .await()
                scrapeUserInfo()
                isRefreshing.value = false
            } catch (err: IOException) {
                Timber.e(err)
                isRefreshing.value = false
            }
        }

    }

    fun scrapeUserInfo(){
        viewModelScope.launch(context = Dispatchers.Main) {
            val userData = async(context = Dispatchers.IO) { repository.scrapeUserInfo() }.await()
            Timber.d("username: ${userData!!.uName}")
            repository.insertUser(userData)
            repository.loadUserFromDb()
        }
    }


}