package io.wherevere.know.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.wherevere.expandpopview.callback.OnTwoListCallback;
import io.wherevere.expandpopview.entity.KeyValue;
import io.wherevere.expandpopview.view.ExpandPopView;
import io.wherevere.know.R;
import io.wherevere.know.adapter.RecyclerAdapter;
import io.wherevere.know.entity.Article;
import io.wherevere.know.model.ClassifyModel;
import io.wherevere.know.presenter.ClassifyPresenter;
import io.wherevere.refreshloadmorelayout.CustomRefreshView;

/**
 * @author wherevere
 * @version 1.0
 * @time 2018/6/1 17:16
 */
public class ClassifyFragment extends BaseFragment<ClassifyPresenter> {

    public int page;
    public int parentListPosition;
    public boolean isLoaded;
    public KeyValue mKeyValue;
    public View mRootView;
    public ExpandPopView mExpandPopView;
    public RecyclerAdapter mRecyclerAdapter;
    public CustomRefreshView mCustomRefreshView;
    public List<Article> mArticleList;
    public List<KeyValue> mParentList;
    public List<KeyValue> mChildList;
    public List<List<KeyValue>> mParentChildList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPresenter = getPresenter();
        attach();
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_main, container, false);
            mPresenter.getParentChildList();
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
    public void initData() {
        mKeyValue = new KeyValue();
        mArticleList = new ArrayList<>();
        mParentList = new ArrayList<>();
        mChildList = new ArrayList<>();
        mParentChildList = new ArrayList<>();
    }

    @Override
    public void initView() {
        mCustomRefreshView = mRootView.findViewById(R.id.refreshview);
        mExpandPopView = mRootView.findViewById(R.id.expandview);
        mExpandPopView.setVisibility(View.VISIBLE);
        mRecyclerAdapter = new RecyclerAdapter(mCustomRefreshView.getContext(), mArticleList);
        mCustomRefreshView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void initListener() {
        mCustomRefreshView.setOnLoadListener(new CustomRefreshView.OnLoadListener() {
            @Override
            public void onRefresh() {
                page = 0;
                mPresenter.getArticle(true, page, mKeyValue.getValue());
            }

            @Override
            public void onLoadMore() {
                page += 1;
                mPresenter.getArticle(false, page, mKeyValue.getValue());
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

    @Override
    public void attach() {
        if (mPresenter != null) {
            mPresenter.attachView(new ClassifyModel(), this);
        }
    }

    @Override
    public ClassifyPresenter getPresenter() {
        return new ClassifyPresenter();
    }

    public void refresh(List<Article> articleList) {
        mRecyclerAdapter.refreshList(articleList);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    public void loadmore(List<Article> articleList) {
        mRecyclerAdapter.loadmoreList(articleList);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    public void setExpandPopView(List<KeyValue> parentList, List<List<KeyValue>> parentChildList) {
        if (!isLoaded) {
            if (parentList == null || parentChildList == null || parentList.size() <= 0 || parentChildList.size() <= 0) {
                return;
            }
            this.mParentList = parentList;
            this.mParentChildList = parentChildList;
            mExpandPopView.addItemToExpandTab("知识点 ： " + mParentChildList.get(0).get(0).getKey(), parentList, parentChildList, new OnTwoListCallback() {
                @Override
                public void returnParentKeyValue(int pos, KeyValue keyValue) {
                    parentListPosition = pos;
                }

                @Override
                public void returnChildKeyValue(int pos, KeyValue keyValue) {
                    mKeyValue = keyValue;
                    page = 0;
                    mPresenter.getArticle(true, page, keyValue.getValue());
                }
            });
            mKeyValue = mParentChildList.get(0).get(0);
            mPresenter.getArticle(true, 0, parentChildList.get(0).get(0).getValue());
            isLoaded = false;
        }
    }
}
