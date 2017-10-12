package com.tstudioz.menze.Model;


public class MenzaItem {

    public String imeMenze;
    public String url;

    public MenzaItem(String imeMenze, String url){
        this.imeMenze=imeMenze;
        this.url=url;

    }


    public String getImeMenze() {
        return imeMenze;
    }

    public String getUrl() {
        return url;
    }

    public void setImeMenze(String imeMenze) {
        this.imeMenze = imeMenze;
    }


    public void setUrl(String url) {
        this.url = url;
    }
}
