package com.mohit.newwhatsupp.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.mohit.newwhatsupp.Fragments.Status;
import com.mohit.newwhatsupp.Fragments.call;
import com.mohit.newwhatsupp.Fragments.chat;

import org.jetbrains.annotations.NotNull;

public class ViewpagerAdapter extends FragmentPagerAdapter {
    private int tabno;

    public ViewpagerAdapter(@NonNull androidx.fragment.app.FragmentManager fm, int behavior,int tabno) {
        super(fm, behavior);
        this.tabno = tabno;

    }



    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new chat();
            case 1:
                return new Status();
            case 2:
                return new call();

            default:
                return new chat();

        }

    }

    @Override
    public int getCount() {
        return tabno;
    }

}
