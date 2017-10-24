package com.tstudioz.menze.Activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import com.tstudioz.menze.R;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


public class DetailActivity extends AppCompatActivity {

    private OkHttpClient okHttpClient;

        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            getSupportActionBar().hide();
            setContentView(R.layout.activity_detail);

            setMenzaTextBold();

            // getMeni("Fesb_vrh");

        }

        public void setMenzaTextBold(){
            String naslov = getIntent().getStringExtra("name");
            TextView titleView = (TextView)findViewById(R.id.detail_title);
            titleView.setText(naslov);
        }

        public void getMeni(final String menzaUrl){

            final CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(this));
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            okHttpClient = new OkHttpClient().newBuilder()
                    .followSslRedirects(true)
                    .followRedirects(true)
                    .cookieJar(cookieJar)
                    .addInterceptor(logging)
                    .build();


            Request request = new Request.Builder()
                    .url("http://sc.dbtouch.com/menu/api.php/?place=kampus")
                    .get()
                    .build();

            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.detail_relative), "Došlo je do pogreške", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                  //      Document document = Jsoup.parse(response.body().string());
//
                  //      String body = document.select("body").text();
//
                  //      Log.e("body", body);
//
                  //      Type type = new TypeToken<JsonObject>() {}.getType();
//
                  //      Gson gson = new GsonBuilder().serializeNulls().create();
                  //      JsonObject object = gson.fromJson(body, JsonObject.class);
//
                  //      Log.e("gson", String.valueOf(object.values[7].toString()));




                  //      int i;
                  //      int j;
                  //      for (i=0; i<object.values.length; i++){
                  //          Log.e("linija broj", String.valueOf(i) + String.valueOf(object.values[i].toString()));
                  //          try {
                  //              String[] items = gson.fromJson(object.values[i].toString(), String[].class);
//
                  //          }catch (JsonParseException e) {
                  //              Log.e("gson exception", e.toString());
                  //          }
                  //      }



                            }
                        });
            }


}


