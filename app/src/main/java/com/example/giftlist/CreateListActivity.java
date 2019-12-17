package com.example.giftlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
            // Set the name of the list
            EditText listName = findViewById(R.id.listName);
            listName.setText(listItem.getName());

            // Set the gifts of the list
            ListView listView = findViewById(R.id.myNewlist_gistList);
            ((GiftAdapter)listView.getAdapter()).addAll(listItem.getGifts());

            // Set the id of the list
            TextView id = findViewById(R.id.listId);
            id.setText(listItem.getId());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            GiftItem gift = (GiftItem) data.getSerializableExtra("gift");

            ListView listView = findViewById(R.id.myNewlist_gistList);
            ((GiftAdapter)listView.getAdapter()).add(gift);
        }
    }
}
