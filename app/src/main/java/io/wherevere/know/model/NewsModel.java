package io.wherevere.know.model;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import io.wherevere.know.entity.Constant;
import io.wherevere.know.network.WanAndroid;
import io.wherevere.know.presenter.NewsPresenter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * @author wherevere
 * @version 1.0
 * @time 2018/5/26 11:58
 */
public class NewsModel extends BaseModel {

    public void getArticle(final boolean is, int page, final NewsPresenter newsPresenter) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + Constant.ARTICLE + page + Constant.END_TAG)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(Constant.LOG_TAG, "my call error");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                final String body = response.body().string();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(body)) {
                            newsPresenter.setArticle(is, null);
                        } else {
                            newsPresenter.setArticle(is, body);
                        }
                    }
                });
            }
        });
        //        Retrofit retrofit = new Retrofit
        //                .Builder()
        //                .baseUrl("www.wanandroid.com")
        //                .build();
        //        WanAndroid wanAndroid = retrofit.create(WanAndroid.class);
        //        retrofit2.Call<ResponseBody> bodyCall = wanAndroid.listResponse(page);
        //        bodyCall.enqueue(new retrofit2.Callback<ResponseBody>() {
        //            @Override
        //            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
        //                ResponseBody body = response.body();
        //            }
        //
        //            @Override
        //            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
        //
        //            }
        //        });
    }
}
