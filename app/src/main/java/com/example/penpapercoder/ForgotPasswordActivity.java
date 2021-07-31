package com.example.penpapercoder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edit_txt_resetEmail;
    private Button button_resetPassword;
    private ProgressBar resetPassword_progress;
    String email;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        //        get all view id from XML
        edit_txt_resetEmail = findViewById(R.id.edit_txt_resetEmail);
        button_resetPassword = findViewById(R.id.button_resetPassword);
        resetPassword_progress = findViewById(R.id.resetPassword_progress);

        //        Get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();


        button_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetPassword_progress.setVisibility(View.VISIBLE);

                firebaseAuth.sendPasswordResetEmail(edit_txt_resetEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                resetPassword_progress.setVisibility(View.GONE);
//                                email=edit_txt_resetEmail.getText().toString().trim();
//                                if (TextUtils.isEmpty(email)) {
//                                    Toast.makeText(ForgotPasswordActivity.this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
//                                }

                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPasswordActivity.this, "Password reset link sent to your Email", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    resetPassword_progress.setVisibility(View.GONE);
                                    Toast.makeText(ForgotPasswordActivity.this, " Oops ,This e-mail is not registered with us.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });


    }
}