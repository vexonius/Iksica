package com.tstudioz.iksica.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by etino7 on 23-Oct-17.
 */

public class Transaction extends RealmObject {

    @PrimaryKey
    public String timeID;

    public String restoran;
    public String vrijeme;
    public String datum;
    public String iznos;
    public String subvencija;


    public void setTimeID(String timeID) {
        this.timeID = timeID;
    }

    public void setRestoran(String restoran) {
        this.restoran = restoran;
    }

    public void setVrijeme(String vrijeme) {
        this.vrijeme = vrijeme;
    }

    public void setIznos(String iznos) {
        this.iznos = iznos;
    }

    public void setSubvencija(String subvencija) {
        this.subvencija = subvencija;
    }

    public String getIznos() {
        return iznos;
    }

    public String getDatum() {
        return datum;
    }

    public String getTimeID() {
        return timeID;
    }

    public String getRestoran() {
        return restoran;
    }

    public String getSubvencija() {
        return subvencija;
    }

    public String getVrijeme() {
        return vrijeme;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }
}
