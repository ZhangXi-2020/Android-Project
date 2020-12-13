package com.zx.tinytiktok;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import com.zx.tinytiktok.Fragment.DisplayFragment;
import com.zx.tinytiktok.Fragment.ShotFragment;
import com.zx.tinytiktok.Fragment.UploadFragment;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FmPagerAdapter pagerAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] titles = new String[]{"视频展示","视频拍摄","视频上传"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 添加 ViewPager 和 Fragment 做可滑动界面
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new FmPagerAdapter(fragments, getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        //添加 TabLayout 支持 3个Tab
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        // 视频展示
        fragments.add(new DisplayFragment());
        tabLayout.addTab(tabLayout.newTab());
        pagerAdapter.notifyDataSetChanged();
        // 视频拍摄
        fragments.add(new ShotFragment());
        tabLayout.addTab(tabLayout.newTab());
        pagerAdapter.notifyDataSetChanged();
        // 视频上传
        fragments.add(new UploadFragment());
        tabLayout.addTab(tabLayout.newTab());
        pagerAdapter.notifyDataSetChanged();

        tabLayout.setupWithViewPager(viewPager,false);
        for(int i=0; i<titles.length; i++){
            tabLayout.getTabAt(i).setText(titles[i]);
        }
    }
}