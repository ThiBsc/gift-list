package com.thibsc.giftlist;

import java.io.Serializable;

import androidx.annotation.Nullable;

/**
 * Describe the content of a gift in a list
 */
public class GiftItem implements Serializable {

    private String name, url;
    private int amount;
    private boolean buy;

    public GiftItem(String name, String url, int amount, boolean buy){
        this.name = name;
        this.url = url;
        this.amount = amount;
        this.buy = buy;
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

    public boolean isBuy() {
        return buy;
    }
}
