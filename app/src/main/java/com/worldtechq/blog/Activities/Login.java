package com.worldtechq.blog.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.worldtechq.blog.R;

public class Login extends AppCompatActivity {
    EditText loged1, loged2;
    Button logbtn;
    TextView tv1;
    ProgressBar progressBar;
    FirebaseAuth auth1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        loged1 = findViewById(R.id.logmail);
        loged2 = findViewById(R.id.logpass);
        tv1 = findViewById(R.id.logtxt);
        logbtn = findViewById(R.id.logbtn);

        auth1 = FirebaseAuth.getInstance();

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Login.this, Register.class);
                startActivity(intent1);
            }
        });

        logbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String logmail = loged1.getText().toString();
                String logpass = loged2.getText().toString();

                if (logmail.isEmpty() || logpass.isEmpty()) {
                    showmsg("verify all fields");
                } else {
                    signIn(logmail, logpass);
                }
            }
        });
    }

    //method for login(matching your email and password)
    private void signIn(String logmail, String logpass) {

        auth1.signInWithEmailAndPassword(logmail, logpass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    UpdateUI();
                } else {
                    showmsg(task.getException().getMessage());
                }
            }
        });
    }

    //for changing activity
    private void UpdateUI() {
        Intent intent3 = new Intent(Login.this, ShowImages.class);
        startActivity(intent3);
        finish();
    }

    //for showing the toast
    private void showmsg(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    //kind of shared preference
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth1.getCurrentUser();
        if (user != null) {
            UpdateUI();
        }
    }


}


