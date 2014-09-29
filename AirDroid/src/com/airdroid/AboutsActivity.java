package com.airdroid;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.airdroid.helper.MainPagerAdapter;

/**
 * 
 * @author hongduongvu93
 *
 * Cài đặt giao diện tab Abouts.
 */
public class AboutsActivity extends FragmentActivity {

	// Adapter hiển thị cho ViewPager.
    MainPagerAdapter mainPagerAdapter;
    // Thuộc tính là mViewPager
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab_abouts);
        // khởi tạo Adapter.
        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        // lấy ViewPager.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        // thiết đặt adapter hiển thị.
        mViewPager.setAdapter(mainPagerAdapter);
    }
}
