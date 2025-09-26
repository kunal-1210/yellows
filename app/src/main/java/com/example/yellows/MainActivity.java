package com.example.yellows;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.yellows.NewsAdapter;
import com.example.yellows.NewsApiService;
import com.example.yellows.RetrofitClient;
import com.example.yellows.databinding.ActivityMainBinding;
import com.example.yellows.Article;
import com.example.yellows.NewsResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NewsAdapter adapter;
    private List<Article> articleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // RecyclerView setup
        adapter = new NewsAdapter(this, articleList);
        binding.rvNews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNews.setAdapter(adapter);

        // Spinners
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item,
            new String[]{"us", "gb", "ca", "au"});
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCountry.setAdapter(countryAdapter);
        binding.spinnerCountry.setSelection(countryAdapter.getPosition("us"));

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item,
            new String[]{"general", "business", "entertainment", "health", "science", "sports", "technology"});
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(categoryAdapter);
        binding.spinnerCategory.setSelection(categoryAdapter.getPosition("general"));

        // Initial fetch
        fetchNews("us", "general", "");

        // Apply filter button
        binding.btnApplyFilter.setOnClickListener(v -> applyFilters());

        // Search submit (enter key)
        binding.searchView.setIconifiedByDefault(false); // Always expanded
        binding.searchView.setQueryHint("Search news...");
        binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilters();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Optional: for live search while typing
                // applyFilters();
                return false;
            }
        });

        // Logout click
        binding.btnLogout.setOnClickListener(v -> {
            // Clear saved login info
            getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

            // Go back to LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish(); // close MainActivity so user cannot press back
        });
    }

    private void applyFilters() {
        String country = binding.spinnerCountry.getSelectedItem().toString();
        String category = binding.spinnerCategory.getSelectedItem().toString();
        String query = binding.searchView.getQuery().toString();

        if (country.equals("None")) country = null;
        if (category.equals("None")) category = null;
        if (query.isEmpty()) query = null;

        fetchNews(country, category, query);
    }

    private void fetchNews(String country, String category, String query) {
        NewsApiService api = RetrofitClient.getInstance().create(NewsApiService.class);

        Log.d("MainActivity", "Fetching news: country=" + country + ", category=" + category + ", query=" + query);

        // Always use top-headlines endpoint
        Call<NewsResponse> call = api.getTopHeadlines(country, category, query);

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                articleList.clear();
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> articles = response.body().getArticles();
                    if (articles != null && !articles.isEmpty()) {
                        articleList.addAll(articles);
                    } else {
                        Toast.makeText(MainActivity.this, "No news found for this filter", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("MainActivity", "Response error: " + response.message() + ", body: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, "Failed to fetch news", Toast.LENGTH_SHORT).show();
                }

                // Update RecyclerView safely
                binding.rvNews.post(() -> adapter.notifyDataSetChanged());
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.e("MainActivity", "Network failure: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
