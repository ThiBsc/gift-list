package com.example.giftlist;

import java.io.Serializable;

import androidx.annotation.Nullable;

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
}
