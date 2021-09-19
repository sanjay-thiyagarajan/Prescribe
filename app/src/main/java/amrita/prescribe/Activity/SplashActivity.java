package amrita.prescribe.Activity;

// IMPORTS
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import amrita.prescribe.R;

public class SplashActivity extends AppCompatActivity {
    
    // Splash timeout set to 1500
    private static int SPLASH_TIME_OUT=5;
    
    FirebaseAuth mAuth;
    String currentUserID;
    DatabaseReference usersRef;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Initialises the Firebase services
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Patients");

        // Runnable thread for splash screen
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                usersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mAuth.getCurrentUser() != null)
                        {
                            // Redirects to Dashboard Activity
                            Intent homeIntent = new Intent(SplashActivity.this, DashboardActivity.class);
                            startActivity(homeIntent);
                            finish();
                        }
                        else
                        {
                            // Redirects to MainActivity
                            Intent homeIntent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(homeIntent);
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        },SPLASH_TIME_OUT);
    }
}
