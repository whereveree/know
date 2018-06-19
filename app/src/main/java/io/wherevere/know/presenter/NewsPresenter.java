package io.wherevere.know.presenter;

import android.util.Log;

import io.wherevere.know.entity.ArticleData;
import io.wherevere.know.entity.Message;
import io.wherevere.know.fragment.NewsFragment;
import io.wherevere.know.model.NewsModel;
import io.wherevere.know.utils.GsonUtil;

/**
 * @author wherevere
 * @version 1.0
 * @time 2018/5/26 11:58
 */
public class NewsPresenter extends BasePresenter<NewsModel, NewsFragment> {


    public void getArticle(boolean is, int page) {
        model.getArticle(is, page, this);
    }

    public void setArticle(boolean is, String body) {
        view.isLoading(false);
        if (body != null && view != null) {
            Message message = GsonUtil.gsonToBean(body, Message.class);
            String json = GsonUtil.gsonToJson(message.getData());
            ArticleData articleData = GsonUtil.gsonToBean(json, ArticleData.class);
            if (is) {
                view.refresh(articleData.getDatas());
            } else {
                view.loadmore(articleData.getDatas());
            }
        } else {
            Log.e("MY ERROR", "BODY IS EMPTY");
        }
    }
}
