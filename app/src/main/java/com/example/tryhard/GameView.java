package com.example.tryhard;

import android.content.SharedPreferences; // For saving and retrieving data
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView; // For displaying text

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity; // Base class for activities

public class GameView extends AppCompatActivity {
    Game game; // Game instance

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the parent class onCreate()
        setContentView(R.layout.game_view); // Set the layout to game_view.xml

        // Use SharedPreferences to retrieve saved high score from previous sessions
        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE); // Access shared preferences
        int highScore = prefs.getInt("highScore", 0); // Get high score, default to 0 if not found

        // Get the TextView for displaying the high score
        TextView highScoreView = findViewById(R.id.textView8);
        // Set the text of the TextView to show the high score
        highScoreView.setText("Highest Score: " + highScore);
    }

    // This method is triggered when the play button is clicked
    public void play(View view) {
        // Check if the game object is null (game not initialized yet)
        if (game == null) {
            game = new Game(this); // Initialize the game object
        }

        // Replace the current activity's layout with the game view (custom game view)
        setContentView(game); // The game view will take over the screen
    }
}
