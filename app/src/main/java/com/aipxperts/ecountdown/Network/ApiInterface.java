package com.aipxperts.ecountdown.Network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by aipxperts-ubuntu-01 on 13/9/17.
 */

public interface ApiInterface {
/*    @GET("search/images/?count={count}&offset={offset}&q={q}")
    Call<ResultModel> peticularGroup(
                                     @Query("count") String count,
                                     @Query("offset") String offset,
                                     @Query("q") String q);*/
@GET("search/images")
Call<ResponseBody> peticularGroup(
        @Query("count") String count,
        @Query("offset") String offset,
        @Query("q") String q);

}
