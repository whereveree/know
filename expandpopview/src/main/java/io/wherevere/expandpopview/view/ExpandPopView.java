package io.wherevere.expandpopview.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.wherevere.expandpopview.R;
import io.wherevere.expandpopview.callback.OnOneListCallback;
import io.wherevere.expandpopview.callback.OnPopViewListener;
import io.wherevere.expandpopview.callback.OnTwoListCallback;
import io.wherevere.expandpopview.entity.KeyValue;
import io.wherevere.expandpopview.listview.PopLinearLayout;
import io.wherevere.expandpopview.listview.PopOneListView;
import io.wherevere.expandpopview.listview.PopTwoListView;

public class ExpandPopView extends LinearLayout implements PopupWindow.OnDismissListener, OnPopViewListener {
    private List<RelativeLayout> mViews;
    private ToggleButton mTbSelected;
    private FixedPopupWindow mPopupWindow;
    private Context mContext;
    private int mDisplayWidth;
    private int mDisplayHeight;

    private int mSelectPosition;
    private int mTabPosition = -1;

    private int mTbtnBackground;            //togglebutton bg
    private int mTbtnBackgroundColor;       //togglebutton bgcolor
    private int mTbtnTextColor;             //togglebutton textcolor
    private int mTbtnTextSize;              //togglebutton textsize
    private int mPopViewBackgroundColor;    //popview bgcolor
    private int mPopViewTextSize;           //popview text size
    private int mPopViewTextColor;          //popview text color
    private int mPopViewTextColorSelected;          //popview text color

    private PopTwoListView mPopTwoListView;
    private List<Integer> mTypeList;
    public static final int TYPE_ONE = 1;
    public static final int TYPE_TWO = 2;
    private Map<Integer, PopTwoListView> mTwoListMap;
    private Map<Integer, PopOneListView> mOneListMap;

    public ExpandPopView(Context context) {
        this(context, null);
    }

