package com.triplefighter.brightside;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Button submit,register;
    EditText emailText,pass;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Typeface mTypeFace= Typeface.createFromAsset(getAssets(),"Nexa Light.otf");

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        emailText=(EditText) findViewById(R.id.email);
        pass=(EditText) findViewById(R.id.password);
        emailText.setTypeface(mTypeFace);
        pass.setTypeface(mTypeFace);

        submit=(Button) findViewById(R.id.submit);
        register=(Button) findViewById(R.id.register);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailText.getText().toString().trim();
                final String passwd = pass.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    emailText.setError("Required");
                }if(TextUtils.isEmpty(passwd)){
                    pass.setError("Required");
                }if (passwd.length() < 6) {
                    progressDialog.cancel();
                    Toast.makeText(LoginActivity.this, "Password minimum 6 kata!", Toast.LENGTH_SHORT).show();
                }

                progressDialog.setMessage("Please Wait");
                progressDialog.show();

                mAuth.signInWithEmailAndPassword(email, passwd)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    progressDialog.cancel();
                                    Toast.makeText(LoginActivity.this, "Log In Gagal, Silahkan Coba Kembali", Toast.LENGTH_SHORT).show();
                                } else {
                                    finish();
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                }
                            }
                        });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }
}
