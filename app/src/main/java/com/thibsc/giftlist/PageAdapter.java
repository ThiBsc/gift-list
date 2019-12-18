package com.thibsc.giftlist;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * The page manager (to switch between your lists and the followed lists)
 */
public class PageAdapter extends FragmentPagerAdapter {

    private Context context;

    public PageAdapter(FragmentManager fm, Context ctx){
        super(fm);
        context = ctx;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = MyListFragment.newInstance();
                break;
            case 1:
                fragment = FollowedListFragment.newInstance();
                break;
            default:
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence title = null;
        switch (position){
            case 0:
                title = context.getString(R.string.my_list);
                break;
            case 1:
                title = context.getString(R.string.followed_list);
                break;
            default:
                break;
        }
        return title;
    }
}
