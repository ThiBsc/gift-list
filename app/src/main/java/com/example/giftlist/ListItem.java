package com.example.giftlist;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * Describe the content of an item in the list
 */
public class ListItem implements Serializable {

    private String creator, name, id;
    private ArrayList<GiftItem> gifts;

    public ListItem(String creator, String name, String id){
        this.creator = creator;
        this.name = name;
        this.id = id;
        gifts = new ArrayList<GiftItem>();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        ListItem item = (ListItem) obj;
        return id.equals(item.getId()) && !item.getId().isEmpty();
    }

    public String getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void addGift(GiftItem gift){
        gifts.add(gift);
    }

    public void delGift(GiftItem gift){
        gifts.remove(gift);
    }

    public ArrayList<GiftItem> getGifts() {
        return gifts;
    }
}
