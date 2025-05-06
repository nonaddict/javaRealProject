package com.example.tryhard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameOver extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        // Get points from the Intent
        int points = getIntent().getIntExtra("points", 0);  // Default value is 0 if points are not passed

        // Display the points
        TextView pointsTextView = findViewById(R.id.textView7);
        pointsTextView.setText("Score: " + points);
    }

    // Restart the game when the button is clicked
    public void restartGame(View view) {

        int points = getIntent().getIntExtra("points", 0);
        Intent intent = new Intent(GameOver.this, GameView.class);
        intent.putExtra("points", points);
        startActivity(intent);
        finish();
    }

}
