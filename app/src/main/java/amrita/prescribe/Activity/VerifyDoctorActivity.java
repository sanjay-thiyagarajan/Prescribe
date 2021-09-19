package amrita.prescribe.Activity;

// IMPORTS
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import amrita.prescribe.R;

public class VerifyDoctorActivity extends AppCompatActivity {

    CodeScanner codeScanner;
    CodeScannerView codeScannerView;
    FirebaseAuth mAuth;
    DatabaseReference docRef;
    String docID,role;
    private static final int RC_PERMISSION = 10;
    private boolean mPermissionGranted;
    private int STORAGE_PERMISSION_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_doctor);
        
        // Initialising Firebase services
        mAuth = FirebaseAuth.getInstance();
        docRef = FirebaseDatabase.getInstance().getReference();
        
        // Getting the role of the user from the Intent
        role = getIntent().getStringExtra("role");

        codeScannerView = (CodeScannerView) findViewById(R.id.qr_scan_view);
        codeScanner = new CodeScanner(this,codeScannerView);
        
        //Requesting camera permissions
        requestPerm();

        // Callback on decoding
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                        docID = result.getText();
                        docRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists())
                                {
                                       if (role.equals("patient")) {
                                           
                                           // Redirects to Doctor View Activity
                                           Toast.makeText(VerifyDoctorActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                           Intent profileIntent = new Intent(VerifyDoctorActivity.this, DoctorViewActivity.class);
                                           profileIntent.putExtra("uid", docID);
                                           startActivity(profileIntent);
                                       }

                                        else if (role.equals("doctor"))
                                        {
                                            Toast.makeText(VerifyDoctorActivity.this, "Success", Toast.LENGTH_SHORT).show();

                                            // Redirects to AddPrescActivity
                                            if(getIntent().getStringExtra("page").equals("prescription"))
                                            {
                                                Intent profileIntent = new Intent(VerifyDoctorActivity.this, AddPrescActivity.class);
                                                profileIntent.putExtra("uid", docID);
                                                startActivity(profileIntent);
                                            }
                                            
                                            //Redirects to AddRepActivity
                                            if(getIntent().getStringExtra("page").equals("report"))
                                            {
                                                Intent profileIntent = new Intent(VerifyDoctorActivity.this, AddRepActivity.class);
                                                profileIntent.putExtra("uid", docID);
                                                startActivity(profileIntent);
                                            }
                                        }


                                }
                                else
                                {
                                    // Toasts when the doctor's profile is not authorised
                                    Toast.makeText(VerifyDoctorActivity.this, "Not authorized", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });

        codeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPerm();
            }
        });
    }

    //Permission handling
    public void requestPerm()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==
                PackageManager.PERMISSION_GRANTED)
        {
            codeScanner.startPreview();
        }
        else
        {
            requestPermission();
        }
    }

    // Requesting for permission
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA))
        {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("We need your permission to access your camera")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ActivityCompat.requestPermissions(VerifyDoctorActivity.this, new String[] {Manifest.permission.CAMERA}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true;
                codeScanner.startPreview();
            } else {
                mPermissionGranted = false;
            }
        }
    }
}
