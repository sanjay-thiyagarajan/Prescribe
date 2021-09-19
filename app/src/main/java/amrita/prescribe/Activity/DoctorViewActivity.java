package amrita.prescribe.Activity;

// IMPORTS
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import amrita.prescribe.R;

public class DoctorViewActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String uid;
    private DatabaseReference docRef;
    private TextView fullnameTV, emailTV, phoneTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_view);
        
        // Getting the UID passed through Intent
        uid = getIntent().getStringExtra("uid");
        
        docRef = FirebaseDatabase.getInstance().getReference().child("Doctors").child(uid);
        fullnameTV = (TextView) findViewById(R.id.doc_profile_fullname);
        emailTV = (TextView) findViewById(R.id.doc_profile_email);
        phoneTV = (TextView) findViewById(R.id.doc_profile_phone);

        
        docRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                
                // If the passed UID is valid and exists
                if (snapshot.exists())
                {
                    // Getting the fullname, email address and phone number of the doctor
                    String fullname, email, phone;
                    fullname = snapshot.child("fullname").getValue().toString();
                    email = snapshot.child("email").getValue().toString();
                    phone = snapshot.child("phone_number").getValue().toString();
                    fullnameTV.setText(fullname);
                    emailTV.setText(email);
                    phoneTV.setText(phone);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
