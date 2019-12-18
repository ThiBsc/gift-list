package com.example.giftlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SelectGiftFragment extends Fragment {

    private TextView listName, creator;
    private ListView giftLift;
    private GiftAdapter giftAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selectgift, container, false);

        listName = view.findViewById(R.id.selectGift_listName);
        creator = view.findViewById(R.id.selectGift_creator);
        giftLift = view.findViewById(R.id.selectGift_giftList);
        giftLift.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*ListItem listItem = (ListItem) l.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), CreateListActivity.class);
                intent.putExtra("list", listItem);
                startActivityForResult(intent, 1);*/
            }
        });

        giftAdapter = new GiftAdapter(getActivity(), true);
        giftLift.setAdapter(giftAdapter);
        return view;
    }
}
