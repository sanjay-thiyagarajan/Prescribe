package amrita.prescribe.Activity;

// IMPORTS
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import amrita.prescribe.R;

public class PresViewActivity extends AppCompatActivity {

    TextView DnameTv, DateTv, PresTv;
    Button DeleteButton;
    ImageView qrView;
    FirebaseAuth mAuth;
    String pdate,dname,currentUserID,press,d_id;
    DatabaseReference presRef;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pres_view);
        
        // Initialising Firebase services
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        presRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(currentUserID).child("Prescriptions");

        DnameTv = (TextView) findViewById(R.id.press_dname);
        DateTv = (TextView) findViewById(R.id.press_pdate);
        PresTv = (TextView) findViewById(R.id.press_presc);
        DeleteButton = (Button) findViewById(R.id.presc_del_btn);
        qrView = (ImageView) findViewById(R.id.qr_view_pres);

        // Getting prescription date and doctor's name through Intent
        pdate = getIntent().getStringExtra("key");
        dname = getIntent().getStringExtra("dname");

        // Rendering the doctor's name and prescription date
        DnameTv.setText("Dr." + dname);
        DateTv.setText(pdate);
        
        // Displaying the prescriptions
        presRef.child(pdate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    // Getting the prescription details from the realtime database
                    press = snapshot.child("prescriptions").getValue().toString();
                    d_id = snapshot.child("doctor_id").getValue().toString();
                    PresTv.setText(press);
                    
                    // Generating a QR code for the doctor's profile and rendering
                    QRGEncoder qrgEncoder = new QRGEncoder(d_id, null, QRGContents.Type.TEXT, 100);
                    bitmap = qrgEncoder.getBitmap();
                    qrView.setImageBitmap(bitmap);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Deleting the prescriptions
        DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               presRef.child(pdate).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful())
                       {
                           // Toasts on deletion
                           Toast.makeText(PresViewActivity.this, "Prescription removed.", Toast.LENGTH_SHORT).show();
                           Intent pIntent = new Intent(PresViewActivity.this, PrescriptionsActivity.class);
                           pIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           startActivity(pIntent);
                       }
                       else
                       {
                           // Toasts the error
                           String msg = task.getException().getMessage();
                           Toast.makeText(PresViewActivity.this, "Error. " + msg, Toast.LENGTH_SHORT).show();
                       }

                   }
               });
            }
        });


    }
}
