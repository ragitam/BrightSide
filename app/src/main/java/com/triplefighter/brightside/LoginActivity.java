package com.triplefighter.brightside;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    Button submit,register;
    EditText username,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Typeface mTypeFace= Typeface.createFromAsset(getAssets(),"Nexa Light.otf");
        username=(EditText) findViewById(R.id.username);
        pass=(EditText) findViewById(R.id.password);
        username.setTypeface(mTypeFace);
        pass.setTypeface(mTypeFace);

        submit=(Button) findViewById(R.id.submit);
        register=(Button) findViewById(R.id.register);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
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
