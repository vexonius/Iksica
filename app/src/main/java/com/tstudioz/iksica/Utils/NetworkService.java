package com.tstudioz.iksica.Utils;

import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

/**
 * Created by etino7 on 09/10/2019.
 */
public class NetworkService {

    OkHttpClient okHttpClient;
    HttpUrl currentUrl;

    public NetworkService(){
        okHttpClient = OkHttpClientX.getInstance();
    }


    public Response fetchSAMLToken() throws IOException {
        Request rq = new Request.Builder()
                .url("https://issp.srce.hr/isspaaieduhr/login.ashx")
                .build();

        Response response = okHttpClient.newCall(rq).execute();

        return response;
    }

    public Response getAuthState(String token) throws IOException{
        RequestBody formBody = new FormBody.Builder()
                .add("submit", "Continue")
                .add("SAMLRequest", token)
                .build();

        Request rq = new Request.Builder()
                .url("https://login.aaiedu.hr/ms/saml2/idp/SSOService.php")
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(rq).execute();
        currentUrl = response.request().url();

        return response;
    }

    public Response postAuthState(String username, String password, String authState) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("Submit", "Prijavi se")
                .add("AuthState", authState)
                .build();

        final Request request = new Request.Builder()
                .url(currentUrl)
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();

        return response;
    }

    public Response getResponseToken(String token) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("SAMLResponse", token)
                .add("submit", "Submit")
                .build();

        final Request request = new Request.Builder()
                .url("https://login.aaiedu.hr/ms/module.php/saml/sp/saml2-acs.php/default-sp")
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();

        return response;
    }

    public Response postResponseToken(String token) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("SAMLResponse", token)
                .add("submit", "Submit")
                .build();

        final Request request = new Request.Builder()
                .url("https://issp.srce.hr/ISSPAAIEduHr/login.ashx")
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        currentUrl = response.request().url();
        Timber.d(currentUrl.toString());

        return response;
    }

    public Response getUserInfo() throws IOException {
        Request request = new Request.Builder()
                .url("https://issp.srce.hr/Student")
                .get()
                .build();

        Response response = okHttpClient.newCall(request).execute();

        return response;
    }



}
