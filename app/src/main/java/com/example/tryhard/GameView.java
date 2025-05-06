package com.example.tryhard;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
    Game game; // Game instance

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_view);

        // Retrieve username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");  // Default to "" if not found

        // Get points from intent
        int points = getIntent().getIntExtra("points", 0);
        //if there is username in sharedPreferences
        if (!username.isEmpty()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://auth-api-dsp0.onrender.com/user/" + username;

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
                                        // Only update if it's a new record
                                        String updateUrl = "https://auth-api-dsp0.onrender.com/updateScore";

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

        // Replace the current activity's layout with the game view
        setContentView(game); // The game view will take over the screen
    }

    // Fetch leaderboard after updating score
    public void fetchLeaderboard() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://auth-api-dsp0.onrender.com/getScores";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray usersArray = response.getJSONArray("users");

                                // Sort users based on their scores
                                ArrayList<User> users= new ArrayList<User>();
                                for (int i = 0; i < usersArray.length(); i++) {
                                    JSONObject userObj = usersArray.getJSONObject(i);
                                    String username = userObj.getString("username");
                                    int score = userObj.getInt("score");
                                    users.add(new User(username,score));
                                }
                                // Bubble Sort (descending order by score)
                                for (int i = 0; i < users.size() - 1; i++) {
                                    for (int j = 0; j < users.size() - i - 1; j++) {
                                        if (users.get(j).getScore() < users.get(j + 1).getScore()) {
                                            // Swap
                                            User temp = users.get(j);
                                            users.set(j, users.get(j + 1));
                                            users.set(j + 1, temp);
                                        }
                                    }
                                }

                                // Build the leaderboard string with ranks
                                StringBuilder leaderboard = new StringBuilder();
                                for (int i = 0; i < users.size(); i++) {
                                    User user = users.get(i);
                                    leaderboard.append((i + 1)).append(". ")
                                            .append(user.getUsername())
                                            .append(": ")
                                            .append(user.getScore())
                                            .append("\n");
                                }

                                // Display the leaderboard
                                TextView leaderboardTextView = findViewById(R.id.textView8);
                                leaderboardTextView.setText("Leader Board\n\n"+leaderboard.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GameView.this, "Error fetching leaderboard", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        queue.add(request);
    }
}
