package com.tstudioz.menze.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.tstudioz.menze.Model.User;
import com.tstudioz.menze.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import static android.content.ContentValues.TAG;


public class CardFragment extends Fragment {

    OkHttpClient okHttpClient;
    TextView saldo, korisnik, broj_kartice;
    ProgressBar loading;
    RelativeLayout relativeLayout;

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
            View view = inflater.inflate(R.layout.iksica_layout,
                    container, false);

            Realm mRealm = Realm.getDefaultInstance();
            User user = mRealm.where(User.class).findFirst();

            fetchData(user.getuMail().toString(), user.getuPassword().toString());

            return view;
        }

        public void fetchData(final String username, final String password){

            final CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getActivity()));
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            okHttpClient = new OkHttpClient().newBuilder()
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .cookieJar(cookieJar)
                    .addInterceptor(logging)
                    .build();

             Request rq = new Request.Builder()
                    .url("https://issp.srce.hr/isspaaieduhr/login.ashx")
                    .get()
                    .build();

            Call call = okHttpClient.newCall(rq);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "odgovor", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    final Document doc = Jsoup.parse(response.body().string());
                    Element el = doc.getElementById("SAMLRequest");
                    String authToken = el.val();

                    Log.d("prvi_body", response.toString());

                    RequestBody formBody = new FormBody.Builder()
                            .add("submit", "Continue")
                            .add("SAMLRequest", authToken)
                            .build();

                 Request rq = new Request.Builder()
                    .url("https://login.aaiedu.hr/ms/saml2/idp/SSOService.php")
                    .post(formBody)
                    .build();

                    Call call1 = okHttpClient.newCall(rq);
                    call1.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "Odgovor_neuspjesno", e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            Document document = Jsoup.parse(response.body().string());

                            String authState = document
                                    .select("#aai_centerbox > div.aai_form_container > div.aai_login_form > form > input[type=\"hidden\"]:nth-child(7)")
                                    .first().attr("value");

                            RequestBody formBody = new FormBody.Builder()
                                    .add("username", username)
                                    .add("password", password)
                                    .add("Submit", "Prijavi se")
                                    .add("AuthState", authState)
                                    .build();

                            final Request request = new Request.Builder()
                                    .url(response.request().url())
                                    .post(formBody)
                                    .build();

                            Call call4 = okHttpClient.newCall(request);
                            call4.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d(TAG, "Odgovor_neuspjesno", e);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {

                                    Document document = Jsoup.parse(response.body().string());

                                    Element el = document.select("body > form > input[type=\"hidden\"]:nth-child(2)").first();
                                    final String loginToken = el.attr("value");

                                    Log.d("Login token", loginToken);

                                    RequestBody formBody = new FormBody.Builder()
                                            .add("SAMLResponse", loginToken)
                                            .add("submit", "Submit")
                                            .build();

                                    final Request request = new Request.Builder()
                                            .url("https://login.aaiedu.hr/ms/module.php/saml/sp/saml2-acs.php/default-sp")
                                            .post(formBody)
                                            .build();

                                    Call call5 = okHttpClient.newCall(request);
                                    call5.enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            Log.d(TAG, "Odgovor_neuspjesno", e);
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {

                                            Document document = Jsoup.parse(response.body().string());

                                            Element el = document.select("body > form > input[type=\"hidden\"]:nth-child(2)").first();
                                            final String afterToken = el.attr("value");

                                            RequestBody formBody = new FormBody.Builder()
                                                    .add("SAMLResponse", afterToken)
                                                    .add("submit", "Submit")
                                                    .build();

                                            final Request request = new Request.Builder()
                                                    .url("https://issp.srce.hr/ISSPAAIEduHr/login.ashx")
                                                    .post(formBody)
                                                    .build();

                                            Call call2 = okHttpClient.newCall(request);
                                            call2.enqueue(new Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {
                                                    Log.d(TAG, "Odgovor_neuspjesno", e);
                                                }

                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {

                                                    final Request request = new Request.Builder()
                                                            .url(response.request().url())
                                                            .get()
                                                            .build();

                                                    Call call6 = okHttpClient.newCall(request);
                                                    call6.enqueue(new Callback() {
                                                        @Override
                                                        public void onFailure(Call call, IOException e) {
                                                            Log.d(TAG, "Odgovor_neuspjesno", e);
                                                        }

                                                        @Override
                                                        public void onResponse(Call call, Response response) throws IOException {

                                                            Document document = Jsoup.parse(response.body().string());
                                                            final Element el = document.select("body > div > div.container > div:nth-child(3) > div:nth-child(4) > p:nth-child(8) ").first();
                                                            final Element user = document.select("body > div > div.container > div:nth-child(3) > div.col-md-4.col-md-offset-2 > h3 > strong").first();
                                                            final Element number = document.select("body > div > div.container > div:nth-child(6) > div > table > tbody > tr:nth-child(2) > td:nth-child(1)").first();

                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    String money = el.text();
                                                                    String num = money.substring(17, money.length());
                                                                    saldo = (TextView)getActivity().findViewById(R.id.pare);
                                                                    saldo.setText(num + " kn");

                                                                    korisnik = (TextView)getActivity().findViewById(R.id.user_name);
                                                                    korisnik.setText(user.text());

                                                                    broj_kartice = (TextView)getActivity().findViewById(R.id.card_number);
                                                                    broj_kartice.setText(number.text());

                                                                    loading = (ProgressBar)getActivity().findViewById(R.id.progressBar);
                                                                    relativeLayout = (RelativeLayout)getActivity().findViewById(R.id.iksica_card_layout);

                                                                    loading.setVisibility(View.INVISIBLE);
                                                                    relativeLayout.setVisibility(View.VISIBLE);


                                                                }
                                                            });

                                                        }
                                                    });



                                                }

                                            });


                                        }
                                    });


                                }
                            });


                        }
                    });
                }
            });
        }


        @Override
        public void onStop(){
            super.onStop();

            if (okHttpClient!=null)
                okHttpClient.dispatcher().cancelAll();

        }
}
