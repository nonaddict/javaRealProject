package com.example.tryhard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void logIn(View view) {
        Intent intent = new Intent(MainActivity.this, LogIn.class);
        startActivity(intent);
        finish();
    }

    public void newUser(View view) {
        EditText emailEditText = findViewById(R.id.editTextTextEmailAddress);
        EditText usernameEditText = findViewById(R.id.editTextText);
        EditText passwordEditText = findViewById(R.id.editTextTextPassword);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        if (username.isEmpty() || password.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("username", username);
            requestBody.put("password", password);
            requestBody.put("score", 0);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://auth-api-production-f8df.up.railway.app/signup";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressBar.setVisibility(View.GONE);
                            String state = response.getString("success");
                            if (state.equals("true")) {
                                Toast.makeText(MainActivity.this, "Hello " + username, Toast.LENGTH_SHORT).show();
                                SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.remove("username");
                                editor.putString("username", username);
                                editor.apply();

                                Intent intent = new Intent(MainActivity.this, GameView.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "Username is taken", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            progressBar.setVisibility(View.GONE);
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Error Occured", Toast.LENGTH_LONG).show();
                    }
                }
        );

        queue.add(request);
    }
}
