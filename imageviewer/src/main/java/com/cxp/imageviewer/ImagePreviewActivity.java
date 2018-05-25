package com.cxp.imageviewer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;

public class ImagePreviewActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private TextView tvPosition;
    private TextView tvTotal;
    private ArrayList<String> list;
    private ArrayList<ImagePreviewFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_preview);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tvPosition = (TextView) findViewById(R.id.tv_position);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        list = (ArrayList<String>) getIntent().getSerializableExtra("list");
        int position = getIntent().getIntExtra("position", 0);
        if (list == null) {
            list = new ArrayList<>();
        }
        fragments = new ArrayList<>();
        for (String s : list) {
            fragments.add(ImagePreviewFragment.newInstance(s));
        }
        tvTotal.setText(String.valueOf(list.size()));
        InnerAD adapter = new InnerAD(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(position);
        tvPosition.setText(String.valueOf(position + 1));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position >= 1 && position < fragments.size() - 1) {
            fragments.get(position - 1).reset();
            fragments.get(position + 1).reset();
        }
    }

    @Override
    public void onPageSelected(final int position) {
        tvPosition.setText(String.valueOf(position + 1));

}
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    protected class InnerAD extends FragmentPagerAdapter {

        public InnerAD(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position);
            if (fragment instanceof ImagePreviewFragment) {
                ((ImagePreviewFragment) fragment).reset();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return fragments == null ? 0 : fragments.size();
        }
    }
}
