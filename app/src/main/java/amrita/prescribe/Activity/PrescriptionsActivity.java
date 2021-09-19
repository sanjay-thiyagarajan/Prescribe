package amrita.prescribe.Activity;

// IMPORTS
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import amrita.prescribe.Model.Prescriptions;
import amrita.prescribe.R;
import amrita.prescribe.Viewholder.PrescriptionsViewHolder;

public class PrescriptionsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference prescRef;
    FirebaseAuth mAuth;
    String currentUserID,dt,dnamee;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    TextView msgTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescriptions);
        
        // Initialising the Firebase services
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        msgTV = (TextView) findViewById(R.id.pmesg1);
        prescRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(currentUserID)
                .child("Prescriptions");

        
        prescRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                {
                    // Makes the recycler view visible and the empty state message invisible when the prescriptions exist in the patient's account
                   recyclerView.setVisibility(View.GONE);
                   msgTV.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.prescriptions_recycler_view);

        FirebaseRecyclerOptions<Prescriptions> options =
                new FirebaseRecyclerOptions.Builder<Prescriptions>()
                        .setQuery(prescRef, Prescriptions.class)
                        .build();

        // Adapter for holding the prescriptions
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Prescriptions, PrescriptionsViewHolder>(options)
        {

            @NonNull
            @Override
            public PrescriptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.prescription_view_layout,parent,false);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        
                        // Redirects to PresViewActivity when the user clicks on a prescription
                        Intent presIntent = new Intent(PrescriptionsActivity.this, PresViewActivity.class);
                        presIntent.putExtra("key",dt);
                        presIntent.putExtra("dname",dnamee);
                        startActivity(presIntent);
                    }
                });

                return new PrescriptionsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PrescriptionsViewHolder holder, int position, @NonNull Prescriptions model) {
                
                // Fetching the details from the model
                dnamee = model.getDr_name();
                holder.setDr_name(dnamee);
                dt = getRef(position).getKey().toString();
                holder.setDate(dt);

            }
        };
        
        // Setting up and binding the adapter with the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();


    }

    // Starts listening when the activity starts
    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.startListening();
    }

    // Stops listening when the activity stops
    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.stopListening();
    }

    // Resumes listening when the activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        if (firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.startListening();
    }
}

