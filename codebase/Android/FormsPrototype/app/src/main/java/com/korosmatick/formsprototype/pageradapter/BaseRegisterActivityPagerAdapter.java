package com.korosmatick.formsprototype.pageradapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.korosmatick.formsprototype.fragment.DisplayFormFragment;
import com.korosmatick.formsprototype.model.Form;

import java.util.List;

/**
 * Created by koros on 11/2/15.
 */
public class BaseRegisterActivityPagerAdapter extends FragmentPagerAdapter {
    public static final String ARG_PAGE = "page";
    List<Form> dialogOptions;
    Fragment mBaseFragment;

    public BaseRegisterActivityPagerAdapter(FragmentManager fragmentManager, List<Form> dialogOptions, Fragment baseFragment) {
        super(fragmentManager);
        this.dialogOptions = dialogOptions;
        this.mBaseFragment = baseFragment;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = mBaseFragment;
                break;

            default:
                Form form = dialogOptions.get(position - 1); // account for the base fragment
                DisplayFormFragment f = new DisplayFormFragment();
                f.setForm(form);
                fragment = f;
                break;
        }

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return dialogOptions.size() + 1; // index 0 is always occupied by the base fragment
    }
}
