package com.thibsc.giftlist;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * Describe the content of an item in the list
 * It's mapped from the database structure to use:
 * document.toObject(ListItem.class);
 */
public class ListItem implements Serializable {

    private String creator, creator_displayname, name;
    private ArrayList<String> followers;
    private ArrayList<GiftItem> gifts;

    public ListItem(){
        followers = new ArrayList<>();
        gifts = new ArrayList<>();
    }

    public ListItem(DocumentReference creator, String creator_displayname, String name, ArrayList<DocumentReference> followers, ArrayList<GiftItem> gifts){
        this.creator = creator.getPath();
        this.creator_displayname = creator_displayname;
        this.name = name;
        this.followers = new ArrayList<>();
        setFollowers(followers);
        this.gifts = gifts;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        ListItem item = (ListItem) obj;
        return getId().equals(item.getId()) && !item.getId().isEmpty();
    }

    public String getId() {
        return creator+"_"+name;
    }

    public DocumentReference getCreator(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.document(creator);
    }

    public void setCreator(DocumentReference creator) {
        this.creator = creator.getPath();
    }

    public String getCreator_displayname() {
        return creator_displayname;
    }

    public void setCreator_displayname(String creator_displayname) {
        this.creator_displayname = creator_displayname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<GiftItem> getGifts() {
        return gifts;
    }

    public void setGifts(ArrayList<GiftItem> gifts) {
        this.gifts = gifts;
    }

    public void addGift(GiftItem gift){
        gifts.add(gift);
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<DocumentReference> followers) {
        for (DocumentReference dr : followers){
            this.followers.add(dr.getPath());
        }
    }

    public boolean addFollower(String follower){
        boolean ret = false;
        if (!followers.contains(follower)){
            followers.add(follower);
            ret = true;
        }
        return ret;
    }
}
