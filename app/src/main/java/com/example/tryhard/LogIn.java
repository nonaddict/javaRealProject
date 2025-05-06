package com.example.tryhard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LogIn extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);
    }

    // Handle user login
    public void check(View view) {
        // Get input fields from UI
        EditText usernameEditText = findViewById(R.id.editTextText2);
        EditText passwordEditText = findViewById(R.id.editTextTextPassword2);
        ProgressBar progressBar = findViewById(R.id.progressBar2);

        // Show the progress bar
        progressBar.setVisibility(View.VISIBLE);

        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


        if (username.isEmpty() || password.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LogIn.this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create JSON body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LogIn.this, "Error occurred", Toast.LENGTH_SHORT).show();
            return;
        }

        // Volley setup
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://auth-api-dsp0.onrender.com/login";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            String message = response.getString("message");
                            String state = response.getString("success");
                            if (state.equals("true")) {
                                Toast.makeText(LogIn.this, "Hello " + username, Toast.LENGTH_SHORT).show();

                                SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("username");
                                editor.putString("username", username);
                                editor.apply();

                                Intent intent = new Intent(LogIn.this, GameView.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LogIn.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LogIn.this, "Error", Toast.LENGTH_LONG).show();
                    }
                }
        );

        queue.add(request);
    }

    public void signUp(View view) {
        Intent intent = new Intent(LogIn.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
