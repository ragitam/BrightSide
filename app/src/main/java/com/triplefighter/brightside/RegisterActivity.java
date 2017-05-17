package com.triplefighter.brightside;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
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
        Typeface mTypeFace= Typeface.createFromAsset(getAssets(),"Nexa Light.otf");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        email = (EditText)findViewById(R.id.emailText);
        username = (EditText)findViewById(R.id.usernameText);
        pass = (EditText)findViewById(R.id.passText);
        register = (Button)findViewById(R.id.sign_up);
        progressDialog = new ProgressDialog(this);
        email.setTypeface(mTypeFace);
        username.setTypeface(mTypeFace);
        pass.setTypeface(mTypeFace);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ema = email.getText().toString().trim();
                final String usernm = username.getText().toString().trim();
                final String passwd = pass.getText().toString().trim();

                if(TextUtils.isEmpty(ema)){
                    email.setError(getText(R.string.enter_email));
                    return;
                }if(TextUtils.isEmpty(usernm)){
                    username.setError(getText(R.string.enter_username));
                    return;
                }if(TextUtils.isEmpty(passwd)){
                    pass.setError(getText(R.string.enter_password));
                    return;
                }if(passwd.length() < 6){
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(), R.string.password_length, Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Registering User");
                progressDialog.show();

                if(ema.isEmpty()){

                }

                mAuth.createUserWithEmailAndPassword(ema, passwd)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    progressDialog.cancel();

                                    try {
                                        throw task.getException();
                                    } catch(FirebaseAuthWeakPasswordException e) {
                                        Toast.makeText(RegisterActivity.this, R.string.password_weak,
                                                Toast.LENGTH_SHORT).show();
                                    } catch(FirebaseAuthInvalidCredentialsException e) {
                                        Toast.makeText(RegisterActivity.this, R.string.invalid_email,
                                                Toast.LENGTH_SHORT).show();
                                    } catch(FirebaseAuthUserCollisionException e) {
                                        Toast.makeText(RegisterActivity.this, R.string.email_used,
                                                Toast.LENGTH_SHORT).show();
                                    } catch(Exception e) {
                                        Log.e("Register", e.getMessage());
                                    }
                                }else{
                                    UserInformation userInformation = new UserInformation(ema, usernm);

                                    FirebaseUser user = mAuth.getCurrentUser();

                                    databaseReference.child("Data User").child(user.getUid()).setValue(userInformation);
                                    mAuth.signOut();
                                    finish();
                                    Toast.makeText(RegisterActivity.this, R.string.success_regis,
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
