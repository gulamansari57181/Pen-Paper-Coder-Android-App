package com.example.penpapercoder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText edit_txt_Fullname, edit_txt_Username, edit_txt_Email, edit_txt_Pass, edit_txt_CoPass;
    private Button button_register;
    private TextView text_view_login;
    ProgressBar signUp_progress;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;


    String fullname, username, email, password, co_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //        get all view id from XML

        signUp_progress = findViewById(R.id.signUp_progress);
        edit_txt_Fullname = findViewById(R.id.edit_txt_Fullname);
        edit_txt_Username = findViewById(R.id.edit_txt_Username);
        edit_txt_Email = findViewById(R.id.edit_txt_Email);
        edit_txt_Pass = findViewById(R.id.edit_txt_Pass);
        edit_txt_CoPass = findViewById(R.id.edit_txt_CoPass);
        text_view_login = findViewById(R.id.text_view_login);
        button_register = findViewById(R.id.button_register);

        //        Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("UserData");

        text_view_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
               startActivity(intent);
            }
        });

        //        handle user SignUp button
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateFullname() || !validateUsername() || !validateEmail() || !validatePassword() ) {
                    return;
                }

                if (password.equals(co_password)) {

                    //    progressbar VISIBLE
                    signUp_progress.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                            (new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        UserData data = new UserData(fullname, username, email);

                                        FirebaseDatabase.getInstance().getReference("UserData")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(data).
                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        //    progressbar GONE
                                                        signUp_progress.setVisibility(View.GONE);

                                                        Toast.makeText(SignupActivity.this, "Successful Registered !", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });


                                    } else {
                                        //    progressbar GONE
                                        signUp_progress.setVisibility(View.GONE);
                                        Toast.makeText(SignupActivity.this, "Check Email id or Password !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(SignupActivity.this, "Password didn't match !", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private boolean validateFullname() {
        fullname = edit_txt_Fullname.getText().toString().trim();
        if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(SignupActivity.this, "Enter Your Full Name !", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateUsername() {
        username = edit_txt_Username.getText().toString().trim();
        if (TextUtils.isEmpty(username)&&!(TextUtils.isEmpty(fullname))) {
            Toast.makeText(SignupActivity.this, "Enter Your User Name !", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateEmail() {
        email = edit_txt_Email.getText().toString().trim();
        if (TextUtils.isEmpty(email)&&!(TextUtils.isEmpty(fullname))&&!(TextUtils.isEmpty(username))) {
            Toast.makeText(SignupActivity.this, "Enter Your Email !", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SignupActivity.this, "Please enter valid Email !", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePassword() {
        password = edit_txt_Pass.getText().toString().trim();
        co_password = edit_txt_CoPass.getText().toString().toLowerCase();

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(SignupActivity.this, "Enter Your Password !", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(co_password)) {
            Toast.makeText(SignupActivity.this, "Re- Enter Password Is Required !", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() <= 6) {
            Toast.makeText(SignupActivity.this, "Password is Very Short !", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    //    if the user already logged in then it will automatically send on Dashboard/MainActivity activity.
    @Override
    public void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);

        }
    }
}