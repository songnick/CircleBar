package com.github.songnick.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.github.songnick.R;
import com.github.songnick.androidview.NoSlideViewPager;
import com.github.songnick.androidview.SlidProgressbar;

import java.util.ArrayList;

/**
 *SongNick
 */
public class AndView4CustomViewFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private NoSlideViewPager mViewPager = null;
    private SlidPagerAdapter mAdapter = null;
    private ArrayList<View> mViewList = null;
    private RadioGroup mTab = null;
    private SlidProgressbar mHorizontalProgressbar = null;
    private int mSeekSize = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (mSeekSize <= 100){
                mHorizontalProgressbar.seek(++mSeekSize);
                sendEmptyMessageDelayed(0, 50);
            }
        }
    };


    public static AndView4CustomViewFragment newInstance() {
        AndView4CustomViewFragment fragment = new AndView4CustomViewFragment();
        return fragment;
    }

    public AndView4CustomViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mViewList = new ArrayList<>();
        View horizontal = inflater.inflate(R.layout.horizontal_slid_progressbar, null);
        mHorizontalProgressbar = (SlidProgressbar)horizontal.findViewById(R.id.horizontal_slid_progressbar);
        mViewList.add(horizontal);
        mViewList.add(inflater.inflate(R.layout.vertical_slid_progressbar, null));
        return inflater.inflate(R.layout.fragment_customeview_androidview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager = (NoSlideViewPager)view.findViewById(R.id.orientation_display);
        mViewPager.setAdapter(mAdapter = new SlidPagerAdapter());
        mTab = (RadioGroup)view.findViewById(R.id.display_tab);
        mTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.horizontal_item:
                        if (mViewPager.getCurrentItem() != 0){
                            mViewPager.setCurrentItem(0, true);
                        }
                        break;

                    case R.id.vertical_item:
                        if (mViewPager.getCurrentItem() != 1){
                            mViewPager.setCurrentItem(1, true);
                        }
                        break;
                }
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mHandler.sendEmptyMessageDelayed(0, 500);
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
    public class SlidPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            container.removeView(mViewList.get(position));
        }
    }
}
