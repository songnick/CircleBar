package com.github.songnick;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.github.songnick.view.AccelerationProgress;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAcc = (AccelerationProgress)findViewById(R.id.acc_progress_loading_complete);
        mCountdown = (AccelerationProgress)findViewById(R.id.acc_progress_loading_time);
        mLoading = (AccelerationProgress)findViewById(R.id.acc_progress_loading);
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

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
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
    public void onDetachedFromWindow() {
        mAcc.stopLoading();
        mLoading.stopLoading();
        mCountdown.stopLoading();
        mHandler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }
}
