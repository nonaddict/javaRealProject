package com.example.tryhard;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameOver extends AppCompatActivity {
    Game game;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        // Get points from the Intent
        int points = getIntent().getIntExtra("points", 0);  // Default value is 0 if points are not passed

        // Display the points (you can show them in a TextView)
        TextView pointsTextView = findViewById(R.id.textView7);
        pointsTextView.setText("Score: " + points);
    }

    // Restart the game when the button is clicked
    public void restartGame(View view) {
//        if (game == null) {
//            game = new Game(this);
//        }
//        setContentView(game);
        Intent intent = new Intent(GameOver.this, GameView.class);
        startActivity(intent);
        finish();  // Finish the current activity so it doesn't stay in the backstack
    }

}
