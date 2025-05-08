package com.example.tryhard;


import android.content.Intent; // For starting new activities
import android.content.SharedPreferences;
import android.os.Bundle; // For passing data between activities
import android.view.View; // For handling UI components
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast; // For short popup messages


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity; // Base class for all activities
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Volley libraries for networking
import com.android.volley.RequestQueue; // Queue for network requests
import com.android.volley.Response; // Listener for successful responses
import com.android.volley.VolleyError; // Listener for failed responses
import com.android.volley.toolbox.JsonObjectRequest; // Request for JSON object
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;

// JSON handling
import org.json.JSONException; // Exception for JSON parsing
import org.json.JSONObject; // JSON object class

// Main activity class, launched when app starts
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the parent class (AppCompatActivity)
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // Set layout to activity_main.xml

        // Apply window padding to avoid overlapping with the status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    // Switch to login screen when login button is clicked
    public void logIn(View view) {
        // Start the LogIn activity
        Intent intent = new Intent(MainActivity.this, LogIn.class);
        startActivity(intent);
        finish(); // Close current activity (MainActivity)
    }

    // Handle user sign-up when new user button is clicked
    public void newUser(View view) {
        // Get input fields for username and password
        EditText emailEditText=findViewById(R.id.editTextTextEmailAddress);
        EditText usernameEditText = findViewById(R.id.editTextText);
        EditText passwordEditText = findViewById(R.id.editTextTextPassword);
        ProgressBar progressBar=findViewById(R.id.progressBar);

        String email=emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        progressBar.setVisibility( View.VISIBLE);

        //ensure both fields are not empty
        if (username.isEmpty() || password.isEmpty()) {
            progressBar.setVisibility( View.GONE);
            // Show a toast if any of the fields are empty
            Toast.makeText(MainActivity.this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
            return; // Stop
        }

        // Create a JSON object to send in the request body for sign-up
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email",email);
            requestBody.put("username", username);
            requestBody.put("password", password);
            requestBody.put("score", 0);
        } catch (JSONException e) {
            e.printStackTrace(); // Print error for debugging
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            return; // Stop further execution
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://auth-api-dsp0.onrender.com/signup"; // API URL for user sign-up

        // Create a POST request with the JSON body (sign-up request)
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, // HTTP POST method
                url, // API URL
                requestBody, // Request body (username and password)
                new Response.Listener<JSONObject>() { // Response listener for successful response
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressBar.setVisibility( View.GONE);
                            // Extract "success" field from the response JSON
                            String state = response.getString("success");
                            if (state.equals("true")) { // If sign-up is successful
                                // Show a welcome toast
                                Toast.makeText(MainActivity.this, "Hello " + username, Toast.LENGTH_SHORT).show();
                                // Get SharedPreferences editor to save data
                                SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();


                                editor.remove("username");

                                // Save the new username
                                editor.putString("username", username);
                                editor.apply();
                                // Move to the GameView activity
                                Intent intent = new Intent(MainActivity.this, GameView.class);
                                startActivity(intent);
                                finish(); // Close current activity (MainActivity)
                            } else {
                                // If username is taken
                                Toast.makeText(MainActivity.this, "Username is taken", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            progressBar.setVisibility( View.GONE);
                            throw new RuntimeException(e); // If there's an error with the JSON
                        }
                    }
                },
                new Response.ErrorListener() { // Response listener for error response
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility( View.GONE);
                        Toast.makeText(MainActivity.this, "Error Occured", Toast.LENGTH_LONG).show();
                    }
                }
        );

        queue.add(request); // Add the request to the Volley queue
    }
}
