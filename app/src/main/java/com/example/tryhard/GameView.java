package com.example.tryhard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameView extends AppCompatActivity {
    Game game;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_view);
        // Get points from the Intent
        int points = getIntent().getIntExtra("points", 0);  // Default value is 0 if points are not passed
        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        int highScore = prefs.getInt("highScore", 0);

        TextView highScoreView = findViewById(R.id.textView8);
        highScoreView.setText("Highest Score: " + highScore);
    }
    public void play(View view) {
        if (game == null) {
            game = new Game(this);
        }
        setContentView(game);  // Replace the current layout with the Game view
    }
}
