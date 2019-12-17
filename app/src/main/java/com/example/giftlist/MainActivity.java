package com.example.giftlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

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
