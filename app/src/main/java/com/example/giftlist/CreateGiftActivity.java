package com.example.giftlist;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

/**
 * Activity that contains the CreateGiftFragment
 */
public class CreateGiftActivity extends FragmentActivity {

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creategift);
    }
}
