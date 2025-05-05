package com.example.tryhard;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

        // Extract text and trim
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate
        if (username.isEmpty() || password.isEmpty()) {
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
            Toast.makeText(LogIn.this, "Error occurred", Toast.LENGTH_SHORT).show();
            return;
        }

        // Volley setup
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://auth-api-dsp0.onrender.com/login"; // API endpoint

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message"); // e.g., "Login successful"
                            String state = response.getString("success"); // e.g., "true"
                            if (state.equals("true")) {
                                Toast.makeText(LogIn.this, "Hello " + username, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LogIn.this, GameView.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LogIn.this, message, Toast.LENGTH_SHORT).show(); // e.g., "Invalid credentials"
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LogIn.this, "Error", Toast.LENGTH_LONG).show();
                    }
                }
        );

        queue.add(request); // Add to request queue
    }
    public void signUp(View view) {
        Intent intent = new Intent(LogIn.this, MainActivity.class);
        startActivity(intent); // Start GameView activity
        finish(); // Close current activity
    }
}
