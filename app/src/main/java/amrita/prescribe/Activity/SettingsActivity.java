package amrita.prescribe.Activity;

// IMPORTS
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import amrita.prescribe.R;

public class SettingsActivity extends AppCompatActivity {

    private Button logoutButton,resetButton;
    private FirebaseAuth mAuth;
    String currentUserID,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Initialising Firebase services
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        logoutButton = (Button) findViewById(R.id.logout_button);
        resetButton = (Button) findViewById(R.id.reset_button);

        // Logs out the user and redirects to SplashActivity
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent dashIntent = new Intent(SettingsActivity.this, SplashActivity.class);
                dashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(dashIntent);
            }
        });

        // Resets the password
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Patients");
                usersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            // Getting the email address of the user
                            email = snapshot.child(currentUserID).child("email").getValue().toString();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                
                // Sending password reset email
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    // Toasts on success
                                    Toast.makeText(SettingsActivity.this, "Password reset link has been sent successfully..!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });
    }
}
