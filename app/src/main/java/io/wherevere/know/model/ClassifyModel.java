package io.wherevere.know.model;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import io.wherevere.know.entity.Constant;
import io.wherevere.know.presenter.ClassifyPresenter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author wherevere
 * @version 1.0
 * @time 2018/6/1 17:14
 */
public class ClassifyModel extends BaseModel {

    public void getParentChildList(final ClassifyPresenter classifyPresenter) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + Constant.TREE)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MY ERROR PARENT","FAILURE");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.e("MY ERROR PARENT","SUCCESS");
                final String body = response.body().string();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (body.isEmpty()) {
                            classifyPresenter.setParentChildList(null);
                        } else {
                            classifyPresenter.setParentChildList(body);
                        }
                    }
                });
            }
        });
    }

    public void getArticle(final boolean is, int page, String value, final ClassifyPresenter classifyPresenter) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + Constant.ARTICLE + page + Constant.END_TAG + "/?cid=" + value)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String body = response.body().string();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (body.isEmpty()) {
                            classifyPresenter.setArticle(is, null);
                        } else {
                            classifyPresenter.setArticle(is, body);
                        }
                    }
                });
            }
        });
    }
}
