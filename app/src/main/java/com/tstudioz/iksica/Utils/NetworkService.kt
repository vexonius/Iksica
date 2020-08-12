package com.tstudioz.iksica.Utils

import okhttp3.*
import java.io.IOException

/**
 * Created by etino7 on 09/10/2019.
 */
class NetworkService(private val okHttpClient: OkHttpClient) {

    private var currentUrl: HttpUrl? = null

    fun clearCookies() {
        OkHttpClientX.clearCookies()
    }

    @Throws(IOException::class)
    fun fetchSAMLToken(): Response {
        val rq = Request.Builder()
                .url("https://issp.srce.hr/isspaaieduhr/login.ashx")
                .build()
        return okHttpClient.newCall(rq).execute()
    }

    @Throws(IOException::class)
    fun getAuthState(token: String?): Response {
        val formBody: RequestBody = FormBody.Builder()
                .add("submit", "Continue")
                .add("SAMLRequest", token ?: "")
                .build()

        val rq = Request.Builder()
                .url("https://login.aaiedu.hr/ms/saml2/idp/SSOService.php")
                .post(formBody)
                .build()

        val response = okHttpClient.newCall(rq).execute()
        currentUrl = response.request.url

        return response
    }

    @Throws(IOException::class)
    fun postAuthState(username: String?, password: String?, authState: String?): Response {
        val formBody: RequestBody = FormBody.Builder()
                .add("username", username ?: "")
                .add("password", password ?: "")
                .add("Submit", "Prijavi se")
                .add("AuthState", authState ?: "")
                .build()

        val request = Request.Builder()
                .url(currentUrl!!)
                .post(formBody)
                .build()

        return okHttpClient.newCall(request).execute()
    }

    @Throws(IOException::class)
    fun getResponseToken(token: String?): Response {
        val formBody: RequestBody = FormBody.Builder()
                .add("SAMLResponse", token ?: "")
                .add("submit", "Submit")
                .build()

        val request = Request.Builder()
                .url("https://login.aaiedu.hr/ms/module.php/saml/sp/saml2-acs.php/default-sp")
                .post(formBody)
                .build()

        return okHttpClient.newCall(request).execute()
    }

    @Throws(IOException::class)
    fun postResponseToken(token: String?): Response {
        val formBody: RequestBody = FormBody.Builder()
                .add("SAMLResponse", token ?: "")
                .add("submit", "Submit")
                .build()

        val request = Request.Builder()
                .url("https://issp.srce.hr/ISSPAAIEduHr/login.ashx")
                .post(formBody)
                .build()

        val response = okHttpClient.newCall(request).execute()
        currentUrl = response.request.url

        return response
    }

    @Throws(IOException::class)
    fun getUserInfo(): Response {
        val request = Request.Builder()
                .url("https://issp.srce.hr/Student")
                .get()
                .build()
        return okHttpClient.newCall(request).execute()
    }

    @Throws(IOException::class)
    fun getUserTransactions(oib: Long?): Response {

        val request = Request.Builder()
                .url("https://issp.srce.hr/PretragaStudenta/StudentRacuni?oib=$oib")
                .get()
                .build()

        return okHttpClient.newCall(request).execute()
    }

    @Throws(IOException::class)
    fun fetchTransactiondetails(linkOfReceipt: String): Response {
        val request = Request.Builder()
                .url("https://issp.srce.hr$linkOfReceipt")
                .get()
                .build()

        return okHttpClient.newCall(request).execute()
    }

}