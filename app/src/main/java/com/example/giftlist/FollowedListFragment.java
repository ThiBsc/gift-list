package com.example.giftlist;

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
        /*for (int i=0; i<5; i++){
            listAdapter.add(new ListItem("Liste Cassy noël #"+i));
        }
        listAdapter.add(new ListItem("Liste Cassy noël 2019"));
        listAdapter.add(new ListItem("Liste Christian noël 2019"));
        listAdapter.add(new ListItem("Cassy anniversaire 2020"));
        listAdapter.add(new ListItem("Julien anniversaire 2020"));*/
        setListAdapter(listAdapter);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d(TAG_FOLLOWED_LIST_FRAGMENT, ((ListItem)l.getItemAtPosition(position)).getName());
    }
}
