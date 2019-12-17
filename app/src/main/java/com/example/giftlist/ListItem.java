package com.example.giftlist;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Describe the content of an item in the list
 */
public class ListItem implements Serializable {

    private String creator, name, id;
    private ArrayList<GiftItem> gifts;

    public ListItem(String creator, String name){
        this.creator = creator;
        this.name = name;
        id = "";
        gifts = new ArrayList<GiftItem>();
    }

    public String getCreator() {
        return creator;
    }

    public String getName() {
        return name;
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
