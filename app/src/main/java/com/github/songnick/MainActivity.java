package com.github.songnick;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.github.songnick.fragment.DefineViewFragment;
import com.github.songnick.fragment.DefineViewGroupFragment;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationItemClickListener{

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout = null;
    private DefineViewFragment mUI4ViewFragment = null;
    private DefineViewGroupFragment mDefineViewGroupFragment = null;
    private FrameLayout mContainer = null;
    private Fragment mCurrentFragment = null;

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
        initDrawLayout();
    }

    private void initDrawLayout(){
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawe_layout);
        mContainer = (FrameLayout)findViewById(R.id.container_fragment);
        if (mUI4ViewFragment == null){
            mUI4ViewFragment = DefineViewFragment.newInstance();
            switchFragment(mUI4ViewFragment);
        }
    }

    @Override
    public void onItemClick(int position) {
        mDrawerLayout.closeDrawers();
        switch (position){
            case 0:
                if (mUI4ViewFragment == null){
                    mUI4ViewFragment = DefineViewFragment.newInstance();
                }
                if (!mCurrentFragment.equals(mUI4ViewFragment)){
                    switchFragment(mUI4ViewFragment);
                }
                break;
            case 1:
                if (mDefineViewGroupFragment == null){
                    mDefineViewGroupFragment = DefineViewGroupFragment.newInstance();
                }
                if (!mCurrentFragment.equals(mDefineViewGroupFragment)){
                    switchFragment(mDefineViewGroupFragment);
                }
                break;
        }
    }

    private void switchFragment(Fragment fragment){
        mCurrentFragment = fragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container_fragment, fragment);
        ft.commit();
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


}
