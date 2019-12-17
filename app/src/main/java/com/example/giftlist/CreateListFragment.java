package com.example.giftlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * The fragment that allow you to create a gift list
 */
public class CreateListFragment extends Fragment {

    private EditText nameList;
    private Button btnAddGift;
    private ListView giftList;
    private FloatingActionButton saveListBtn;
    private GiftAdapter giftAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createlist, container, false);

        nameList = view.findViewById(R.id.listName);
        btnAddGift = view.findViewById(R.id.btnAddGift);
        giftList = view.findViewById(R.id.myNewlist_gistList);
        saveListBtn = view.findViewById(R.id.savelist_btn);

        giftAdapter = new GiftAdapter(getActivity());
        giftList.setAdapter(giftAdapter);

        btnAddGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateGiftActivity.class);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }
}
