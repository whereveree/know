package io.wherevere.know.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.wherevere.know.R;
import io.wherevere.know.adapter.RecyclerAdapter;
import io.wherevere.know.entity.Article;
import io.wherevere.know.entity.ArticleData;
import io.wherevere.know.model.NewsModel;
import io.wherevere.know.presenter.NewsPresenter;
import io.wherevere.know.refresh.PullRefreshLayout;
import io.wherevere.refreshloadmorelayout.CustomRefreshView;

/**
 * @author wherevere
 * @version 1.0
 * @time 2018/5/26 13:19
 */
public class NewsFragment extends BaseFragment<NewsPresenter> {

    public int page;
    public List<Article> mArticleList;
    public View mRootView;
    public RecyclerView mRecyclerView;
    public RecyclerAdapter mRecyclerAdapter;
    public CustomRefreshView mCustomRefreshView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPresenter = getPresenter();
        attach();
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_main, container, false);
            initData();
            initView();
            initListener();
        }
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void attach() {
        if (mPresenter != null) {
            mPresenter.attachView(new NewsModel(), this);
        }
    }

    @Override
    public NewsPresenter getPresenter() {
        return new NewsPresenter();
    }

    @Override
    public void initData() {
        mArticleList = new ArrayList<>();
    }

    @Override
    public void initView() {
        mCustomRefreshView = mRootView.findViewById(R.id.refreshview);
        mRecyclerAdapter = new RecyclerAdapter(mCustomRefreshView.getContext(), mArticleList);
        mCustomRefreshView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void initListener() {
        mCustomRefreshView.setOnLoadListener(new CustomRefreshView.OnLoadListener() {
            @Override
            public void onRefresh() {
                page = 0;
                mPresenter.getArticle(true, page);
            }

            @Override
            public void onLoadMore() {
                page += 1;
                mPresenter.getArticle(false, page);
            }
        });
        mCustomRefreshView.setRefreshing(true);
    }

    @Override
    public void isLoading(boolean isLoading) {
        if (isLoading) {
            mCustomRefreshView.setRefreshEnable(true);
        } else {
            mCustomRefreshView.complete();
        }
    }

    public void refresh(List<Article> articleList) {
        mRecyclerAdapter.refreshList(articleList);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    public void loadmore(List<Article> articleList) {
        mRecyclerAdapter.loadmoreList(articleList);
        mRecyclerAdapter.notifyDataSetChanged();
    }
}
