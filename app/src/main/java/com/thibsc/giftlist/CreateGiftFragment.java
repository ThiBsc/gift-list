package com.thibsc.giftlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

/**
 * The fragment that allow you to create a gift
 */
public class CreateGiftFragment extends Fragment {

    private EditText nameGift, urlGift, amountGift;
    private FloatingActionButton saveGiftBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creategift, container, false);

        nameGift = view.findViewById(R.id.giftName);
        urlGift = view.findViewById(R.id.giftUrl);
        amountGift = view.findViewById(R.id.giftAmount);
        saveGiftBtn = view.findViewById(R.id.savegift_btn);

        saveGiftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if (nameGift.getText().length() == 0){
                    Toast.makeText(getContext(), R.string.missing_namegift, Toast.LENGTH_SHORT).show();
                } else {
                    GiftItem gift = new GiftItem(nameGift.getText().toString(), urlGift.getText().toString(), Integer.parseInt(amountGift.getText().toString()), new HashMap<String, Object>());

                    Intent data = new Intent();
                    data.putExtra("gift", gift);
                    getActivity().setResult(Activity.RESULT_OK, data);
                    getActivity().finish();
                }
            }
        });

        return view;
    }
}
