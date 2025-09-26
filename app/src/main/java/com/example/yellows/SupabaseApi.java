package com.example.yellows;

import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SupabaseApi {

    @POST("auth/v1/signup")
    Call<Map<String, Object>> signUp(
        @Header("apikey") String apiKey,
        @Body Map<String, String> body
    );

    @POST("auth/v1/token?grant_type=password")
    Call<Map<String, Object>> signIn(
        @Header("apikey") String apiKey,
        @Body Map<String, String> body
    );


}
