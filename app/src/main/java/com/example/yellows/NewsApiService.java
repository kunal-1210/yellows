package com.example.yellows;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {

    @GET("v2/top-headlines")  // your Render endpoint
    Call<NewsResponse> getTopHeadlines(
        @Query("country") String country,
        @Query("category") String category,
        @Query("q") String query
    );

}
