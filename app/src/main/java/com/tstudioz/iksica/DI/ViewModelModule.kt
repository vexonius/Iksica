package com.tstudioz.iksica.DI

import com.tstudioz.iksica.HomeScreen.MainViewModel
import com.tstudioz.iksica.SignInScreen.SignInViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by etino7 on 4.3.2020..
 */

val viewModelModule = module {
    viewModel { SignInViewModel() }
    viewModel { MainViewModel() }
}