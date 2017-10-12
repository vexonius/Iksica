package com.tstudioz.menze.Activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.tstudioz.menze.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DetailActivity extends AppCompatActivity {

    private OkHttpClient okHttpClient;

        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            getSupportActionBar().hide();
            setContentView(R.layout.activity_detail);

            setMenzaTextBold();

            getMeni("Fesb_vrh");

        }

        public void setMenzaTextBold(){
            String naslov = getIntent().getStringExtra("name");
            TextView titleView = (TextView)findViewById(R.id.detail_title);
            titleView.setText(naslov);
        }

        public void getMeni(final String menzaUrl){


            okHttpClient = new OkHttpClient().newBuilder()
                    .followSslRedirects(true)
                    .followRedirects(true)
                    .connectTimeout(600, TimeUnit.MILLISECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url("http://www.scst.unist.hr/jelovnik/")
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
                Document doc = Jsoup.parse(response.body().string());

                Element element = doc.getElementById(menzaUrl);


            }
        });

    }

}


