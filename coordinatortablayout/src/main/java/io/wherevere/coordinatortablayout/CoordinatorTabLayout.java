package io.wherevere.coordinatortablayout;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * @author wherever
 * @version 1.0
 * @time 2018/4/28 21:11
 */
public class CoordinatorTabLayout extends CoordinatorLayout {

    public Context mContext;
    public int[] mImageArray, mColorArray;
    public CollapsingToolbarLayout mCollapsingToolbarLayout;
    public ImageView mImageView;
    public Toolbar mToolbar;
    public ActionBar mActionbar;
    public TabLayout mTabLayout;

    private LoadHeaderImagesListener mLoadHeaderImagesListener;
    private TabLayout.OnTabSelectedListener mOnTabSelectedListener;

    public CoordinatorTabLayout(Context context) {
        super(context);
        this.mContext = context;
    }

    public CoordinatorTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        if (!isInEditMode()) {
            initView(mContext);
        }
    }

    public CoordinatorTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        if (!isInEditMode()) {
            initView(mContext);
        }
    }

    public void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_coordinatortablayout, this, true);
        mCollapsingToolbarLayout = findViewById(R.id.collapsingtoolbarlayout);
        mToolbar = findViewById(R.id.toolbar);
        ((AppCompatActivity) context).setSupportActionBar(mToolbar);
        if (((AppCompatActivity) context).getSupportActionBar() != null) {
            mActionbar = ((AppCompatActivity) context).getSupportActionBar();
        }
        mImageView = findViewById(R.id.imageview);
        mTabLayout = findViewById(R.id.tabLayout);
    }

    /**
     * 获取该组件中的ActionBar
     */
    public ActionBar getActionBar() {
        return mActionbar;
    }

    /**
     * 设置Toolbar标题
     *
     * @param title 标题
     * @return CoordinatorTabLayout
     */
    public CoordinatorTabLayout setTitle(String title) {
        if (mActionbar != null) {
            mActionbar.setTitle(title);
        }
        return this;
    }

    /**
     * 设置Toolbar显示返回按钮及标题
     *
     * @param isDisplay 是否显示Home按钮及设置图标
     * @return CoordinatorTabLayout
     */
    public CoordinatorTabLayout setHomeIconAndIsDisplay(boolean isDisplay, int drawable) {
        if (isDisplay && mActionbar != null) {
            mActionbar.setDisplayHomeAsUpEnabled(true);
            mActionbar.setHomeAsUpIndicator(drawable);
        }
        return this;
    }

    /**
     * 设置每个tab对应项的文本颜色
     *
     * @return CoordinatorTabLayout
     */
    public CoordinatorTabLayout setTabTextColors(int colorValue) {
        mTabLayout.setTabTextColors(ColorStateList.valueOf(colorValue));
        return this;
    }

    /**
     * 设置每个tab对应项的指示器颜色
     *
     * @return CoordinatorTabLayout
     */
    public CoordinatorTabLayout setSelectedTabIndicatorColor(int colorValue) {
        mTabLayout.setSelectedTabIndicatorColor(colorValue);
        return this;
    }

    /**
     * 设置每个tab对应的ContentScrimColor
     *
     * @param colorArray 图片数组
     * @return CoordinatorTabLayout
     */
    public CoordinatorTabLayout setContentScrimColorArray(@NonNull int[] colorArray) {
        mColorArray = colorArray;
        return this;
    }

    /**
     * 设置每个tab对应的头部图片
     *
     * @param imageArray 图片数组
     * @return CoordinatorTabLayout
     */
    public CoordinatorTabLayout setImageArray(@NonNull int[] imageArray) {
        mImageArray = imageArray;
        return this;
    }

    /**
     * 设置TabLayout TabMode
     *
     * @param mode 固定模式或者可滑动模式
     * @return CoordinatorTabLayout
     */
    public CoordinatorTabLayout setTabMode(@TabLayout.Mode int mode) {
        mTabLayout.setTabMode(mode);
        return this;
    }

    /**
     * 设置与该组件搭配的ViewPager
     *
     * @param viewPager 与TabLayout结合的ViewPager
     * @return CoordinatorTabLayout
     */
    public CoordinatorTabLayout setupWithViewPager(ViewPager viewPager) {
        startTabLayout();
        mTabLayout.setupWithViewPager(viewPager);
        return this;
    }

    public void startTabLayout() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mImageView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_dismiss));

                if (mLoadHeaderImagesListener == null) {
                    if (mImageArray != null) {
                        mImageView.setImageResource(mImageArray[tab.getPosition()]);
                    }
                } else {
                    mLoadHeaderImagesListener.loadHeaderImages(mImageView, tab);
                }
                if (mColorArray != null) {
                    mCollapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(mContext, mColorArray[tab.getPosition()]));
                }
                mImageView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_show));

                if (mOnTabSelectedListener != null) {
                    mOnTabSelectedListener.onTabSelected(tab);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (mOnTabSelectedListener != null) {
                    mOnTabSelectedListener.onTabUnselected(tab);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (mOnTabSelectedListener != null) {
                    mOnTabSelectedListener.onTabReselected(tab);
                }
            }
        });
    }

    /**
     * 设置透明状态栏
     *
     * @param activity 当前展示的activity
     * @return CoordinatorTabLayout
     */
    public CoordinatorTabLayout setTranslucentStatusBar(@NonNull Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return this;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (mToolbar != null) {
            MarginLayoutParams layoutParams = (MarginLayoutParams) mToolbar.getLayoutParams();
            layoutParams.setMargins(
                    layoutParams.leftMargin,
                    layoutParams.topMargin + SystemView.getStatusBarHeight(activity),
                    layoutParams.rightMargin,
                    layoutParams.bottomMargin);
        }
        return this;
    }

    /**
     * 设置LoadHeaderImagesListener
     *
     * @param loadHeaderImagesListener 设置LoadHeaderImagesListener
     * @return CoordinatorTabLayout
     */
    public CoordinatorTabLayout setLoadHeaderImagesListener(LoadHeaderImagesListener loadHeaderImagesListener) {
        mLoadHeaderImagesListener = loadHeaderImagesListener;
        return this;
    }

    /**
     * 设置onTabSelectedListener
     *
     * @param onTabSelectedListener 设置onTabSelectedListener
     * @return CoordinatorTabLayout
     */
    public CoordinatorTabLayout addOnTabSelectedListener(TabLayout.OnTabSelectedListener onTabSelectedListener) {
        mOnTabSelectedListener = onTabSelectedListener;
        return this;
    }
}
