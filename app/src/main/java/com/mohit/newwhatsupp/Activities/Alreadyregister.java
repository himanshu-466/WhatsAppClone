package com.mohit.newwhatsupp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mohit.newwhatsupp.R;
import com.mohit.newwhatsupp.databinding.ActivityAlreadyregisterBinding;

public class Alreadyregister extends AppCompatActivity {
    Button login;
    TextView newaccount;
    EditText email,password;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alreadyregister);
        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        ProgressBar progressBar = findViewById(R.id.progressbar_login);
        login = findViewById(R.id.login);
        email = findViewById(R.id.emailin);
        password= findViewById(R.id.passwordin);
        newaccount = findViewById(R.id.newaccount);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty())
                {
                    progressBar.setVisibility(View.VISIBLE);
                    login.setVisibility(View.INVISIBLE);

                    String login_email = email.getText().toString();
                    String login_password = password.getText().toString();

                    auth.signInWithEmailAndPassword(login_email,login_password)
                            .addOnCompleteListener(Alreadyregister.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        login.setVisibility(View.VISIBLE);
                                        email.setText("");
                                        password.setText("");
                                        Intent intent = new Intent(Alreadyregister.this,profileactivity.class);
                                        startActivity(intent) ;
                                        finish();





                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        login.setVisibility(View.VISIBLE);
                                        email.setText("");
                                        password.setText("");
                                        Toast.makeText(Alreadyregister.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }

                else
                {
                    Toast.makeText(Alreadyregister.this, "Please Fill all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        newaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Alreadyregister.this,ResgiterYourself.class));

            }
        });

    }
}