package io.wherevere.know.presenter;

import io.wherevere.know.fragment.BaseFragment;
import io.wherevere.know.model.BaseModel;

/**
 * @author wherevere
 * @version 1.0
 * @time 2018/5/26 13:25
 */
public class BasePresenter<M extends BaseModel, V extends BaseFragment> {

    public M model;
    public V view;

    public void attachView(M m, V v) {
        this.model = m;
        this.view = v;
    }

    public void detachView() {
        model = null;
        view = null;
    }
}
