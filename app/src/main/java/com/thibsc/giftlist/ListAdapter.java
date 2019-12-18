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

    private LayoutInflater layoutInflater;

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

        listName.setText(listItem.getName());
        listCreator.setText(listItem.getCreator());
        listAmount.setText(String.format("(%d)", listItem.getGifts().size()));

        return ret;
    }
}
