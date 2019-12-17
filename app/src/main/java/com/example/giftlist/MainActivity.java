package com.example.giftlist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.mainViewPager);

        pageAdapter = new PageAdapter(getSupportFragmentManager(), getBaseContext());
        viewPager.setAdapter(pageAdapter);

        tabLayout = findViewById(R.id.mainTabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}
