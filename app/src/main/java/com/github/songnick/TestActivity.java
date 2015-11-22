package com.github.songnick;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.github.songnick.viewgroup.ScaleViewPager;

import java.util.ArrayList;

/**
 * Created by SongNick on 15/10/25.
 */
public class TestActivity extends AppCompatActivity {

    private ScaleViewPager mViewPager = null;
    private ArrayList<Fragment> mFragmentList = null;
    private FragmentAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
//        mViewPager = (ScaleViewPager)findViewById(R.id.view_pager);
//        mFragmentList = new ArrayList<>();
//        mFragmentList.add(AccProgressbarFragment.newInstance());
//        mFragmentList.add(AccProgressbarFragment.newInstance());
//        mFragmentList.add(AccProgressbarFragment.newInstance());
//        mViewPager.setAdapter(mAdapter = new FragmentAdapter(getSupportFragmentManager(), mFragmentList));
//        mViewPager.addOnPageChangeListener(new ScaleViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                mViewPager.setScaleX(0.9f);
//                mViewPager.setScaleY(0.9f);
//                View child0 = mViewPager.getChildAt(0);
//                child0.setScaleY(0.8f);
//                child0.setScaleX(0.8f);
//
//                View child1 = mViewPager.getChildAt(1);
//                child1.setScaleY(0.6f);
//                child1.setScaleX(0.6f );
////                int width = child0.getMeasuredWidth();
////                mViewPager.setPageMargin(-200);
////                View v = mViewPager.getChildAt(1);
////                v.layout(v.getLeft() - 100, v.getTop(), v.getRight(), v.getBottom());
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
//        mViewPager.setPageTransformer(false, new ScaleViewPager.PageTransformer() {
//            @Override
//            public void transformPage(View page, float position) {
//
//            }
//        });
    }

    private final class FragmentAdapter extends FragmentPagerAdapter {

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
