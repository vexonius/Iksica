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
import com.tstudioz.iksica.Utils.Exceptions.WrongCredsException
import com.tstudioz.iksica.Utils.LiveEvent
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

/**
 * Created by etino7 on 16/10/2019.
 */
class MainViewModel : ViewModel() {

    private val repository: Repository by inject(Repository::class.java)

    private val mUserData: MutableLiveData<PaperUser>
    private val isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)

    private val areTransactionsRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)
    private val mUserTransactions: MutableLiveData<List<Transaction>> = MutableLiveData()
    private val mTransactionsMapped: MutableLiveData<LinkedHashMap<String, Float>> = MutableLiveData()

    private val mCurrentTransactionData: MutableLiveData<Transaction> = MutableLiveData()

    private val mErrors: LiveEvent<String> = LiveEvent()

    private val handler = CoroutineExceptionHandler { _, throwable ->
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

    fun isTransactionsLayoutRefreshing(): LiveData<Boolean> {
        return areTransactionsRefreshing
    }

    fun loginUser() {
        viewModelScope.launch(handler) {
            isRefreshing.value = true
            areTransactionsRefreshing.value = true
            withContext(context = Dispatchers.IO) {
                repository.loginUser()
            }

            withContext(context = Dispatchers.IO) {
                scrapeUserInfo()
            }
            isRefreshing.value = false
        }
    }

    private fun scrapeUserInfo() {
        viewModelScope.launch(context = Dispatchers.Main) {
            val userData: PaperUser = async(context = Dispatchers.IO) { repository.scrapeUserInfo() }.await()

            withContext(context = Dispatchers.IO) {
                repository.updateUserData(userData)
            }

            val transactions: ArrayList<Transaction> = withContext(context = Dispatchers.IO) {
                repository.scrapeUserTransactions(userData)
            }

            mUserTransactions.value = transactions

            withContext(context = Dispatchers.IO) {
                createLinkedHashMap(transactions)
                repository.updateUserData(userData)
                areTransactionsRefreshing.postValue(false)
            }
        }
    }

    private fun createLinkedHashMap(list: List<Transaction>) {
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
            withContext(context = Dispatchers.IO) {
                repository.fetchTransactionDetails(transaction.linkOfReceipt)
            }
        }
    }

    fun getCurrentTransaction(): LiveData<Transaction> {
        return mCurrentTransactionData
    }

    fun getCurrentTransactionItems(): LiveData<TransactionDetails> {
        return repository.getTransactionDetails()
    }

}