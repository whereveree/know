package io.wherevere.know.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author wherevere
 * @version 1.0
 * @time 2018/6/3 2:40
 */
public interface WanAndroid {
    @GET("article/list/{page}/json")
    Call<ResponseBody> listResponse(@Path("page") int page);
}
