package com.tstudioz.menze.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

/**
 * Created by etino7 on 08-Oct-17.
 */

public class SignInActivity extends AppCompatActivity {

    Button signInButton;
    EditText editEmail, editPass;
    public Realm realm, lRealm;
    private Snackbar snack;
    String loginToken, authToken, authState;
    private OkHttpClient okHttpClient;
    private ProgressBar pbar;
    private RelativeLayout relative;


    @Override
    protected void onCreate(Bundle savedInstanceBundle){
       super.onCreate(savedInstanceBundle);
        getSupportActionBar().hide();
        setContentView(R.layout.sign_in_layout);

        realm = Realm.getDefaultInstance();
        lRealm = Realm.getDefaultInstance();

        signInButton = (Button)findViewById(R.id.sign_in_button);
        editEmail=(EditText)findViewById(R.id.sign_in_username);
        editPass=(EditText)findViewById(R.id.sign_in_password);
        pbar=(ProgressBar)findViewById(R.id.progressBarLoading);
        relative = (RelativeLayout)findViewById(R.id.sign_in_layout);

        checkUser();

    }

    public  void checkUser(){
        SharedPreferences sp = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
        Boolean prijavljen = sp.getBoolean("korisnik_prijavljen", false);

        User u = realm.where(User.class).findFirst();
        if (prijavljen && u!=null){
            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
            finish();
        } else {
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isNetworkAvailable()) {
                        signInButton.setVisibility(View.INVISIBLE);
                        pbar.setVisibility(View.VISIBLE);
                        fetchData(editEmail.getText().toString(), editPass.getText().toString());
                    } else {
                        showNetworkErrorSnack();
                        signInButton.setVisibility(View.VISIBLE);
                        pbar.setVisibility(View.INVISIBLE);
                    }
                }
            });

        }
    }

    public void registerUser(){
        try {
            lRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    User user = realm.createObject(User.class);
                    user.setuMail(editEmail.getText().toString());
                    user.setuPassword(editPass.getText().toString());

                    SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("korisnik_prijavljen", true);
                    editor.commit();

                    startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                    finish();
                }
            });
        } finally {
            lRealm.close();
        }
    }

    public void fetchData(final String username, final String password) {

        final CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(this));
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
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                registerUser();
                                            }
                                        });

                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                signInButton.setVisibility(View.VISIBLE);
                                                pbar.setVisibility(View.INVISIBLE);
                                                showErrorSnack("Uneseni podatci su pogrešni");
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

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public void showNetworkErrorSnack(){
        snack = Snackbar.make(relative, "Niste povezani", Snackbar.LENGTH_INDEFINITE);
        View view = snack.getView();
        view.setPadding(0,0,0, 167);
        snack.setAction("PONOVI", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUser();
            }
        });
        snack.show();
    }

    public void showErrorSnack(String message) {
            snack = Snackbar.make(relative, message, Snackbar.LENGTH_LONG);
            View view = snack.getView();
            view.setPadding(0,0,0, 167);
            snack.show();
        }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(realm!=null)
            realm.close();

    }

}
