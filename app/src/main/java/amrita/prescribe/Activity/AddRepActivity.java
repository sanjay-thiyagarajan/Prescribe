package amrita.prescribe.Activity;

// IMPORTS
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import amrita.prescribe.R;


public class AddRepActivity extends AppCompatActivity {

    private TextView NameTV,DateTV,fileNameTV;
    private EditText DescET,TitleET;
    private Button AddButton,SubmitButton;
    private FirebaseAuth mAuth;
    private DatabaseReference patientsRef;
    private String currentUserID,patient_id,formattedDate;
    private Task<Uri> uriTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rep);
        
        // Initialising Firebase services
        mAuth = FirebaseAuth.getInstance();
        
        currentUserID = mAuth.getCurrentUser().getUid();
        patientsRef = FirebaseDatabase.getInstance().getReference().child("Patients");
        patient_id = getIntent().getStringExtra("uid");
        fileNameTV = (TextView) findViewById(R.id.file_name_tv);
        NameTV = (TextView) findViewById(R.id.add_rep_pname);
        DateTV = (TextView) findViewById(R.id.add_rep_pdate);
        TitleET  = (EditText) findViewById(R.id.add_rep_title);
        DescET = (EditText) findViewById(R.id.add_rep_desc);
        AddButton = (Button) findViewById(R.id.upload_doc_button);
        SubmitButton = (Button) findViewById(R.id.upload_report_button);

        // Getting current time
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        // Getting current date
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        formattedDate = df.format(c);

        // Rendering the fullname and the formatted current date
        patientsRef.child(patient_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    NameTV.setText(snapshot.child("fullname").getValue().toString());
                    DateTV.setText(formattedDate);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
        // Listener when Add Report Button is clicked
        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                String desc,title;
                desc = DescET.getText().toString();
                title = TitleET.getText().toString();
                
                // Checking the null state before uploading
                if (!(TextUtils.isEmpty(desc) && TextUtils.isEmpty(title)))
                {
                    if(isReadStoragePermissionGranted())
                    {
                        // Getting the PDF report from the patient using an Intent
                        Intent uploadIntent = new Intent();
                        uploadIntent.setType("application/pdf");
                        uploadIntent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(uploadIntent,"PDF FILE"),12);
                    }

                }
                else
                {
                    // Toasts when the description field is empty
                    Toast.makeText(AddRepActivity.this, "Please provide some description about the document", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // Checking Storage access permissions
    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { 
            // Permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==12 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            fileNameTV.setText(data.getDataString()
            .substring(data.getDataString().lastIndexOf("/") + 1));

            // Submit button is made VISIBLE after satisfying the base conditions
            SubmitButton.setVisibility(View.VISIBLE);

            SubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog progressDialog =  new ProgressDialog(AddRepActivity.this);
                    progressDialog.setTitle("Please wait");
                    progressDialog.setMessage("We are adding the report to the database");
                    progressDialog.show();

                    final String desc = DescET.getText().toString();
                    final String title = TitleET.getText().toString();
                    
                    // Stores the collected document in the Firebase Cloud Storage
                    StorageReference docRef = FirebaseStorage.getInstance().getReference()
                            .child("Reports").child(patient_id).child(title + formattedDate + ".pdf");

                    docRef.putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            // Getting the download URL of the document for reference
                            uriTask = taskSnapshot.getStorage().getDownloadUrl();

                        }
                    });

                    DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference()
                            .child("Patients").child(patient_id).child("Reports");

                    // Storing the report details in the realtime database
                    HashMap hashMap = new HashMap();
                    hashMap.put("title",title);
                    hashMap.put("desc",desc);
                    hashMap.put("report",formattedDate);
                    hashMap.put("doctor_id",currentUserID);
                    hashMap.put("date",formattedDate);

                    // Generating a unique ID by combining title and datetime for the report to act as a unique child in database
                    reportsRef.child(title+formattedDate).updateChildren(hashMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful())
                                    {
                                        // On success, redirects to Dashboard Activity
                                        Toast.makeText(AddRepActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                        Intent homeIntent = new Intent(AddRepActivity.this, DashboardActivity.class);
                                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(homeIntent);
                                    }
                                    else
                                    {
                                        // Toasts the error
                                        progressDialog.hide();
                                        String msg = task.getException().getMessage();
                                        Toast.makeText(AddRepActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                }
            });


        }
    }
}
