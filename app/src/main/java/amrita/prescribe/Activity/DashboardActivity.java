package amrita.prescribe.Activity;

// IMPORTS
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import amrita.prescribe.R;

public class DashboardActivity extends AppCompatActivity {
    
    private LinearLayout myQrLT, verifyLT, addPrescLT, settingsLT, addRepLT, prescLLT, reportsLLT, myProfile;
    private DatabaseReference docRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        // Initialising Firebase services
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        
        myQrLT = (LinearLayout) findViewById(R.id.llt_myqr);
        verifyLT = (LinearLayout) findViewById(R.id.verify_dr_llt);
        addPrescLT = (LinearLayout) findViewById(R.id.add_presc_llt);
        settingsLT = (LinearLayout) findViewById(R.id.settings_llt);
        prescLLT = (LinearLayout) findViewById(R.id.prescs_llt);
        addRepLT = (LinearLayout) findViewById(R.id.add_rep_llt);
        reportsLLT = (LinearLayout) findViewById(R.id.reports_llt);
        myProfile = (LinearLayout)findViewById(R.id.ll_my_profile);
        docRef = FirebaseDatabase.getInstance().getReference().child("Doctors");

        // Displays the Add prescription and Add report sections only if the user is a DOCTOR
        docRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(currentUserID))
                {
                    addPrescLT.setVisibility(View.VISIBLE);
                    addRepLT.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }); 
        
        // Redirects to Profile Activity on click
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        
        // Redirects to My QR activity on click
        myQrLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent qrIntent = new Intent(DashboardActivity.this, MyQRActivity.class);
                startActivity(qrIntent);
            }
        });

        // Redirects to Verify doctor Activity on click
        verifyLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent qrIntent = new Intent(DashboardActivity.this, VerifyDoctorActivity.class);
                qrIntent.putExtra("role","patient");
                startActivity(qrIntent);
            }
        });

        // Redirects to Add Prescriptions Activity on click
        addPrescLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent qrIntent = new Intent(DashboardActivity.this, AddPrescActivity.class);
                qrIntent.putExtra("role","doctor");
                qrIntent.putExtra("page","prescription");
                startActivity(qrIntent);
            }
        });

        // Redirects to Add Report Activity on click
        addRepLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent qrIntent = new Intent(DashboardActivity.this, AddRepActivity.class);
                qrIntent.putExtra("role","doctor");
                qrIntent.putExtra("page","report");
                startActivity(qrIntent);
            }
        });
        
        // Redirects to Settings Activity on click
        settingsLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(DashboardActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
        
        // Redirects to Prescriptions Activity on click
        prescLLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pIntent = new Intent(DashboardActivity.this, PrescriptionsActivity.class);
                startActivity(pIntent);
            }
        });
        
        // Redirects to Reports Activity on click
        reportsLLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pIntent = new Intent(DashboardActivity.this, ReportsActivity.class);
                startActivity(pIntent);
            }
        });

    }
}
