package com.triplefighter.brightside;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.triplefighter.brightside.Model.UserInformation;

public class RegisterActivity extends AppCompatActivity {

    EditText email, pass, username;
    Button register;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        email = (EditText)findViewById(R.id.emailText);
        username = (EditText)findViewById(R.id.usernameText);
        pass = (EditText)findViewById(R.id.passText);
        register = (Button)findViewById(R.id.sign_up);
        progressDialog = new ProgressDialog(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ema = email.getText().toString().trim();
                final String usernm = username.getText().toString().trim();
                final String passwd = pass.getText().toString().trim();

                if(TextUtils.isEmpty(ema)){
                    email.setError("Masukkan Alamat Email!");
                }if(TextUtils.isEmpty(usernm)){
                    username.setError("Masukkan Username!");
                }if(TextUtils.isEmpty(passwd)){
                    pass.setError("Masukkan Password!");
                }if(passwd.length() < 6){
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(), "Password minimum 6 kata!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Registering User");
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(ema, passwd)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    progressDialog.cancel();
                                    Toast.makeText(RegisterActivity.this, "Email anda telah digunakan.",
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    UserInformation userInformation = new UserInformation(ema, usernm);

                                    FirebaseUser user = mAuth.getCurrentUser();

                                    databaseReference.child("Data User").child(user.getUid()).setValue(userInformation);
                                    mAuth.signOut();
                                    finish();
                                    Toast.makeText(RegisterActivity.this, "Registrasi akun anda berhasil.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
