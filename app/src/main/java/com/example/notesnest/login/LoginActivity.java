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

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;

    TextView createnewaccount;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            Toast.makeText(getApplicationContext(), "Welcome back, "+firebaseAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        }else {
            textInputEmail = findViewById(R.id.text_input_email);
            textInputPassword = findViewById(R.id.text_input_password);
            Button submitButton = findViewById(R.id.subButton);
            createnewaccount=findViewById(R.id.create);

            createnewaccount.setOnClickListener(view -> {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            });

            submitButton.setOnClickListener(view -> {

                String emailInput = textInputEmail.getEditText().getText().toString().trim();
                String passwordInput = textInputPassword.getEditText().getText().toString().trim();

                boolean check = validateinfo(emailInput, passwordInput);

                if (check) {
                    firebaseAuth.signInWithEmailAndPassword(emailInput,passwordInput).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Welcome, "+firebaseAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                            textInputPassword.getEditText().setText("");
                        }
                    });
                    //Toast.makeText(getApplicationContext(), "Data is valid", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry,Check Information Again", Toast.LENGTH_SHORT).show();
                    textInputEmail.setOnClickListener(v -> textInputEmail.setErrorEnabled(false));
                    textInputPassword.setOnClickListener(v -> textInputPassword.setErrorEnabled(false));
                }
            });
        }
    }

    private Boolean validateinfo(String emailInput, String passwordInput) {

        if (emailInput.length()==0){
            textInputEmail.requestFocus();
            textInputEmail.setError("Field can't be empty");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            textInputEmail.setError("Please provide valid e-mail!");
            textInputEmail.requestFocus();
            return false;
        }
        else if (passwordInput.length()<=5)
        {
            textInputPassword.requestFocus();
            textInputPassword.setError("Minimum 6 Characters Required");
            return false;
        }
        else {
            textInputEmail.setErrorEnabled(false);
            textInputPassword.setErrorEnabled(false);
            return true;
        }
    }
}