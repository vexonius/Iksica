package com.tstudioz.iksica.HomeScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.iksica.Data.Models.PaperUser
import com.tstudioz.iksica.Data.Models.Transaction
import com.tstudioz.iksica.Data.Models.TransactionDetails
import com.tstudioz.iksica.Data.Repository
import com.tstudioz.iksica.Utils.Exceptions.NoNetworkException
import com.tstudioz.iksica.Utils.LiveEvent
import com.tstudioz.iksica.Utils.NoNetworkInterceptor
import com.tstudioz.iksica.Utils.Exceptions.WrongCredsException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by etino7 on 16/10/2019.
 */
class MainViewModel : ViewModel() {

    val repository: Repository

    val mUserData: MutableLiveData<PaperUser>
    val isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)

    val areTransactionsRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)
    val mUserTransactions: MutableLiveData<List<Transaction>> = MutableLiveData()
    val mTransactionsMapped: MutableLiveData<LinkedHashMap<String, Float>> = MutableLiveData()

    val mCurrentTransactionData: MutableLiveData<Transaction> = MutableLiveData()

    val mErrors: LiveEvent<String> = LiveEvent()

    val handler = CoroutineExceptionHandler { _, throwable ->
        when (throwable) {
            is WrongCredsException -> {
                mErrors.postValue("Došlo je do pogreške prilikom prijave. Osvježite za ponovni pokušaj")
                isRefreshing.value = false
                areTransactionsRefreshing.value = false
            }
            is NoNetworkException -> {
                mErrors.postValue("Niste povezani. \nProvjerite internetsku vezu i pokušajte ponovno")
                isRefreshing.value = false
                areTransactionsRefreshing.value = false
            }
            else -> {
                Timber.e(throwable)
                mErrors.postValue("Došlo je do pogreške. \nPokušajte ponovno kasnije")
                isRefreshing.value = false
                areTransactionsRefreshing.value = false
            }
        }
    }


    init {
        repository = Repository.getInstance()
        mUserData = repository.getUserData()
        loginUser()
    }

    fun getUserData(): LiveData<PaperUser>? {
        return mUserData
    }

    fun getErrors(): LiveData<String>? {
        return mErrors
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

    fun isTransactionsLayoutRefreshing() : LiveData<Boolean> {
        return areTransactionsRefreshing
    }

    fun loginUser() {
        viewModelScope.launch(handler) {
            isRefreshing.value = true
            areTransactionsRefreshing.value = true
            async(context = Dispatchers.IO) {
                repository.loginUser()
            }.await()

            async(context = Dispatchers.IO) {
                scrapeUserInfo()
            }.await()
            isRefreshing.value = false
        }

    }

    fun scrapeUserInfo() {
        viewModelScope.launch(context = Dispatchers.Main) {
            val userData: PaperUser = async(context = Dispatchers.IO) { repository.scrapeUserInfo() }.await()

            async(context = Dispatchers.IO) {
                repository.updateUserData(userData)
            }.await()

            val transactions: ArrayList<Transaction> = async(context = Dispatchers.IO) {
                repository.scrapeUserTransactions(userData)
            }.await()

            mUserTransactions.value = transactions

            async(context = Dispatchers.IO) {
                createLinkedHashMap(transactions)
                repository.updateUserData(userData)
                areTransactionsRefreshing.postValue(false)
            }.await()
        }
    }

    fun createLinkedHashMap(list: List<Transaction>) {
        val linkedHashMap: LinkedHashMap<String, Float> = LinkedHashMap()
        for (item in list)
            linkedHashMap[item.date] = item.amount.toFloat()

        mTransactionsMapped.postValue(linkedHashMap)
    }

    fun logOutUser() {
        repository.logOutUser()
    }

    fun updateCurrentTransactionDetails(transaction: Transaction) {
        mCurrentTransactionData.value = transaction
        repository.clearTransactionDetails()
        viewModelScope.launch(handler) {
            async(context = Dispatchers.IO) {
                repository.fetchTransactionDetails(transaction.linkOfReceipt)
            }.await()
        }
    }

    fun getCurrentTransaction(): LiveData<Transaction> {
        return mCurrentTransactionData
    }

    fun getCurrentTransactionItems(): LiveData<TransactionDetails> {
        return repository.getTransactionDetails()
    }

}