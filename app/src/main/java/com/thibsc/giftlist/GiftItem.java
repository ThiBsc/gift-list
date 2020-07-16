package com.thibsc.giftlist;

import java.io.Serializable;
import java.util.HashMap;

import androidx.annotation.Nullable;

/**
 * Describe the content of a gift in a list
 * It's mapped from the database structure to use:
 * document.toObject(GiftItem.class);
 */
public class GiftItem implements Serializable {

    private String name, url;
    private int amount;
    private HashMap<String, String> get_by;

    public GiftItem(){
        get_by = new HashMap<>();
    }

    public GiftItem(String name, String url, int amount, HashMap<String, Object> get_by){
        this.name = name;
        this.url = url;
        this.amount = amount;
        this.get_by = new HashMap<>();

        if (!get_by.isEmpty()) {
            setGet_by(get_by);
        }
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

    public HashMap<String, String> getGet_by() {
        return get_by;
    }

    public void setGet_by(HashMap<String, Object> get_by) {
        if (get_by != null && !get_by.isEmpty()) {
            this.get_by.put("user", get_by.get("user").toString());
            this.get_by.put("user_displayname", get_by.get("user_displayname").toString());
        } else {
            this.get_by.clear();
        }
    }

    public boolean isBuy(){
        return !get_by.isEmpty();
    }
}
