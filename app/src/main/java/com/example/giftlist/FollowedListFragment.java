package com.example.giftlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

/**
 * The lists that you following
 */
public class FollowedListFragment extends ListFragment {

    public static final String TAG_FOLLOWED_LIST_FRAGMENT = "FOLLOWED_LIST_FRAGMENT";

    private ListAdapter listAdapter;

    public static FollowedListFragment newInstance(){
        return new FollowedListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG_FOLLOWED_LIST_FRAGMENT, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_followedlist, container, false);
        listAdapter = new ListAdapter(getActivity());

        listAdapter.add(new ListItem("john", "Christmas 2019", "a"));
        listAdapter.add(new ListItem("jerry", "Christmas 2020", "b"));
        listAdapter.add(new ListItem("john", "Birthday 2020", "c"));
        listAdapter.add(new ListItem("jerry", "Party Ibiza 2k21", "d"));
        listAdapter.getItem(0).addGift(new GiftItem("Valise eastpak", "https://www.eastpak.com/fr-fr/bagages-c140/tranverz-s-super-dreamy-pink-pEK61LA74+00+999.html", 1, true));
        listAdapter.getItem(0).addGift(new GiftItem("Raspberry pi 4", "https://www.kubii.fr/174-raspberry-pi-4", 2, false));
        listAdapter.getItem(0).addGift(new GiftItem("USB type c cable", "", 1, true));
        listAdapter.getItem(1).addGift(new GiftItem("Licorne", "https://www.unicorn.fr/", 1, false));

        setListAdapter(listAdapter);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ListItem listItem = (ListItem) l.getItemAtPosition(position);
        Log.d(TAG_FOLLOWED_LIST_FRAGMENT, listItem.getName());

        Intent intent = new Intent(getActivity(), SelectGiftActivity.class);
        intent.putExtra("list", listItem);
        startActivity(intent);
    }
}
