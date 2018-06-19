package io.wherevere.know.refresh;

/**
 * @author wherevere
 * @version 1.0
 * @time 2018/6/10 23:03
 */
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by chenj on 2016/9/28.
 */
public class PullRefreshLayout extends RelativeLayout {

    private int[] colors = {0xFFFF0000, 0xFFFF7F00, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF, 0xFF8B00FF};

    private final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;

    private MaterialProgressDrawable mProgress;

    private ValueAnimator mValueAnimator;

    private boolean mStart = false;

    private boolean mVisable = false;

    private static final int PULL_IMAGE_SIZE = 40;
    private static int PULL_IMAGE_SIZE_PX;//上拉View的大小（像素）
    private static int PULL_IMAGE_SIZE_PX_MAX ;//最大拉动的距离
    private static int PULL_IMAGE_SIZE_PX_EXECUTE ;//拉动到什么位置开始执行
    private static int PULL_IMAGE_SIZE_PX_EXECUTE_REFRESH ;//刷新是所在的位置
    private static float ROTATE_ANIM_ANGLE_PER;//根据最大距离计算旋转角度的比列
    // private static final
    private ImageView mImageView;
    private boolean mIsFirst;

    private int mStartY, mLastY;
    private RecyclerView mRecyclerView;
    //private int mFirstVisiblePosition;
    private int mLastVisiblePosition;

    private boolean mIsCanScoll;

    private boolean mVisibleCanScoll;

    private boolean mPrepareAnimation;//准备执行动画

    private boolean mIsAllowLoadMore = true;//是否可以上拉刷新

    private boolean mIsDispatch = true;//是否分发事件

    public PullRefreshLayout(Context context) {
        this(context, null);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


    }

    /**
     * 是否允许下拉加载更多
     * @param allowLoadMore
     */
    public void setAllowLoadMore(boolean allowLoadMore) {
        mIsAllowLoadMore = allowLoadMore;
    }

    public boolean isAllowLoadMore() {
        return mIsAllowLoadMore;
    }

    /**
     * 设置进度圈的颜色
     * @param colors 如：0xFFFF0000
     */
    public void setColorSchemeColors(int... colors){
        this.colors = colors;
    }
    /**
     * 设置进度圈的颜色
     * @param colorResIds 如：R.color.red
     */
    public void setColorSchemeResources(@ColorRes int... colorResIds) {
        final Resources res = getResources();
        int[] colorRes = new int[colorResIds.length];
        for (int i = 0; i < colorResIds.length; i++) {
            colorRes[i] = res.getColor(colorResIds[i]);
        }
        setColorSchemeColors(colorRes);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!mIsFirst) {
            createProgressView();
            over:
            for (int i = 0; i < getChildCount(); i++) {
                View childView = getChildAt(i);
                if (childView instanceof SwipeRefreshLayout) {
                    ViewGroup viewGroup = (ViewGroup) childView;
                    for (int j = 0; j < viewGroup.getChildCount(); j++) {
                        View childViewJ = viewGroup.getChildAt(j);
                        if (childViewJ instanceof RecyclerView) {
                            mRecyclerView = (RecyclerView) childViewJ;
                            break over;
                        }
                    }
                }
                if(childView instanceof RecyclerView){
                    mRecyclerView = (RecyclerView) childView;
                    break over;
                }
            }
            mIsFirst = true;
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if(!mIsAllowLoadMore) return super.dispatchTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:

                if (!mStart) {
                    //如果不满足上拉的条件就直接分发事件
                    if(!canPullUp()){
                        return super.dispatchTouchEvent(event);
                    }
                    if (canPullUp() && !mIsCanScoll) {
                        showRefreshArrow();
                        mStartY = (int) event.getRawY();
                        mIsCanScoll = true;
                    } else {
                        //mStartY = (int) event.getRawY();
                        //hideRefreshArrow();
                        //hide();
                    }
                    if (mVisibleCanScoll) {
                        int endY = (int) event.getRawY();
                        int offset = mStartY - endY;
                        //System.out.println("----------------------offset:" + offset);
                        LayoutParams lp = (LayoutParams) mImageView.getLayoutParams();
                        int bottomMargin = lp.bottomMargin;
                        bottomMargin += offset;
                        if (bottomMargin >= PULL_IMAGE_SIZE_PX_MAX) {
                            bottomMargin = PULL_IMAGE_SIZE_PX_MAX;
                        }

                        if (bottomMargin <= -PULL_IMAGE_SIZE_PX) {
                            bottomMargin = -PULL_IMAGE_SIZE_PX;

                        }
                        lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, bottomMargin);
                        mImageView.setLayoutParams(lp);

                        rotateAniamtor(bottomMargin * ROTATE_ANIM_ANGLE_PER);

                        mStartY = endY;
                    }

                    LayoutParams lp = (LayoutParams) mImageView.getLayoutParams();
                    //如果按住上拉时，上拉箭头向下滑动的时候事件不应分发
                    if(mVisable && lp.bottomMargin > -PULL_IMAGE_SIZE_PX){
                        mIsDispatch = false;
                    }else if(mVisable && lp.bottomMargin == -PULL_IMAGE_SIZE_PX){//等到上拉箭头被隐藏掉的时候在分发事件
                        mIsDispatch = true;
                    }

                    //是否分发事件
                    if(!mIsDispatch) {
                        return false;
                    }
                    else {
                        return super.dispatchTouchEvent(event);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                if (!mStart) {

                    if (mVisibleCanScoll) {
                        LayoutParams lp = (LayoutParams) mImageView.getLayoutParams();
                        if (lp.bottomMargin >= PULL_IMAGE_SIZE_PX_EXECUTE) {
                            //lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, PULL_IMAGE_SIZE_PX / 3 * 2);
                            //mImageView.setLayoutParams(lp);
                            //start();
                            getValueToTranslation();
                            mPrepareAnimation = true;

                            if (mOnPullListener != null) {
                                mOnPullListener.onLoadMore(this);
                            }
                        } else {

                            hideArrow();

                        }
                    }
                    if (!mStart && !mPrepareAnimation)
                        hideArrow();
                }

                mIsCanScoll = false;

                break;
        }

        return super.dispatchTouchEvent(event);
    }

