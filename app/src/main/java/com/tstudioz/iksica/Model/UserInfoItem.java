package com.tstudioz.iksica.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by etino7 on 20-Oct-17.
 */

public class UserInfoItem  extends RealmObject{

    @PrimaryKey
    public int index;

    public String itemTitle;
    public String itemDesc;

    public void setindex(int index) {
        this.index = index;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public int getindex() {
        return index;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemDesc() {
        return itemDesc;
    }

}
