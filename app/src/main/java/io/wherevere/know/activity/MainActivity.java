package io.wherevere.know.activity;

import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.wherevere.coordinatortablayout.CoordinatorTabLayout;
import io.wherevere.know.adapter.PagerAdapter;
import io.wherevere.know.R;
import io.wherevere.know.fragment.ClassifyFragment;
import io.wherevere.know.fragment.NewsFragment;

public class MainActivity extends AppCompatActivity {

    public DrawerLayout mDrawerLayout;
    public NavigationView mNavigationView;
    View headerView;
    ImageView imageview;
    CircleImageView circleimageview;


    public List<Fragment> mFragmentList;
    public ViewPager mViewPager;
    public String[] mTitle = {"最新", "分类"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int[] imageArray = new int[]{
                R.mipmap.bg_ios,
                R.mipmap.bg_other};
        int[] colorArray = new int[]{
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light};

        initFragment();
        initViewPager();

        mDrawerLayout = findViewById(R.id.drawerlayout);
        mNavigationView = findViewById(R.id.navigation_view);
        headerView = mNavigationView.getHeaderView(0);
        imageview = headerView.findViewById(R.id.imageview);
        circleimageview = headerView.findViewById(R.id.circleimageview);

        CoordinatorTabLayout coordinatorTabLayout = findViewById(R.id.coordinatortablayout);
        coordinatorTabLayout.setTranslucentStatusBar(this)
                .setTitle("Play Android")
                .setHomeIconAndIsDisplay(true, R.mipmap.actionbar_menu)
                .setImageArray(imageArray)
                .setTabTextColors(Color.WHITE)
                .setSelectedTabIndicatorColor(Color.WHITE)
                .setContentScrimColorArray(colorArray)
                .setupWithViewPager(mViewPager);
    }

    protected void initFragment() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new NewsFragment());
        mFragmentList.add(new ClassifyFragment());
    }

    protected void initViewPager() {
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), mFragmentList, mTitle));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

}
