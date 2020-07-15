package com.thibsc.giftlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ExpandableListFollowedAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<ListItem> lists;

    public ExpandableListFollowedAdapter(Context ctx){
        this.context = ctx;
        this.lists = new ArrayList<>();
    }

    public void addList(ListItem li){
        lists.add(li);
    }

    public void clear(){
        lists.clear();
    }

    @Override
    public int getGroupCount() {
        return lists.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return lists.get(i).getGifts().size();
    }

    @Override
    public Object getGroup(int i) {
        return lists.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return lists.get(i).getGifts().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        View ret = view;
        if (ret == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ret = layoutInflater.inflate(R.layout.line_list, null, false);
        }
        ListItem listItem = (ListItem) getGroup(i);

        TextView listName = ret.findViewById(R.id.myListName);
        TextView listCreator = ret.findViewById(R.id.myListCreator);
        TextView listAmount = ret.findViewById(R.id.myListAmount);
        TextView followerAmount = ret.findViewById(R.id.myFollowerAmount);

        listName.setText(listItem.getName());
        listCreator.setText(listItem.getCreator_displayname());
        listAmount.setText(String.format("(%d)", listItem.getGifts().size()));

        int followers = listItem.getFollowers().size();
        if (followers > 0){
            followerAmount.setVisibility(View.VISIBLE);

            String followed_text = this.context.getResources().getString(R.string.followed_amount);
            String followed_amount = this.context.getResources().getString(followers > 1 ? R.string.persons : R.string.person);
            followerAmount.setText(String.format("%s %d %s", followed_text, followers, followed_amount));
        } else {
            followerAmount.setVisibility(View.INVISIBLE);
        }

        return ret;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        View ret = view;
        if (ret == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ret = layoutInflater.inflate(R.layout.line_gift, null, false);
        }
        GiftItem giftItem = (GiftItem) getChild(i, i1);

        ImageView indicator = ret.findViewById(R.id.buyIndicator);
        if (giftItem.isBuy()) {
            indicator.setVisibility(View.VISIBLE);
        } else {
            indicator.setVisibility(View.GONE);
        }

        TextView giftName = ret.findViewById(R.id.myGiftName);
        TextView giftUrl = ret.findViewById(R.id.myGiftUrl);
        TextView giftAmount = ret.findViewById(R.id.myGiftAmount);

        giftName.setText(giftItem.getName());
        giftUrl.setText(giftItem.getUrl());

        String buyer = (giftItem.isBuy() ?
                String.format(" (%s %s)", this.context.getResources().getString(R.string.get_by), giftItem.getGet_by().get("user_displayname"))
                : "");
        giftAmount.setText(String.format("x%d%s", giftItem.getAmount(), buyer));

        return ret;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