    private void hideArrow() {
        LayoutParams lp = (LayoutParams) mImageView.getLayoutParams();
        translationTo(lp.bottomMargin,-PULL_IMAGE_SIZE_PX,false);
    }

    private void showRefreshArrow() {
        mImageView.setVisibility(View.VISIBLE);
        visable();
    }

    /**
     * 隐藏箭头显示的载体ImageView
     */
    private void hideRefreshArrow() {
        mImageView.setVisibility(View.GONE);
    }


    private boolean canPullUp() {
        if(mRecyclerView == null || mRecyclerView.getAdapter() == null) return false;
        RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        mLastVisiblePosition = getLastVisibleItemPosition();
        int count = mRecyclerView.getAdapter().getItemCount();
        if (0 == count) {
            // 没有item的时候也可以上拉加载
            return true;
        } else if (mLastVisiblePosition == (count - 1)) {
            // 滑到底部了
            if (lm.findViewByPosition(count - 1).getBottom() <= getMeasuredHeight()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取底部可见项的位置
     */
    private int getLastVisibleItemPosition() {
        RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        int lastVisibleItemPosition = 0;
        if (lm instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) lm).findLastVisibleItemPosition();
        } else if (lm instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) lm).findLastVisibleItemPosition();
        }
        return lastVisibleItemPosition;
    }


    /**
     * 创建刷新View和初始化一些数据
     */
    private void createProgressView() {
        mImageView = new ImageView(getContext());

        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PULL_IMAGE_SIZE, getContext().getResources().getDisplayMetrics());
        PULL_IMAGE_SIZE_PX = size;
        PULL_IMAGE_SIZE_PX_MAX = PULL_IMAGE_SIZE_PX * 2;
        PULL_IMAGE_SIZE_PX_EXECUTE = PULL_IMAGE_SIZE_PX;
        PULL_IMAGE_SIZE_PX_EXECUTE_REFRESH = PULL_IMAGE_SIZE_PX / 3 * 2;
        ROTATE_ANIM_ANGLE_PER = (360.0f / PULL_IMAGE_SIZE_PX_MAX);
        LayoutParams lp = new LayoutParams(size, size);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, -PULL_IMAGE_SIZE_PX);
        mImageView.setLayoutParams(lp);
        mImageView.setBackground(getShapeDrawable());

        addView(mImageView);
        mImageView.setVisibility(View.GONE);

        mProgress = new MaterialProgressDrawable(getContext(), mImageView);

        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        //圈圈颜色,可以是多种颜色
        mProgress.setColorSchemeColors(colors);
        //设置圈圈的各种大小
        mProgress.updateSizes(MaterialProgressDrawable.LARGE);

        mImageView.setImageDrawable(mProgress);
    }

    /**
     * mImageView的背景
     */
    private Drawable getShapeDrawable() {
        /**
         * <layer-list xmlns:android="http://schemas.android.com/apk/res/android" >
         <!-- 第一层  上部和左部偏移一定距离-->
         <item
         >
         <shape android:shape="oval">
         <solid android:color="#f5f5f5" />
         <!-- 描边 -->
         <stroke
         android:width="0.5dp"
         android:color="#99f5f5f5" />
         </shape>
         </item>
         <!-- 第二层 下部和有部偏移一定距离-->
         <item
         android:left="2dp"
         android:top="2dp"
         android:bottom="2dp"
         android:right="2dp">
         <shape android:shape="oval">
         <solid android:color="#ffffff" />
         <!-- 描边 -->
         <!--<stroke android:width="0.33dp" android:color="#dedede" />-->
         </shape>
         </item>
         </layer-list>
         */
        //代码实现
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setColor(Color.parseColor("#f5f5f5"));
        int stroke = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, getContext().getResources().getDisplayMetrics());
        gradientDrawable.setStroke(stroke,Color.parseColor("#99f5f5f5"));
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setShape(GradientDrawable.OVAL);
        gradientDrawable2.setColor(Color.parseColor("#ffffff"));
        LayerDrawable drawable = new LayerDrawable(new Drawable[]{gradientDrawable,gradientDrawable2});
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getContext().getResources().getDisplayMetrics());
        drawable.setLayerInset(1,padding,padding,padding,padding);////第一个参数1代表数组的第二个元素，为白色
        return drawable;
    }

    /**
     * 隐藏箭头
     */
    private void hide() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mVisable = false;
            mVisibleCanScoll = false;
        }

    }

    private void visable() {
        if (mValueAnimator == null) {
            mValueAnimator = mValueAnimator.ofFloat(0f, 1f);
            mValueAnimator.setDuration(10);
            mValueAnimator.setInterpolator(new DecelerateInterpolator());
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float n = (float) animation.getAnimatedValue();
                    //圈圈的旋转角度
                    mProgress.setProgressRotation(n * 0.5f);
                    //圈圈周长，0f-1F
                    mProgress.setStartEndTrim(0f, n * 0.8f);
                    //箭头大小，0f-1F
                    mProgress.setArrowScale(n);
                    //透明度，0-255
                    mProgress.setAlpha((int) (255 * n));
                }
            });
            mValueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mVisable = true;

                }
            });
        }

        if (!mValueAnimator.isRunning()) {
            if (!mVisable) {
                //是否显示箭头
                mProgress.showArrow(true);
                mValueAnimator.start();
                mVisibleCanScoll = true;
            }
        }
    }

    private void start() {
        if (mVisable) {
            if (!mStart) {
                mProgress.start();

                mStart = true;

            }
        }
    }

    /**
     * 计算执行动画的距离参数
     */
    private void getValueToTranslation() {
        //如果mImageView还没有被创建出来是不会执行的
        if(mImageView != null) {
            LayoutParams lp = (LayoutParams) mImageView.getLayoutParams();
            int bottomMargin = lp.bottomMargin;
            //执行平移
            translationTo(bottomMargin, PULL_IMAGE_SIZE_PX_EXECUTE_REFRESH, true);
        }
    }

    private void stop() {
        if (mStart) {
            mProgress.stop();
            mStart = false;
            mVisable = false;
            mVisibleCanScoll = false;
        }
    }


    /**
     * 执行平移动画
     */
    private void translationTo(int from,int to,final boolean isShow){
        //1.调用ofInt(int...values)方法创建ValueAnimator对象
        ValueAnimator mAnimator = ValueAnimator.ofInt(from,to);
        //2.为目标对象的属性变化设置监听器
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 3.为目标对象的属性设置计算好的属性值
                int animatorValue = (int)animation.getAnimatedValue();
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) mImageView.getLayoutParams();
                marginLayoutParams.bottomMargin = animatorValue;
                mImageView.setLayoutParams(marginLayoutParams);
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(isShow){
                    start();
                    mPrepareAnimation = false;
                }else{
                    hideRefreshArrow();
                    hide();
                }

            }
        });
        //4.设置动画的持续时间、是否重复及重复次数等属性
        mAnimator.setDuration(100);
        //mAnimator.setRepeatCount(3);
        mAnimator.setRepeatMode(ValueAnimator.INFINITE);
        //5.为ValueAnimator设置目标对象并开始执行动画
        mAnimator.setTarget(mImageView);
        mAnimator.start();
    }

    /**
     * 旋转动画效果
     */
    private void rotateAniamtor(float from){

        ObjectAnimator mAnimatorRotate = ObjectAnimator.ofFloat(mImageView, "rotation",from,from + 1);
        mAnimatorRotate.setRepeatMode(Animation.INFINITE);
        mAnimatorRotate.setRepeatCount(1);
        mAnimatorRotate.setDuration(10);
        mAnimatorRotate.start();
    }


    /**
     * 加载更多或停止加载更多
     */
    public void setRefreshing(boolean refreshing) {
        if(!mIsAllowLoadMore) return;
        if(refreshing){
            if(mStart) return;
            showRefreshArrow();
            getValueToTranslation();
            mPrepareAnimation = true;

            if (mOnPullListener != null) {
                mOnPullListener.onLoadMore(this);
            }
            mIsCanScoll = false;
        }else {
            stop();
            hideArrow();
        }
    }

    /**
     * 当前是否在上拉刷新
     */
    public boolean isRefreshing(){
        return mStart;
    }


    /**
     * 刷新加载回调接口
     */
    public interface OnPullListener {

        /**
         * 加载操作
         */
        void onLoadMore(PullRefreshLayout pullRefreshLayout);
    }

    private OnPullListener mOnPullListener;

    public void setOnPullListener(OnPullListener listener) {
        mOnPullListener = listener;
    }

}
