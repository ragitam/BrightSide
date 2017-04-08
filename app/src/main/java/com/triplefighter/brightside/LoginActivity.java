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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText ema, passwd;
    private Button signIn, btn_register;
    private ProgressDialog progressDialog;

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

        ema = (EditText) findViewById(R.id.email);
        passwd = (EditText) findViewById(R.id.password);
        signIn = (Button) findViewById(R.id.submit);
        btn_register = (Button) findViewById(R.id.register);
        ema.setTypeface(mTypeFace);
        passwd.setTypeface(mTypeFace);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ema.getText().toString().trim();
                final String password = passwd.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    ema.setError("Masukkan Alamat Email!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwd.setError("Masukkan Password!");
                    return;
                }
                if (password.length() < 6) {
                    progressDialog.cancel();
                    Toast.makeText(LoginActivity.this, "Password minimum 6 kata!", Toast.LENGTH_SHORT).show();
                }

                progressDialog.setMessage("Please Wait");
                progressDialog.show();

                //authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    progressDialog.cancel();
                                    Toast.makeText(LoginActivity.this, "Log In gagal, Silahkan fragment_statistic kembali", Toast.LENGTH_SHORT).show();
                                } else {
                                    finish();
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                }
                            }
                        });
            }
        });
    }
}
