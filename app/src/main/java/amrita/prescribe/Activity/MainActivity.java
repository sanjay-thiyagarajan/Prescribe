package amrita.prescribe.Activity;

// IMPORTS
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import amrita.prescribe.R;


public class MainActivity extends AppCompatActivity {

    private Button SignupButton, LoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SignupButton = (Button) findViewById(R.id.signup_button);
        LoginButton = (Button) findViewById(R.id.login_button);

        // When signup button is clicked
        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirects to signup page
                Intent signupIntent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(signupIntent);
            }
        });

        // When Login button is clicked
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                // Redirects to login page
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });


    }
}
