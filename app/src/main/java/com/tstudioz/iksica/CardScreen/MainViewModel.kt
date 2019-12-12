package com.tstudioz.iksica.CardScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.iksica.Data.Models.PaperUser
import com.tstudioz.iksica.Data.Models.Transaction
import com.tstudioz.iksica.Data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by etino7 on 16/10/2019.
 */
class MainViewModel() : ViewModel() {

    val repository: Repository
    val mUserData: MutableLiveData<PaperUser>
    val isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)
    val mUserTransactions: MutableLiveData<List<Transaction>> = MutableLiveData()
    val mTransactionsMapped: MutableLiveData<LinkedHashMap<String, Float>> = MutableLiveData()

    init {
        repository = Repository.getInstance()
        mUserData = repository.getUserData()
        loginUser()
    }

    fun getUserData(): LiveData<PaperUser>? {
        return mUserData
    }

    fun getUserTransactions(): LiveData<List<Transaction>>? {
        return mUserTransactions
    }

    fun getTransactionDataMapped(): LiveData<LinkedHashMap<String, Float>> {
        return mTransactionsMapped
    }

    fun isLayoutRefreshing(): LiveData<Boolean> {
        return isRefreshing
    }

    fun loginUser() {
        viewModelScope.launch(context = Dispatchers.Main) {
            try {
                isRefreshing.value = true
                async(context = Dispatchers.IO) {
                    repository.loginUser()
                }.await()

                async(context = Dispatchers.IO) {
                    scrapeUserInfo()
                }.await()
                isRefreshing.value = false
            } catch (err: Exception) {
                Timber.e(err)
                if (err is NullPointerException)
                    Timber.d("Podatci za login su null")
                isRefreshing.value = false
            }
        }

    }

    fun scrapeUserInfo() {
        viewModelScope.launch(context = Dispatchers.Main) {
            val userData: PaperUser = async(context = Dispatchers.IO) { repository.scrapeUserInfo() }.await()
            val transactions: ArrayList<Transaction> = async(context = Dispatchers.IO) { repository.scrapeuserTransactions(userData) }.await()
            mUserTransactions.value = transactions
            async(context = Dispatchers.Default) { createLinkedHashMap(transactions) }
            async(context = Dispatchers.IO) {
                repository.updateUserData(userData)
            }.await()
        }
    }

    fun createLinkedHashMap(list: List<Transaction>) {
        val linkedHashMap: LinkedHashMap<String, Float> = LinkedHashMap()
        for (item in list)
            linkedHashMap.put(item.date, item.amount.toFloat())

        mTransactionsMapped.postValue(linkedHashMap)

    }


}