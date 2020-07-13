package com.thibsc.giftlist;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Describe how to display an item in the both lists (MyListFragment and FollowedListFragment)
 */
public class ListAdapter extends ArrayAdapter<ListItem> {

    public  ListAdapter(Activity activity){
        super(activity, R.layout.line_list);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("ListAdapter", "getView()");
        View ret = convertView;
        if (ret == null){
            ret = LayoutInflater.from(getContext()).inflate(R.layout.line_list, parent, false);
        }
        ListItem listItem = getItem(position);

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

            String followed_text = getContext().getResources().getString(R.string.followed_amount);
            String followed_amount = getContext().getResources().getString(followers > 1 ? R.string.persons : R.string.person);
            followerAmount.setText(String.format("%s %d %s", followed_text, followers, followed_amount));
        } else {
            followerAmount.setVisibility(View.INVISIBLE);
        }

        return ret;
    }
}
