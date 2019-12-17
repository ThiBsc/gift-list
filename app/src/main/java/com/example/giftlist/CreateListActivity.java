package com.example.giftlist;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

/**
 * Activity that contains the CreateListFragment
 */
public class CreateListActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createlist);

        ListItem listItem = (ListItem) getIntent().getSerializableExtra("list");

        if (listItem != null){
            EditText listName = findViewById(R.id.listName);
            listName.setText(listItem.getName());

            ListView listView = findViewById(R.id.myNewlist_gistList);
            ((GiftAdapter)listView.getAdapter()).addAll(listItem.getGifts());
        }
    }
}
