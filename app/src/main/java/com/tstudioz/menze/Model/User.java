package com.tstudioz.menze.Model;

import io.realm.RealmObject;

/**
 * Created by amarthus on 10-Oct-17.
 */

public class User extends RealmObject {

    public String uMail;
    public String uPassword;
    public String uName;
    public String srcLink;

    public void setuMail(String uMail) {
        this.uMail = uMail;
    }

    public void setuPassword(String uPassword) {
        this.uPassword = uPassword;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public void setSrcLink(String srcLink) {
        this.srcLink = srcLink;
    }

    public String getuName() {
        return uName;
    }

    public String getuMail() {
        return uMail;
    }

    public String getuPassword() {
        return uPassword;
    }

    public String getSrcLink() {
        return srcLink;
    }
}
