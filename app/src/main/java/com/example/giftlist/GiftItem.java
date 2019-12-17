package com.example.giftlist;

import java.io.Serializable;

/**
 * Describe the content of a gift in a list
 */
public class GiftItem implements Serializable {

    private String name, url;
    private int amount;

    public GiftItem(String name, String url, int amount){
        this.name = name;
        this.url = url;
        this.amount = amount;
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
}
