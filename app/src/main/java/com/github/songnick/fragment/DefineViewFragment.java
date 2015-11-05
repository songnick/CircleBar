package com.github.songnick.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.songnick.AccProgressbarFragment;
import com.github.songnick.R;
import com.github.songnick.RefreshProgressbarFragment;
import com.github.songnick.SplashProFragment;

import java.util.ArrayList;

/**
 * create an instance of this fragment.
 */
public class DefineViewFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ViewPager mViewPager = null;
    private TabLayout mTabLayout = null;
    private ArrayList<android.support.v4.app.Fragment> mFragmentList = null;
    private FragmentAdapter mAdapter = null;
    private DrawerLayout mDrawerLayout = null;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DefineViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DefineViewFragment newInstance(String param1, String param2) {
        DefineViewFragment fragment = new DefineViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static DefineViewFragment newInstance() {
        DefineViewFragment fragment = new DefineViewFragment();
        return fragment;
    }

    public DefineViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.define_ui_view_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        mFragmentList = new ArrayList<>();
        mFragmentList.add(AccProgressbarFragment.newInstance());
        mFragmentList.add(RefreshProgressbarFragment.newInstance());
        mFragmentList.add(SplashProFragment.newInstance());
        mViewPager.setAdapter(mAdapter = new FragmentAdapter(getFragmentManager(), mFragmentList));
    }

    private void initView(View view){
        mViewPager = (ViewPager)view.findViewById(R.id.view_pager);
        mTabLayout = (TabLayout)view.findViewById(R.id.tab_layout);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private final class FragmentAdapter extends FragmentPagerAdapter {

        private ArrayList<android.support.v4.app.Fragment> fragments = null;

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return fragments.get(position);
        }

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
        }

        public FragmentAdapter(FragmentManager fm, ArrayList<android.support.v4.app.Fragment> fragmentArrayList){
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
