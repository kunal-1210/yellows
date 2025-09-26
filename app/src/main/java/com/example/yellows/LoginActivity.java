package com.example.yellows;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yellows.ApiClient;
import com.example.yellows.SupabaseApi;
import com.example.yellows.databinding.ActivityLoginBinding;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private SupabaseApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String accessToken = prefs.getString("accessToken", null);

        if (isLoggedIn && accessToken != null) {
            // User is already logged in, go to MainActivity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // No session, show login screen
            binding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
        }

        api = ApiClient.getClient(BuildConfig.SUPABASE_URL).create(SupabaseApi.class);

        binding.btnLogin.setOnClickListener(v -> loginUser());
        binding.tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Log.e(TAG, "Email or password is empty");
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        api.signIn(BuildConfig.SUPABASE_API_KEY, body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Login successful: " + response.body().toString());

                    // Extract session from response
                    Map<String, Object> session = (Map<String, Object>) response.body().get("session");
                    String accessToken = "";
                    String refreshToken = "";
                    if (session != null) {
                        accessToken = (String) session.get("access_token");
                        refreshToken = (String) session.get("refresh_token");
                    }

                    // Save login state + tokens
                    getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                        .edit()
                        .putBoolean("isLoggedIn", true)
                        .putString("accessToken", accessToken)
                        .putString("refreshToken", refreshToken)
                        .apply();

                    // Navigate to main screen
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "Login failed: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Login failed: Unknown error", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "Login error: " + t.getMessage(), t);
            }
        });
    }
}
