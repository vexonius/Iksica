package com.tstudioz.menze.Model;

import java.util.List;

import io.realm.RealmObject;



public class MeniItem extends RealmObject {

    public String meniNaslov;
    public List<String> jela;
    public String cijena;
    public String vrstaObroka;

    public void setMeniNaslov(String meniNaslov) {
        this.meniNaslov = meniNaslov;
    }

    public void setJela(List<String> jela) {
        this.jela = jela;
    }

    public void setCijena(String cijena) {
        this.cijena = cijena;
    }

    public void setVrstaObroka(String vrstaObroka) {
        this.vrstaObroka = vrstaObroka;
    }

    public String getMeniNaslov() {
        return meniNaslov;
    }

    public List<String> getJela() {
        return jela;
    }

    public String getCijena() {
        return cijena;
    }

    public String getVrstaObroka() {
        return vrstaObroka;
    }
}
