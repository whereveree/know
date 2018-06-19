package io.wherevere.refreshloadmorelayout;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CustomRefreshView extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {

    public FrameLayout blankView;
    public View mEmptyView;
    public TextView mEmptyText;
    public View mErrorView;

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager mLayoutManager;
    public SwipeRefreshLayout mRefreshLayout;
    public BaseFooterView mFootView;
    public OnLoadListener mListener;

    public DataObserver mDataObserver;
    public WrapperAdapter mWrapperAdapter;

    public boolean isEmptyViewShowing;
    public boolean isLoadingMore;
    public boolean isLoadMoreEnable;
    public boolean isRefreshEnable;

    public int lastVisiblePosition = 0;
    public Context context;


    public CustomRefreshView(Context context) {
        this(context, null);
    }

    public CustomRefreshView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRefreshView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        setupSwipeRecyclerView();
    }

    private void setupSwipeRecyclerView() {

        isEmptyViewShowing = false;
        isLoadingMore = false;
        isRefreshEnable = true;
        isLoadMoreEnable = true;

        mFootView = new SimpleFooterView(getContext());
        //将this传入，加载失败自动回调loadmore
        mFootView.setCustomRefreshView(this);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_refresh_layout, this);

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipeRefreshLayout);
        blankView = (FrameLayout) view.findViewById(R.id.blank_view);

        LayoutParams params = new LayoutParams(-1, -1);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        blankView.setLayoutParams(params);

        //默认下拉刷新ui颜色
        mRefreshLayout.setColorSchemeColors(Color.parseColor("#000000"));
        recyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView);
        //默认线性布局
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mLayoutManager = recyclerView.getLayoutManager();

        mRefreshLayout.setOnRefreshListener(this);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 禁止加载更多、正在下拉刷新、正在加载更多 return
                // isLoadingMore为框架控制，当需要禁止加载时操作isLoadMoreEnable(public)
                if (!isLoadMoreEnable || isRefreshing() || isLoadingMore) {
                    return;
                }

                mLayoutManager = recyclerView.getLayoutManager();
                if (mLayoutManager instanceof LinearLayoutManager) {
                    lastVisiblePosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
                }
                int childCount = mWrapperAdapter == null ? 0 : mWrapperAdapter.getItemCount();
                if (childCount > 9 && lastVisiblePosition == childCount - 1) {
                    if (mListener != null) {
                        isLoadingMore = true;
                        mListener.onLoadMore();
                    }
                }
            }
        });
    }


    //是否开启下拉刷新
    public void setRefreshEnable(boolean refreshEnable) {
        isRefreshEnable = refreshEnable;
        mRefreshLayout.setEnabled(isRefreshEnable);
    }

    //是否开启加载更多
    public void setLoadMoreEnable(boolean loadMoreEnable) {
        if (!loadMoreEnable) {
            stopLoadingMore();
        }
        isLoadMoreEnable = loadMoreEnable;
    }

    public boolean getRefreshEnable() {
        return isRefreshEnable;
    }

    public boolean getLoadMoreEnable() {
        return isLoadMoreEnable;
    }

    //是否下拉刷新中
    public boolean isRefreshing() {
        return mRefreshLayout.isRefreshing();
    }

    //是否加载更多中
    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    //获取SwipeRefreshLayout，更改属性
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mRefreshLayout;
    }

    //获取recyclerView，更改属性
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    //设置listener
    public void setOnLoadListener(OnLoadListener listener) {
        mListener = listener;
    }

    //设置footerView
    public void setFooterView(BaseFooterView footerView) {
        if (footerView != null) {
            this.mFootView = footerView;
        }
    }

    //空视图是否展示中
    public boolean isEmptyViewShowing() {
        return isEmptyViewShowing;
    }

    //设置默认空视图，无数据的情况下
    public void setEmptyView(String s) {
        if (blankView.getChildCount() > 0) {
            blankView.removeAllViews();
        }
        if (mEmptyView == null) {
            mEmptyView = LayoutInflater.from(context).inflate(R.layout.empty_view, null);
            mEmptyText = (TextView) mEmptyView.findViewById(R.id.module_base_empty_text);
        }
        blankView.addView(mEmptyView);

        mEmptyText.setText(s);
        if (mDataObserver != null) {
            mDataObserver.onChanged();
        }
    }

    //设置默认错误视图，当网络出现问题
    public void setErrorView() {
        if (blankView.getChildCount() > 0) {
            blankView.removeAllViews();
        }
        if (mErrorView == null) {
            mErrorView = LayoutInflater.from(context).inflate(R.layout.error_view, null);
            mErrorView.findViewById(R.id.module_base_id_fail_retry).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //重试
                    setRefreshing(true);
                }
            });
        }
        blankView.addView(mErrorView);

        if (mDataObserver != null) {
            mDataObserver.onChanged();
        }
    }

    public void setCreateView(View createView) {
        if (blankView.getChildCount() > 0) {
            blankView.removeAllViews();
        }
        blankView.addView(createView);

        if (mDataObserver != null) {
            mDataObserver.onChanged();
        }
    }

    //setAdapter，footerView的样式变化交给包装类展示
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter != null) {
            if (mDataObserver == null) {
                mDataObserver = new DataObserver();
            }
            mWrapperAdapter = new WrapperAdapter(adapter);
            recyclerView.setAdapter(mWrapperAdapter);
            adapter.registerAdapterDataObserver(mDataObserver);
            mDataObserver.onChanged();
        }
    }

    //下拉刷新或上拉加载完成
    public void complete() {
        mRefreshLayout.setRefreshing(false);
        stopLoadingMore();
    }

    //停止加载更多
    public void stopLoadingMore() {
        isLoadingMore = false;
        if (mWrapperAdapter != null) {
            mWrapperAdapter.notifyItemRemoved(mWrapperAdapter.getItemCount());
        }
    }

    //首次加载时
    public void setRefreshing(boolean refreshing) {
        mRefreshLayout.setRefreshing(refreshing);
        if (refreshing && !isLoadingMore && mListener != null) {
            mListener.onRefresh();
        }
    }

    //SwipeRefreshLayout.OnRefreshListener的实现
    @Override
    public void onRefresh() {
        if (mListener != null) {
            isLoadingMore = false;
            //重置footerView的样式（防止滑动最后一条时用户又下拉刷新）
            if (mFootView != null) {
                mFootView.onLoadingMore();
            }
            mListener.onRefresh();
        }
    }

    //恢复footerView的正常状态
    public void onLoadingMore() {
        if (mFootView != null) {
            mFootView.onLoadingMore();
        }
    }

    //footerView ui-没有更多数据
    public void onNoMore() {
        if (mFootView != null) {
            isLoadingMore = true;
            mFootView.onNoMore();
        }
    }

    //footerView ui-加载出错   备注：error之后，pager自行减1
    public void onError() {
        if (mFootView != null) {
            mFootView.onError();
        }
    }

    //WrapperAdapter ：包装类,扩展实体adapter，控制footerView展示
    private class WrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public static final int TYPE_FOOTER = 0x100;

        RecyclerView.Adapter<RecyclerView.ViewHolder> mInnerAdapter;

        public WrapperAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
            this.mInnerAdapter = adapter;
        }

        public boolean isLoadMoreItem(int position) {
            return isLoadMoreEnable && position == getItemCount() - 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (TYPE_FOOTER == viewType) {
                //当出现nomore或onerror，主动改变footerView样式
                //                mFootView.onLoadingMore();
                return new FooterViewHolder(mFootView);
            }
            return mInnerAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (isLoadMoreItem(position)) {
                return;
            }
            mInnerAdapter.onBindViewHolder(holder, position);
        }


        @Override
        public int getItemViewType(int position) {
            if (isLoadMoreItem(position)) {
                return TYPE_FOOTER;
            } else {
                return mInnerAdapter.getItemViewType(position);
            }
        }

        @Override
        public int getItemCount() {
            int count = mInnerAdapter == null ? 0 : mInnerAdapter.getItemCount();

            if (count == 0) {
                return 0;
            }
            return isLoadMoreEnable ? count + 1 : count;
        }

        @Override
        public long getItemId(int position) {
            return mInnerAdapter.getItemId(position);
        }

        @Override
        public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            Log.i("tag", "registerAdapterDataObserver--------------");
            mInnerAdapter.registerAdapterDataObserver(observer);
        }

        @Override
        public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            Log.i("tag", "unregisterAdapterDataObserver--------------");
            if (mInnerAdapter.hasObservers()) {
                mInnerAdapter.unregisterAdapterDataObserver(observer);
            }
        }

    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    //监控数据变化，用来控制无数据界面展示
    class DataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null) {
                if (adapter.getItemCount() == 0) {
                    isEmptyViewShowing = true;
                    recyclerView.setVisibility(View.GONE);
                    blankView.setVisibility(VISIBLE);
                } else {
                    isEmptyViewShowing = false;
                    blankView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mWrapperAdapter != null) {
            mWrapperAdapter.unregisterAdapterDataObserver(mDataObserver);
        }
        super.onDetachedFromWindow();
    }

    public interface OnLoadListener {
        void onRefresh();

        void onLoadMore();
    }
}
