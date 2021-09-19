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

import amrita.prescribe.Model.Reports;
import amrita.prescribe.R;
import amrita.prescribe.Viewholder.ReportsViewHolder;

public class ReportsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView msgTv;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    FirebaseAuth mAuth;
    String currentUserID,rptitle,rpdate;
    DatabaseReference repRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        
        // Initialising the Firebase services
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        recyclerView = (RecyclerView) findViewById(R.id.reports_recycler_view);
        msgTv = (TextView) findViewById(R.id.rmesg1);
        repRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(currentUserID).child("Reports");

        repRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                {
                    // Hides the recycler view if thee are no reports and displays the empty message
                    recyclerView.setVisibility(View.GONE);
                    msgTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<Reports> options =
                new FirebaseRecyclerOptions.Builder<Reports>()
                        .setQuery(repRef, Reports.class)
                        .build();

        // Adapter for holding the reports
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Reports, ReportsViewHolder>(options)
                {
                    @NonNull
                    @Override
                    public ReportsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.reports_view_layout, parent,false);

                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                
                                // Redirects to RepViewActivity if the user clicks on the report
                               Intent repIntent = new Intent(ReportsActivity.this, RepViewActivity.class);
                               repIntent.putExtra("title",rptitle);
                               repIntent.putExtra("date",rpdate);
                               startActivity(repIntent);
                            }
                        });

                        return new ReportsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ReportsViewHolder holder, int position, @NonNull Reports model) {
                        
                        // Fetching the report title and date
                        rptitle = model.getTitle();
                        rpdate = model.getDate();
                        holder.setTitle(rptitle);
                        holder.setDate(rpdate);
                    }
                };
        
        // Setting and binding the adapter with the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    // Starts listening when Activity starts
    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    // Stops listening when the Activity stops
    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    // Starts listening when the Activity resumes
    @Override
    protected void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
    }
}

