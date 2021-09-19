package amrita.prescribe.Activity;

// IMPORTS
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import amrita.prescribe.R;

public class SignupActivity extends AppCompatActivity {

    private EditText FullnameET, PhoneNumET, EmailET, PassET, ConfPassET;
    private CheckBox roleCheck;
    private Button SignupButton;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    String currentUserID;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialising the Firebase services
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FullnameET = (EditText) findViewById(R.id.signup_fullname_et);
        PhoneNumET = (EditText) findViewById(R.id.signup_phnum_et);
        EmailET = (EditText) findViewById(R.id.signup_email_et);
        PassET = (EditText) findViewById(R.id.signup_pass_et);
        ConfPassET = (EditText) findViewById(R.id.signup_conf_pass_et);
        SignupButton = (Button) findViewById(R.id.signup_create_button);
        roleCheck = (CheckBox) findViewById(R.id.dctr_state_checkbox);

        // When the signup button is clicked
        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullname, phone_number, email, conf_pass, password,role;
                
                // Checking the role of the user
                if (roleCheck.isChecked())
                    role = "doctor";
                else
                    role = "patient";
                
                // Fetching the fullname, phone number, email, password from the user
                fullname = FullnameET.getText().toString();
                phone_number = PhoneNumET.getText().toString();
                email = EmailET.getText().toString();
                password = PassET.getText().toString();
                conf_pass = ConfPassET.getText().toString();

                // Checking whether the passwords match
                if(password.equals(conf_pass))
                {

                    //Checking for empty condition
                    if (!(TextUtils.isEmpty(fullname) && TextUtils.isEmpty(phone_number) && TextUtils.isEmpty(email)
                            && TextUtils.isEmpty(password) && TextUtils.isEmpty(conf_pass)))
                    {
                        progressDialog.setTitle("Please wait");
                        progressDialog.setMessage("We are creating your account");
                        progressDialog.show();
                        usersRef = FirebaseDatabase.getInstance().getReference().child("Patients");
                        
                        // Creates the account with the email and password
                        mAuth.createUserWithEmailAndPassword(email,password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful())
                                        {
                                            // Stores the user profile details to the realtime database
                                            currentUserID = mAuth.getCurrentUser().getUid();
                                            final HashMap newMap = new HashMap();
                                            newMap.put("fullname", fullname);
                                            newMap.put("phone_number", phone_number);
                                            newMap.put("email", email);
                                            newMap.put("uid",currentUserID);
                                            newMap.put("role", role);
                                            usersRef.child(currentUserID).updateChildren(newMap)
                                                    .addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if (task.isSuccessful())
                                                            {
                                                                if (role.equals("doctor"))
                                                                {
                                                                    // Storing the profile details in an additional space for Doctors if the user is a doctor
                                                                    DatabaseReference docRef = FirebaseDatabase.getInstance().getReference().child("Doctors");
                                                                    docRef.child(currentUserID).updateChildren(newMap);
                                                                }
                                                                
                                                                // Redirects to Dashboard activity
                                                                Intent dashboardIntent = new Intent(SignupActivity.this, DashboardActivity.class);
                                                                dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(dashboardIntent);
                                                            }
                                                            else
                                                            {
                                                                // Toasts the error
                                                                progressDialog.hide();
                                                                String msg = task.getException().getMessage();
                                                                Toast.makeText(SignupActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                    });
                                        }
                                        else
                                        {
                                            // Toasts the error
                                            progressDialog.hide();
                                            String err = task.getException().getMessage();
                                            Toast.makeText(SignupActivity.this, err, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else
                    {
                        // Toasts when any of the fields is empty
                        Toast.makeText(SignupActivity.this, "Please fill all the credentials", Toast.LENGTH_SHORT).show();
                    }
                }
                
                // Toasts when the passwords do not match
                else
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
