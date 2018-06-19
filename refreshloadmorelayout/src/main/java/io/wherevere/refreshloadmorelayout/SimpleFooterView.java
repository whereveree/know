package io.wherevere.refreshloadmorelayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SimpleFooterView extends BaseFooterView {

    private TextView mText;
    private ProgressBar progressBar;
    private View view;
    private CustomRefreshView customRefreshView;

    public SimpleFooterView(Context context) {
        this(context, null);
    }

    public SimpleFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view = LayoutInflater.from(getContext()).inflate(R.layout.footer_view, this);
        progressBar = (ProgressBar) view.findViewById(R.id.footer_view_progressbar);
        mText = (TextView) view.findViewById(R.id.footer_view_tv);
    }


    public void setCustomRefreshView(CustomRefreshView customRefreshView) {
        this.customRefreshView = customRefreshView;
    }

    public void nolyShowText() {
        progressBar.setVisibility(GONE);
        mText.setVisibility(VISIBLE);
    }

    @Override
    public void onLoadingMore() {
        progressBar.setVisibility(VISIBLE);
        mText.setVisibility(VISIBLE);
        mText.setText("快速加载中。。。");
        view.setOnClickListener(null);
    }

    @Override
    public void onNoMore() {
        nolyShowText();
        mText.setText("-- 没有更多了 --");
        view.setOnClickListener(null);
    }

    /**
     * error后pager自行减1
     */
    @Override
    public void onError() {
        nolyShowText();
        mText.setText("--  出错了, 点我重试  --");
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //变更footerView ui，重新执行加载
                onLoadingMore();
                customRefreshView.mListener.onLoadMore();
            }
        });
    }
}
