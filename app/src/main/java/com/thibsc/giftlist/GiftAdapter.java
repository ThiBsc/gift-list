package com.thibsc.giftlist;

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
    private boolean displayBuyIndicator;

    public  GiftAdapter(Activity activity, boolean displayBuyIndicator){
        super(activity, R.layout.line_gift);
        this.displayBuyIndicator = displayBuyIndicator;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("ListAdapter", "getView()");
        View ret = convertView;
        if (ret == null){
            ret = LayoutInflater.from(getContext()).inflate(R.layout.line_gift, parent, false);
        }
        GiftItem giftItem = getItem(position);

        ImageView indicator = ret.findViewById(R.id.buyIndicator);
        if (!(displayBuyIndicator && giftItem.isBuy())) {
            indicator.setVisibility(View.GONE);
        } else {
            indicator.setVisibility(View.VISIBLE);
        }

        TextView giftName = ret.findViewById(R.id.myGiftName);
        TextView giftUrl = ret.findViewById(R.id.myGiftUrl);
        TextView giftAmount = ret.findViewById(R.id.myGiftAmount);

        giftName.setText(giftItem.getName());
        giftUrl.setText(giftItem.getUrl());

        String buyer = (displayBuyIndicator && giftItem.isBuy() ?
                String.format(" (%s %s)", getContext().getResources().getString(R.string.get_by), giftItem.getGet_by().get("user_displayname"))
                : "");
        giftAmount.setText(String.format("x%d%s", giftItem.getAmount(), buyer));

        return ret;
    }
}
