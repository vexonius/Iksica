package com.tstudioz.menze.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.tstudioz.menze.Model.Transaction;
import com.tstudioz.menze.Model.User;
import com.tstudioz.menze.Model.UserInfoItem;
import com.tstudioz.menze.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    TextView saldo, korisnik, broj_kartice, potrosenoDanasTextView;
    ProgressBar loading;
    RelativeLayout relativeLayout;
    private Snackbar snack;
    public Realm mRealm;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.iksica_layout,
                container, false);

        loading = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.iksica_card_layout);

        mRealm = Realm.getDefaultInstance();
        User user = mRealm.where(User.class).findFirst();

        fetchData(user.getuMail().toString(), user.getuPassword().toString());


        return view;
    }

    public void showUserCard() {

        if (isNetworkAvailable()) {
            loading.setVisibility(View.INVISIBLE);
            relativeLayout.setVisibility(View.VISIBLE);
        } else {
            showNetworkErrorSnack();
        }

    }

    public void showNetworkErrorSnack() {
        snack = Snackbar.make(getActivity().findViewById(R.id.iksica_root_relative), "Niste povezani", Snackbar.LENGTH_INDEFINITE);
        snack.setAction("PONOVI", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserCard();
            }
        });
        snack.show();
    }

    public void showErrorSnack() {
        snack = Snackbar.make(getActivity().findViewById(R.id.iksica_root_relative), "Došlo je do pogreške. Pokušajte ponovno kasnije", Snackbar.LENGTH_LONG);
        snack.show();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public void fetchData(final String username, final String password) {

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

                                                        final Document document = Jsoup.parse(response.body().string());
                                                        final Element el = document.select("body > div > div.container > div:nth-child(3) > div:nth-child(4) > p:nth-child(8) ").first();
                                                        final Element user = document.select("body > div > div.container > div:nth-child(3) > div.col-md-4.col-md-offset-2 > h3 > strong").first();
                                                        final Element number = document.select("body > div > div.container > div:nth-child(6) > div > table > tbody > tr:nth-child(2) > td:nth-child(1)").first();

                                                        final String uciliste = document.select("body > div > div.container > div:nth-child(3) > div:nth-child(4) > p:nth-child(4)").first().text();
                                                        final String razinaPrava = document.select("body > div > div.container > div:nth-child(3) > div:nth-child(4) > p:nth-child(5)").first().text();
                                                        final String pravaOd = document.select("body > div > div.container > div:nth-child(3) > div:nth-child(4) > p:nth-child(6)").first().text();
                                                        final String pravaDo = document.select("body > div > div.container > div:nth-child(3) > div:nth-child(4) > p:nth-child(7)").first().text();
                                                        final String potrosnjaDanas = document.select("body > div > div.container > div:nth-child(3) > div:nth-child(4) > p:nth-child(9)").first().text();
                                                        final String userName = document.select("body > div > div.container > div:nth-child(3) > div.col-md-4.col-md-offset-2 > h3").first().text();
                                                        final  String slikaLink = document.getElementsByClass("col-md-2").select("img").attr("src").toString();


                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                final UserInfoItem userInfoItem = new UserInfoItem();
                                                                userInfoItem.setindex(1);
                                                                userInfoItem.setItemTitle("Fakultet");
                                                                userInfoItem.setItemDesc(uciliste.substring(9, uciliste.length()));

                                                                final UserInfoItem userInfoItem2 = new UserInfoItem();
                                                                userInfoItem2.setindex(2);
                                                                userInfoItem2.setItemTitle("Razina  prava");
                                                                userInfoItem2.setItemDesc(razinaPrava.substring(13, razinaPrava.length()));

                                                                final UserInfoItem userInfoItem3 = new UserInfoItem();
                                                                userInfoItem3.setindex(3);
                                                                userInfoItem3.setItemTitle("Prava vrijede od");
                                                                userInfoItem3.setItemDesc(pravaOd.substring(9, pravaOd.length()));

                                                                final UserInfoItem userInfoItem4 = new UserInfoItem();
                                                                userInfoItem4.setindex(4);
                                                                userInfoItem4.setItemTitle("Prava vrijede do");
                                                                userInfoItem4.setItemDesc(pravaDo.substring(9, pravaDo.length()));



                                                                mRealm.executeTransaction(new Realm.Transaction() {
                                                                    @Override
                                                                    public void execute(Realm realm) {
                                                                        User mUser = mRealm.where(User.class).findFirst();
                                                                        mUser.setuName(userName);
                                                                        mUser.setSrcLink(slikaLink);

                                                                        mRealm.copyToRealmOrUpdate(userInfoItem);
                                                                        mRealm.copyToRealmOrUpdate(userInfoItem2);
                                                                        mRealm.copyToRealmOrUpdate(userInfoItem3);
                                                                        mRealm.copyToRealmOrUpdate(userInfoItem4);
                                                                    }
                                                                });

                                                                String money = el.text();
                                                                String num = money.substring(17, money.length());
                                                                saldo = (TextView) getActivity().findViewById(R.id.pare);
                                                                saldo.setText(num + " kn");

                                                                korisnik = (TextView) getActivity().findViewById(R.id.user_name);
                                                                korisnik.setText(user.text());

                                                                broj_kartice = (TextView) getActivity().findViewById(R.id.card_number);
                                                                broj_kartice.setText(number.text());

                                                                potrosenoDanasTextView = (TextView) getActivity().findViewById(R.id.potroseno_danas_value);
                                                                potrosenoDanasTextView.setText("- " + potrosnjaDanas.substring(16, potrosnjaDanas.length()) + " kn");

                                                                loading = (ProgressBar) getActivity().findViewById(R.id.progressBar);
                                                                relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.iksica_card_layout);

                                                                loading.setVisibility(View.INVISIBLE);
                                                                relativeLayout.setVisibility(View.VISIBLE);

                                                                getTransactions("https://issp.srce.hr" + document.getElementsByClass("btn-primary btn-lg").first().attr("href"));


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

    public void getTransactions(String link){

        final Request request = new Request.Builder()
                .url(link)
                .get()
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showErrorSnack();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Document document = Jsoup.parse(response.body().string());


                try {


                    final Element tablica = document.select("body > div > div.container > table").first();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("LOGAMO MALO", tablica.text());
                        }
                    });


                    final Elements redovi = tablica.select("tr");

                    for (final Element red : redovi) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                 String vrijeme = red.child(1).text();
                                 if(!vrijeme.equals("Vrijeme računa")) {
                                     final Transaction transaction = new Transaction();
                                     transaction.setTimeID(vrijeme);
                                     transaction.setRestoran(red.child(0).text());
                                     transaction.setVrijeme(vrijeme.substring(11, vrijeme.length() - 3));
                                     transaction.setDatum(vrijeme.substring(0, 5));
                                     transaction.setIznos(red.child(2).text());
                                     transaction.setSubvencija(red.child(3).text());

                                     mRealm.executeTransaction(new Realm.Transaction() {
                                         @Override
                                         public void execute(Realm realm) {
                                             mRealm.copyToRealmOrUpdate(transaction);
                                         }
                                     });

                                 }

                            }
                        });
                    }

                }catch (NullPointerException e){
                    Log.d("error on string", e.toString());
                }
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();

        if (okHttpClient != null)
            okHttpClient.dispatcher().cancelAll();

    }
}
