package com.example.notesnest.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesnest.R;
import com.example.notesnest.dashboard.DashboardActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    TextView alreadyhaveacc;
    private FirebaseAuth mAuth;
    private TextInputLayout editTextName, editTextEmail,editTextPassword;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextEmail= findViewById(R.id.text_input_email);
        editTextName= findViewById(R.id.text_input_username);
        editTextPassword= findViewById(R.id.text_input_confirmpassword);
        alreadyhaveacc=findViewById(R.id.alreadyhaveacc);
        alreadyhaveacc.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            finish();
        });
        mAuth= FirebaseAuth.getInstance();
        submitButton = findViewById(R.id.subButton);
        submitButton.setOnClickListener(view -> {
            String email=editTextEmail.getEditText().getText().toString().trim();
            String password=editTextPassword.getEditText().getText().toString().trim();
            String name=editTextName.getEditText().getText().toString().trim();
            boolean check =validateinfo(email,password,name);

            if (check){
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task1 -> {

                                });
                    }
                });
                Toast.makeText(getApplicationContext(), "User Created Successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                finish();
            }
            else {
                editTextEmail.getEditText().setText("");
                editTextName.getEditText().setText("");
                editTextPassword.getEditText().setText("");
                Toast.makeText(RegisterActivity.this, "Sorry,Check Information Again", Toast.LENGTH_SHORT).show();
                editTextEmail.setOnClickListener(v -> editTextEmail.setErrorEnabled(false));
                editTextPassword.setOnClickListener(v -> editTextPassword.setErrorEnabled(false));
                editTextName.setOnClickListener(v -> editTextName.setErrorEnabled(false));
            }
        });
    }

    private boolean validateinfo(String email, String password, String name) {
        if (name.length()==0){
            editTextName.requestFocus();
            editTextName.setError("Field can't be empty");
            return false;
        }
        if (!name.matches("[a-zA-Z]+"))
        {
            editTextName.requestFocus();
            editTextName.setError("Enter only Alphabetical Characters");
            return false;
        }
        if(email.isEmpty()){
            editTextEmail.setError("E-mail is required!");
            editTextEmail.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide valid e-mail!");
            editTextEmail.requestFocus();
            return false;
        }
        if (password.length()<=5)
        {
            editTextPassword.requestFocus();
            editTextPassword.setError("Minimum 6 Characters Required");
            return false;
        }
        else {
            editTextEmail.setErrorEnabled(false);
            editTextPassword.setErrorEnabled(false);
            editTextName.setErrorEnabled(false);
            return true;
        }
    }
}