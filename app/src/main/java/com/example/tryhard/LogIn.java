package com.example.tryhard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LogIn extends AppCompatActivity {
    ProgressBar progressBar;
    EditText usernameEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);


        usernameEditText = findViewById(R.id.editTextText2);
        passwordEditText = findViewById(R.id.editTextTextPassword2);
    }


    public void check(View view) {


        progressBar = findViewById(R.id.progressBar2);


        progressBar.setVisibility(View.VISIBLE);

        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


        if (username.isEmpty() || password.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LogIn.this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }


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


        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://auth-api-production-f8df.up.railway.app/login";

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

    public void sendNewCode(View view) {
        if (!usernameEditText.getText().toString().isEmpty()) {

            progressBar=findViewById(R.id.progressBar2);
            progressBar.setVisibility(View.VISIBLE);

            RequestQueue queue = Volley.newRequestQueue(this);
            String username = usernameEditText.getText().toString().trim();
            String url = "https://auth-api-production-f8df.up.railway.app/user/" + username;

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONObject user = response.getJSONObject("user");
                                String recipientEmail = user.getString("email");
                                String password = user.getString("password");

                                if(recipientEmail.isEmpty()){
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(LogIn.this, "you did'nt sign up with an email", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String senderEmail = "jossefkisch@test-zxk54v883kzljy6v.mlsender.net";
                                String senderName = "Retro ping pong";
                                String subject = "Password Recovery";
                                String textContent = "Your password is: " + password;
                                String htmlContent = "<b>Your password is: " + password + "</b>";

                                JSONObject emailData = new JSONObject();
                                emailData.put("from", new JSONObject().put("email", senderEmail).put("name", senderName));
                                emailData.put("to", new JSONArray().put(new JSONObject().put("email", recipientEmail).put("name",username)));
                                emailData.put("subject", subject);
                                emailData.put("text", textContent);
                                emailData.put("html", htmlContent);

                                String apiKey = "mlsn.f7ea2d4f3405a7cc6f7b8bf6e1114b6cdd438941622a34e17b975e33c487f550";
                                String emailApiUrl = "https://api.mailersend.com/v1/email";

                                JsonObjectRequest emailRequest = new JsonObjectRequest(
                                        Request.Method.POST,
                                        emailApiUrl,
                                        emailData,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(LogIn.this, "Password Recovery was sent to your email", Toast.LENGTH_SHORT).show();
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                progressBar.setVisibility(View.GONE);
                                                try {
                                                    boolean state=response.getBoolean("success");
                                                    if (state){
                                                        Toast.makeText(LogIn.this, "Password Recovery was sent to your email", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(LogIn.this, "Error occurred", Toast.LENGTH_SHORT).show();
                                                    }
                                                }catch (JSONException e){
                                                    Toast.makeText(LogIn.this, "Error occurred", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                ) {
                                    @Override
                                    public Map<String, String> getHeaders() {
                                        Map<String, String> headers = new HashMap<>();
                                        headers.put("Authorization", "Bearer " + apiKey);
                                        headers.put("Content-Type", "application/json");
                                        return headers;
                                    }
                                };

                                queue.add(emailRequest);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LogIn.this, "Error occurred while retrieving user info", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            String message = "An error occurred";

                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                try {
                                    String jsonStr = new String(error.networkResponse.data, "UTF-8");
                                    JSONObject json = new JSONObject(jsonStr);

                                    if (json.has("message")) {
                                        message = json.getString("message");
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    message = "Error";
                                }
                            }
                            Toast.makeText(LogIn.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            queue.add(request);
        } else {
            Toast.makeText(this, "Please enter a username first", Toast.LENGTH_SHORT).show();
        }
    }


}