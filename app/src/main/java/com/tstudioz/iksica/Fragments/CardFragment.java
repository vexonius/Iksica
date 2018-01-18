package com.tstudioz.iksica.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.tstudioz.iksica.Activities.SignInActivity;
import com.tstudioz.iksica.Model.Transaction;
import com.tstudioz.iksica.Model.User;
import com.tstudioz.iksica.Model.UserInfoItem;
import com.tstudioz.iksica.R;

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


public class CardFragment extends Fragment {

    public OkHttpClient okHttpClient;
    private Realm mRealm;

    private String authToken, authState, loginToken, afterToken;
    private String uciliste, razinaPrava, pravaOd, pravaDo, slikaLink, potrosnjaDanas, userName;
    public TextView saldo, korisnik, broj_kartice, potrosenoDanasTextView;
    private ProgressBar loading;
    private RelativeLayout relativeLayout, activity_root_layout;
    private Snackbar snack;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.iksica_layout,
                container, false);

        loading = (ProgressBar) view.findViewById(R.id.progressBar);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.iksica_card_layout);
        activity_root_layout = (RelativeLayout)view.findViewById(R.id.relative_home);
        korisnik = (TextView) view.findViewById(R.id.user_name);
        broj_kartice = (TextView) view.findViewById(R.id.card_number);
        potrosenoDanasTextView = (TextView) view.findViewById(R.id.potroseno_danas_value);
        saldo = (TextView) view.findViewById(R.id.pare);

        showUserCard();

        return view;
    }

    public void showUserCard() {

        loading.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.INVISIBLE);

        if (isNetworkAvailable()) {
            getKorisnickePodatke();
        } else {
            showNetworkErrorSnack();
        }

    }

    public void getKorisnickePodatke(){
        mRealm = Realm.getDefaultInstance();
        User user = mRealm.where(User.class).findFirst();
        fetchData(user.getuMail(), user.getuPassword());
    }

    public void showNetworkErrorSnack() {
        snack = Snackbar.make(getActivity().findViewById(R.id.relative_home), "Niste povezani", Snackbar.LENGTH_INDEFINITE);
        snack.setAction("PONOVI", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserCard();
            }
        });
        snack.show();
    }

    public void showErrorSnack(String message) {
        if(activity_root_layout!=null) {
            snack = Snackbar.make(activity_root_layout, message, Snackbar.LENGTH_INDEFINITE);
            snack.show();
        }
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

        okHttpClient = new OkHttpClient().newBuilder()
                .followRedirects(true)
                .followSslRedirects(true)
                .cookieJar(cookieJar)
                .build();

        Request rq = new Request.Builder()
                .url("https://issp.srce.hr/isspaaieduhr/login.ashx")
                .get()
                .build();

        Call call = okHttpClient.newCall(rq);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showErrorSnack("Došlo je do pogreške. Pokušajte ponovno");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Document doc = Jsoup.parse(response.body().string());

                final Element el = doc.getElementById("SAMLRequest");
                authToken = el.val();

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
                        showErrorSnack("Došlo je do pogreške. Pokušajte ponovno");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Document document = Jsoup.parse(response.body().string());

                        authState = document
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
                                showErrorSnack("Došlo je do pogreške. Pokušajte ponovno");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                                Document document = Jsoup.parse(response.body().string());

                                try {
                                    Element el = document.select("body > form > input[type=\"hidden\"]:nth-child(2)").first();
                                    loginToken = el.attr("value");
                                } catch(Exception exp){
                                    Log.e("Exception found", exp.toString());
                                }
                                finally {
                                    if(loginToken!=null) {
                                        parseResponseToken(loginToken);
                                    } else {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                signOut();
                                                Toast.makeText(getContext(), "Pogrešno unesen email/lozinka", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                }
                            }
                        });
                    }
                });
            }
        });
    }

    public void parseResponseToken(String token){

        RequestBody formBody = new FormBody.Builder()
                .add("SAMLResponse", token)
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
                showErrorSnack("Došlo je do pogreške. Pokušajte ponovno");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Document document = Jsoup.parse(response.body().string());

                try {
                    Element el = document.select("body > form > input[type=\"hidden\"]:nth-child(2)").first();
                    afterToken = el.attr("value");
                } catch(Exception ex){
                    Log.e("Exception found", ex.toString());
                }

                if(afterToken!=null)
                    submitResponseToken(afterToken);

            }
        });
    }

    public void submitResponseToken(String token){
        RequestBody formBody = new FormBody.Builder()
                .add("SAMLResponse", token)
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
                showErrorSnack("Došlo je do pogreške. Pokušajte ponovno");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                fetchUserInfo(response.request().url().toString());
            }
        });
    }

    public void fetchUserInfo(String url){
         Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Call call6 = okHttpClient.newCall(request);
        call6.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showErrorSnack("Došlo je do pogreške. Pokušajte ponovno");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final Document document = Jsoup.parse(response.body().string());

                try {

                    final Element el = document.select("body > div > div.container > div:nth-child(3) > div:nth-child(4) > p:nth-child(8) ").first();
                    final Element user = document.select("body > div > div.container > div:nth-child(3) > div.col-md-4.col-md-offset-2 > h3 > strong").first();
                    final Element number = document.select("body > div > div.container > div:nth-child(6) > div > table > tbody > tr:nth-child(2) > td:nth-child(1)").first();

                    uciliste = document.select("body > div > div.container > div:nth-child(3) > div:nth-child(4) > p:nth-child(4)").first().text();
                    razinaPrava = document.select("body > div > div.container > div:nth-child(3) > div:nth-child(4) > p:nth-child(5)").first().text();
                    pravaOd = document.select("body > div > div.container > div:nth-child(3) > div:nth-child(4) > p:nth-child(6)").first().text();
                    pravaDo = document.select("body > div > div.container > div:nth-child(3) > div:nth-child(4) > p:nth-child(7)").first().text();
                    potrosnjaDanas = document.select("body > div > div.container > div:nth-child(3) > div:nth-child(4) > p:nth-child(9)").first().text();
                    userName = document.select("body > div > div.container > div:nth-child(3) > div.col-md-4.col-md-offset-2 > h3").first().text();
                    slikaLink = document.getElementsByClass("col-md-2").select("img").attr("src").toString();

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
                        
                        saldo.setText(num + " kn");
                        korisnik.setText(user.text());
                        broj_kartice.setText(number.text());
                        potrosenoDanasTextView.setText("- " + potrosnjaDanas.substring(16, potrosnjaDanas.length()) + " kn");

                        loading.setVisibility(View.INVISIBLE);
                        relativeLayout.setVisibility(View.VISIBLE);

                        getTransactions("https://issp.srce.hr" + document.getElementsByClass("btn-primary btn-lg").first().attr("href"));
                    }
                });

                } catch (Exception ex){
                    Log.e("Exception parsing data", ex.toString());
                }
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
                showErrorSnack("Došlo je do pogreške. Pokušajte ponovno");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Document document = Jsoup.parse(response.body().string());

                try {
                    final Element tablica = document.select("body > div > div.container > table").first();
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

                }catch (Exception e){
                    Log.d("error on string", e.toString());
                }
            }
        });
    }


    public void signOut(){
        SharedPreferences sp = getActivity().getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("korisnik_prijavljen");
        editor.commit();

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.deleteAll();
            }
        });

        getActivity().startActivity(new Intent(getActivity(), SignInActivity.class));
        getActivity().finish();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (okHttpClient != null)
            okHttpClient.dispatcher().cancelAll();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mRealm != null){
            mRealm.close();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }
}
