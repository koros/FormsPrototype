package com.korosmatick.formsprototype.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


import com.korosmatick.formsprototype.R;
import com.korosmatick.formsprototype.database.MySQLiteHelper;
import com.korosmatick.formsprototype.fragment.DisplayFormFragment;
import com.korosmatick.formsprototype.fragment.PreviewDataFragment;
import com.korosmatick.formsprototype.model.Form;
import com.korosmatick.formsprototype.util.FormUtils;
import com.korosmatick.formsprototype.viewpager.SampleViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by koros on 11/23/15.
 */
public class FormDataActivity extends AppCompatActivity {

    @Bind(R.id.view_pager) SampleViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    MySQLiteHelper mySQLiteHelper = null;

    private int currentPage;

    private Form form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mySQLiteHelper = MySQLiteHelper.getInstance(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        Long id = bundle.getLong("id");
        form = mySQLiteHelper.getFormById(id);

        setTitle(form.getFormName());

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new SampleFragmentPagerAdapter(getSupportFragmentManager());
        mPager.setOffscreenPageLimit(2); // corner case; prevent the offscreen fragments from being destroyed; it wont happen though but just in case
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                onPageChanged(position);
            }
        });

    }

    public void onPageChanged(int page){
        setRequestedOrientation(page == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            switchToDisplayFormFragment(1, null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }

    public void switchToDisplayFormFragment(int index, String initialData){
        DisplayFormFragment displayFormFragment = (DisplayFormFragment) findFragmentByPosition(index);
        if (displayFormFragment != null && initialData != null) {
            displayFormFragment.setFormData(initialData);
            displayFormFragment.loadFormData();
        }
        mPager.setCurrentItem(index, false);
    }

    public void switchToBaseFragment(final String data){
        int formIndex = currentPage;
        mPager.post(new Runnable() {
            @Override
            public void run() {
                mPager.setCurrentItem(0, false);
            }
        });

        try {
            PreviewDataFragment previewDataFragment = (PreviewDataFragment) findFragmentByPosition(0);
            DisplayFormFragment formFragment = (DisplayFormFragment) findFragmentByPosition(formIndex);
            if (previewDataFragment != null && data != null) {
                FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
                formUtils.saveFormData(data);
                previewDataFragment.refreshListView();
            }

            //hack reset the form
            DisplayFormFragment displayFormFragment = (DisplayFormFragment) findFragmentByPosition(formIndex);
            if (displayFormFragment != null) {
                displayFormFragment.setFormData(null);
                displayFormFragment.loadFormData();
            }

            formFragment.setRecordId(null);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        public static final String ARG_PAGE = "page";

        public SampleFragmentPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new PreviewDataFragment();
                    ((PreviewDataFragment)fragment).setTableName(form.getTableName());
                    break;
                case 1:
                    fragment = new DisplayFormFragment();
                    ((DisplayFormFragment)fragment).setForm(form);
                    break;
                default:
                    break;
            }

            Bundle args = new Bundle();
            args.putInt(ARG_PAGE, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2; // two items only at the moment
        }
    }

    @Override
    public void onBackPressed() {
        if (currentPage != 0){
            switchToBaseFragment(null);
        }else if (currentPage == 0) {
            super.onBackPressed(); // allow back key only if we are
        }
    }

}
