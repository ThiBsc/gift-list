package com.example.giftlist;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class SelectGiftActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectgift);

        ListItem listItem = (ListItem) getIntent().getSerializableExtra("list");

        if (listItem != null){
            // Set the name of the list
            TextView listName = findViewById(R.id.selectGift_listName);
            listName.setText(listItem.getName());

            // Set the creator
            TextView creator = findViewById(R.id.selectGift_creator);
            creator.setText(listItem.getCreator());

            // Set the gifts of the list
            ListView listView = findViewById(R.id.selectGift_giftList);
            ((GiftAdapter)listView.getAdapter()).addAll(listItem.getGifts());

            // Set the id of the list
            TextView id = findViewById(R.id.listId);
            id.setText(listItem.getId());
        }
    }
}