    public ExpandPopView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandPopView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        mViews = new ArrayList<>();
        mTypeList = new ArrayList<>();
        mTwoListMap = new HashMap<>();
        mOneListMap = new HashMap<>();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandPopView);
        mTbtnBackground = a.getResourceId(R.styleable.ExpandPopView_tab_togglebtn_bg, -1);
        mTbtnBackgroundColor = a.getColor(R.styleable.ExpandPopView_tab_togglebtn_bg_color, Color.WHITE);
        mTbtnTextSize = a.getDimensionPixelSize(R.styleable.ExpandPopView_tab_togglebtn_text_size,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
        mTbtnTextColor = a.getColor(R.styleable.ExpandPopView_tab_togglebtn_text_color, Color.BLACK);
        mPopViewBackgroundColor = a.getColor(R.styleable.ExpandPopView_tab_pop_bg_color, Color.parseColor("#b0000000"));
        mPopViewTextSize = a.getDimensionPixelSize(R.styleable.ExpandPopView_tab_pop_text_size,
                mTbtnTextSize - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1, getResources().getDisplayMetrics()));
        mPopViewTextColor = a.getColor(R.styleable.ExpandPopView_tab_pop_text_color, Color.BLACK);
        mPopViewTextColorSelected = a.getColor(R.styleable.ExpandPopView_tab_pop_text_color_selected, 0);

        a.recycle();
        mDisplayWidth = ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth();
        mDisplayHeight = ((Activity) mContext).getWindowManager().getDefaultDisplay().getHeight();
        setOrientation(HORIZONTAL);
    }

    /**
     * add onelistview to tab
     */
    public void addItemToExpandTab(String title, List<KeyValue> oneList, OnOneListCallback callback) {
        PopOneListView oneListView = new PopOneListView(mContext);
        oneListView.setData(oneList);
        oneListView.setCallback(callback);
        oneListView.setPopViewListener(this);
        oneListView.setDrawable(mPopViewTextSize, mPopViewTextColor, mPopViewTextColorSelected);
        mTypeList.add(TYPE_ONE);
        mOneListMap.put(mTabPosition + 1, oneListView);
        addItemToExpandTab(title, oneListView);
    }

    /**
     * add twolistview to tab
     */
    public void addItemToExpandTab(String title, List<KeyValue> parentList, List<List<KeyValue>> parentChild, OnTwoListCallback callback) {
        PopTwoListView twoListView = new PopTwoListView(mContext);
        twoListView.setData(parentList, parentChild);
        twoListView.setCallback(callback);
        twoListView.setPopViewListener(this);
        twoListView.setDrawable(mPopViewTextSize, mPopViewTextColor, mPopViewTextColorSelected);
        mTypeList.add(TYPE_TWO);
        mTwoListMap.put(mTabPosition + 1, twoListView);
        addItemToExpandTab(title, twoListView);
    }

    public void addItemToExpandTab(String title, PopLinearLayout tabItemView) {
        ToggleButton tBtn = (ToggleButton) LayoutInflater.from(mContext).inflate(R.layout.expand_tab_togglebutton, this, false);
        setToggleButtonDrawable(tBtn);
        tBtn.setText(title);
        tBtn.setTextOff(title);
        tBtn.setTextOn(title);
        mTabPosition += 1;
        tBtn.setTag(mTabPosition);
        tBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton toggleButton = (ToggleButton) v;
                if (mTbSelected != null && mTbSelected != toggleButton) {
                    mTbSelected.setChecked(false);
                }
                mTbSelected = toggleButton;
                mSelectPosition = (int) mTbSelected.getTag();
                expandPopView();
            }
        });
        addView(tBtn);
        RelativeLayout popContainerView = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (mDisplayHeight * 0.6));
        popContainerView.addView(tabItemView, rl);
        popContainerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopView();
            }
        });
        popContainerView.getChildAt(0);
        popContainerView.setBackgroundColor(mPopViewBackgroundColor);
        mViews.add(popContainerView);
    }

    public void setItemData(int tabPosition, List<KeyValue> oneList) {
        if (TYPE_ONE == mTypeList.get(tabPosition)) {
            mOneListMap.get(tabPosition).setData(oneList);
        }
    }

    public void setItemData(int tabPosition, List<KeyValue> parentList, List<KeyValue> childList, List<List<KeyValue>> parentChildren) {
        if (TYPE_TWO == mTypeList.get(tabPosition)) {
            mTwoListMap.get(tabPosition).setData(parentList, childList, parentChildren);
        }
    }

    public void refreshItemChildrenData(int tabPosition, List<KeyValue> childList) {
        if (TYPE_TWO == mTypeList.get(tabPosition)) {
            mTwoListMap.get(tabPosition).setData(null, childList, null);
        }
    }

    private void expandPopView() {
        if (mPopupWindow == null) {
            mPopupWindow = new FixedPopupWindow(mViews.get(mSelectPosition), WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            mPopupWindow.setFocusable(false);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setOnDismissListener(this);
        }
        if (mTbSelected.isChecked()) {
            if (!mPopupWindow.isShowing()) {
                showPopView();
            } else {
                mPopupWindow.dismiss();
            }
        } else {
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
        }
    }

    private void showPopView() {
        if (mPopupWindow.getContentView() != mViews.get(mSelectPosition)) {
            mPopupWindow.setContentView(mViews.get(mSelectPosition));
        }
        mPopupWindow.showAsDropDown(this);
    }

    private boolean hidePopView() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            if (mTbSelected != null) {
                mTbSelected.setChecked(false);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDismiss() {
        if (TYPE_TWO == mTypeList.get(mSelectPosition)) {
            mTwoListMap.get(mSelectPosition).refreshSelected();
        }
    }

    /**
     * set togglebutton drawables
     */
    public void setToggleButtonDrawable(ToggleButton tbtn) {
        if (mTbtnBackground != -1) {
            tbtn.setBackgroundResource(mTbtnBackground);
        } else {
            tbtn.setBackground(null);
        }
        if (mTbtnTextSize != -1) {
            tbtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTbtnTextSize);
        }

        tbtn.setBackgroundColor(mTbtnBackgroundColor);
        tbtn.setTextColor(mTbtnTextColor);
    }

    @Override
    public void unexpandPopView(String title) {
        hidePopView();
        ToggleButton tbtn = (ToggleButton) getChildAt(mSelectPosition);
        tbtn.setTextOff(title);
        tbtn.setText(title);
        tbtn.setTextOn(title);
    }

    public void setAnimationStyle(int style) {
        mPopupWindow.setAnimationStyle(style);
    }
}
