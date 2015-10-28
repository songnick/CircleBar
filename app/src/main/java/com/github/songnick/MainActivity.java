package com.github.songnick;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ViewPager mViewPager = null;
    private TabLayout mTabLayout = null;
    private ArrayList<Fragment> mFragmentList = null;
    private FragmentAdapter mAdapter = null;
    private DrawerLayout mDrawerLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_format_list_bulleted_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
//        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
//        TabLayout.Tab tab = tabLayout.newTab().setCustomView(R.layout.abc_search_view);
//        tabLayout.addTab(tabLayout.newTab().setText("AccProgressBar"));
//        tabLayout.addTab(tabLayout.newTab().setText("RefreshProgressBar"));
//        tabLayout.addTab(tabLayout.newTab().setText("SplashProgressBar"));
//        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        initView();
        mFragmentList = new ArrayList<>();
        mFragmentList.add(AccProgressbarFragment.newInstance());
        mFragmentList.add(RefreshProgressbarFragment.newInstance());
        mFragmentList.add(SplashProFragment.newInstance());
        mViewPager.setAdapter(mAdapter = new FragmentAdapter(getSupportFragmentManager(), mFragmentList));
    }

    private void initView(){
        mViewPager = (ViewPager)findViewById(R.id.view_pager);
        mTabLayout = (TabLayout)findViewById(R.id.tab_layout);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawe_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText("Acc"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Refresh"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Splash"));
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mTabLayout.setScrollPosition(position, positionOffset, true);

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final class FragmentAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments = null;

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
        }

        public FragmentAdapter(FragmentManager fm, ArrayList<Fragment> fragmentArrayList){
            super(fm);
            if (fragmentArrayList != null){
                fragments = fragmentArrayList;
            }
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
