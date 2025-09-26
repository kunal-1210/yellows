package com.example.yellows;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.yellows.databinding.ActivitySignupBinding;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private SupabaseApi api;
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        api = ApiClient.getClient(BuildConfig.SUPABASE_URL).create(SupabaseApi.class);

        binding.btnSignUp.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("redirect_to", "myapp://login");

        api.signUp(BuildConfig.SUPABASE_API_KEY,body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SignUpActivity.this, "SignUp successful!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "SignUp Response: " + response.body().toString());
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(SignUpActivity.this, "SignUp failed", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "SignUp failed: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Exception reading errorBody", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "SignUp network error", t);
            }
        });
    }
}
