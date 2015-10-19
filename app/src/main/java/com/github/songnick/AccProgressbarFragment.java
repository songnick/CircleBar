package com.github.songnick;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.songnick.view.AccelerationProgress;

public class AccProgressbarFragment extends Fragment {

    private static final String TAG = "MainActivity";
    private static final int LOADING_MSG = 0x001;
    private static final int COMPLETED_MSG = 0x002;
    private static final int COUNTDOWN_MSG = 0x003;
    private static final int DELAY_TIME = 5 * 1000;

    private AccelerationProgress mAcc = null;
    private AccelerationProgress mCountdown = null;
    private AccelerationProgress mLoading = null;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOADING_MSG:
                    mAcc.startLoading();
                    mHandler.sendEmptyMessageDelayed(COMPLETED_MSG, DELAY_TIME);
                    break;

                case COMPLETED_MSG:
                    mAcc.loadCompleted(true);
                    mHandler.sendEmptyMessageDelayed(LOADING_MSG, DELAY_TIME);
                    break;
                case COUNTDOWN_MSG:
                    mCountdown.startLoading();
                    break;
            }
        }
    };

    public static AccProgressbarFragment newInstance(){
        return new AccProgressbarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.acc_progress_demo_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAcc = (AccelerationProgress)view.findViewById(R.id.acc_progress_loading_complete);
        mCountdown = (AccelerationProgress)view.findViewById(R.id.acc_progress_loading_time);
        mLoading = (AccelerationProgress)view.findViewById(R.id.acc_progress_loading);

    }

    @Override
    public void onResume() {
        super.onResume();
        mAcc.startLoading();
        mLoading.startLoading();
        //10 seconds
        mCountdown.setCountDownTime(10);
        mCountdown.setCountdownCompleteListener(new AccelerationProgress.CountdownCompleteListener() {
            @Override
            public void countdownComplete() {
                mHandler.sendEmptyMessageDelayed(COUNTDOWN_MSG, 2 * 1000);
            }
        });
        mCountdown.startLoading();
        mHandler.sendEmptyMessageDelayed(COMPLETED_MSG, DELAY_TIME);
//        mCountdown.setCountDownTime(10 * 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
         mAcc.stopLoading();
        mLoading.stopLoading();
        mCountdown.stopLoading();
        mHandler.removeCallbacksAndMessages(null);
    }
//    @Override
//    public void onAttachedToWindow() {
//        super.onAttachedToWindow();

//    }
//
//    @Override
//    public void onDetachedFromWindow() {
//        mAcc.stopLoading();
//        mLoading.stopLoading();
//        mCountdown.stopLoading();
//        mHandler.removeCallbacksAndMessages(null);
//        super.onDetachedFromWindow();
//    }
}
