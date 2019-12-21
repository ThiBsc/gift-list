package com.thibsc.giftlist;

import java.io.Serializable;

import androidx.annotation.Nullable;

/**
 * Describe the content of a gift in a list
 */
public class GiftItem implements Serializable {

    private String name, url;
    private int amount;
    private UserGetter userGetter;

    class UserGetter implements Serializable {
        String user_id;
        String display_name;
    }

    public GiftItem(String name, String url, int amount){
        this.name = name;
        this.url = url;
        this.amount = amount;
        userGetter = null;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        GiftItem item = (GiftItem) obj;
        return name.equals(item.getName()) && url.equals(item.getUrl()) && amount==item.getAmount();
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getAmount() {
        return amount;
    }

    public void setUserGetter(String user_id, String display_name) {
        this.userGetter = new UserGetter();
        this.userGetter.user_id = user_id;
        this.userGetter.display_name = display_name;
    }

    public UserGetter getUserGetter() {
        return userGetter;
    }

    public boolean isBuy(){
        return userGetter != null;
    }
}
