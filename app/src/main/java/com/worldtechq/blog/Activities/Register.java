package com.worldtechq.blog.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.worldtechq.blog.R;

public class Register extends AppCompatActivity {
    EditText ed1, ed2, ed3, ed4;
    Button btn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        ed1 = findViewById(R.id.regname);
        ed2 = findViewById(R.id.regmail);
        ed3 = findViewById(R.id.regpass);
        ed4 = findViewById(R.id.regpass2);
        btn = findViewById(R.id.regbtn);

        auth = FirebaseAuth.getInstance();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ed1.getText().toString();
                String email = ed2.getText().toString();
                String password = ed3.getText().toString();
                String password2 = ed4.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    showmessage("please verify all fields");
                } else if (!name.matches("[A-Za-z0-9]+") || !email.matches("[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+") || !password.equals(password2)) {
                    showmessage("credentials are not valid");
                } else {
                    btn.setVisibility(View.INVISIBLE);
                    createaccount(name, email, password);
                }
            }
        });
    }


    private void createaccount(String name, String email, String password) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    showmessage("Account created");
                    Intent intent = new Intent(Register.this, Login.class);
                    startActivity(intent);
                } else {
                    showmessage("Account createion is failed" + task.getException().getMessage());
                    btn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showmessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
