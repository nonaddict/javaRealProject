package com.example.tryhard;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

import java.util.ArrayList;


public class GameView extends AppCompatActivity {
    Game game;
    ImageButton button;
    ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_view);
        button = findViewById(R.id.imageButton);
        progressBar=findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.VISIBLE);
        button.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");


        int points = getIntent().getIntExtra("points", 0);

        if (!username.isEmpty()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://auth-api-production-f8df.up.railway.app/user/" + username;

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("success")) {
                                    JSONObject user = response.getJSONObject("user");
                                    int currentScore = user.getInt("score");

                                    if (points > currentScore) {
                                        String updateUrl = "https://auth-api-production-f8df.up.railway.app/updateScore";

                                        JSONObject updateBody = new JSONObject();
                                        updateBody.put("username", username);
                                        updateBody.put("score", points);

                                        JsonObjectRequest updateRequest = new JsonObjectRequest(
                                                Request.Method.PUT,
                                                updateUrl,
                                                updateBody,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        fetchLeaderboard();
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Toast.makeText(GameView.this, "Failed to display the scores", Toast.LENGTH_SHORT).show();
                                                        progressBar.setVisibility(View.GONE);
                                                        button.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                        );

                                        queue.add(updateRequest);
                                    }else{
                                        fetchLeaderboard();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(GameView.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            button.setVisibility(View.VISIBLE);
                        }
                    }
            );

            queue.add(request);
        }
    }

    public void play(View view) {
        if (game == null) {
            game = new Game(this);
        }


        setContentView(game);
    }


    public void fetchLeaderboard() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://auth-api-production-f8df.up.railway.app/getScores";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressBar.setVisibility(View.GONE);
                            button.setVisibility(View.VISIBLE);
                            if (response.getBoolean("success")) {
                                JSONArray usersArray = response.getJSONArray("users");


                                ArrayList<User> users= new ArrayList<User>();
                                for (int i = 0; i < usersArray.length(); i++) {
                                    JSONObject userObj = usersArray.getJSONObject(i);
                                    String username = userObj.getString("username");
                                    int score = userObj.getInt("score");
                                    users.add(new User(username,score));
                                }

                                for (int i = 0; i < users.size() - 1; i++) {
                                    for (int j = 0; j < users.size() - i - 1; j++) {
                                        if (users.get(j).getScore() < users.get(j + 1).getScore()) {
                                            User temp = users.get(j);
                                            users.set(j, users.get(j + 1));
                                            users.set(j + 1, temp);
                                        }
                                    }
                                }

                                StringBuilder leaderboard = new StringBuilder();
                                for (int i = 0; i < users.size(); i++) {
                                    User user = users.get(i);
                                    leaderboard.append((i + 1)).append(". ")
                                            .append(user.getUsername())
                                            .append(": ")
                                            .append(user.getScore())
                                            .append("\n");
                                }


                                TextView leaderboardTextView = findViewById(R.id.textView8);
                                leaderboardTextView.setText("Leader Board\n\n"+leaderboard.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            button.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GameView.this, "Error fetching leaderboard", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        button.setVisibility(View.VISIBLE);
                    }
                }
        );

        queue.add(request);
    }
}
