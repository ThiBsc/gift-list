package com.example.giftlist;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Describe how to display a gift in list of gift
 */
public class GiftAdapter extends ArrayAdapter<GiftItem> {

    private LayoutInflater layoutInflater;

    public  GiftAdapter(Activity activity){
        super(activity, R.layout.line_gift);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("ListAdapter", "getView()");
        View ret = convertView;
        if (ret == null){
            ret = LayoutInflater.from(getContext()).inflate(R.layout.line_gift, parent, false);
        }
        GiftItem giftItem = getItem(position);

        TextView giftName = ret.findViewById(R.id.myGiftName);
        TextView giftUrl = ret.findViewById(R.id.myGiftUrl);
        TextView giftAmount = ret.findViewById(R.id.myGiftAmount);

        giftName.setText(giftItem.getName());
        giftUrl.setText(giftItem.getUrl());
        giftAmount.setText(String.format("x%d", giftItem.getAmount()));

        ImageView image = ret.findViewById(R.id.myListIcon);
        image.setImageResource(R.drawable.ic_card_giftcard_blue_24dp);

        return ret;
    }
}
