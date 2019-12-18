package com.thibsc.giftlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

/**
 * Your gift lists (what you have created)
 */
public class MyListFragment extends ListFragment {

    public static final String TAG_MY_LIST_FRAGMENT = "MY_LIST_FRAGMENT";

    private ListAdapter listAdapter;
    private FloatingActionButton createListButton;

    public static MyListFragment newInstance(){
        return new MyListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG_MY_LIST_FRAGMENT, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_mylist, container, false);
        listAdapter = new ListAdapter(getActivity());
        createListButton = view.findViewById(R.id.addlist_btn);
        createListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateListActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        listAdapter.add(new ListItem("Thibaut", "Noël 2020", "azerty"));
        listAdapter.add(new ListItem("Thibaut", "Anniversaire 2020", "qwerty"));
        listAdapter.getItem(0).addGift(new GiftItem("Valise eastpak", "https://www.eastpak.com/fr-fr/bagages-c140/tranverz-s-super-dreamy-pink-pEK61LA74+00+999.html", 1, false));
        listAdapter.getItem(0).addGift(new GiftItem("Raspberry pi 4", "https://www.kubii.fr/174-raspberry-pi-4", 2, true));
        listAdapter.getItem(1).addGift(new GiftItem("Licorne", "https://www.unicorn.fr/", 1, false));

        setListAdapter(listAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ListItem listItem = (ListItem) l.getItemAtPosition(position);
        Log.d(TAG_MY_LIST_FRAGMENT, listItem.getName());

        Intent intent = new Intent(getActivity(), CreateListActivity.class);
        intent.putExtra("list", listItem);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.list_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.deleteList:
                listAdapter.remove(listAdapter.getItem(info.position));
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            // From CreateListActivity
            // replace the old by the new if different
            ListItem list = (ListItem) data.getSerializableExtra("list");
            int idx = 0;
            boolean found = false;
            while (idx < listAdapter.getCount() && !found){
                if (list.equals(listAdapter.getItem(idx++))){
                    idx--;
                    found = true;
                }
            }
            if (found){
                // Remove the last
                listAdapter.remove(list);
                listAdapter.insert(list, idx);
            } else {
                list.setId(String.format("notsynchronized#%d", listAdapter.getCount()));
                listAdapter.insert(list, 0);
            }

        }
    }
}
