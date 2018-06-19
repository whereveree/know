package io.wherevere.know.fragment;

import android.support.v4.app.Fragment;

import io.wherevere.know.model.BaseModel;
import io.wherevere.know.presenter.BasePresenter;

/**
 * @author wherevere
 * @version 1.0
 * @time 2018/5/26 13:25
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment {

    public P mPresenter;

    public abstract void initData();
    public abstract void initView();
    public abstract void initListener();
    public abstract void isLoading(boolean isLoading);
    public abstract void attach();
    public abstract P getPresenter();
}
