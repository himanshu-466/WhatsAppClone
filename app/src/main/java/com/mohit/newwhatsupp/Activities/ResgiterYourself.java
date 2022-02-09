package com.mohit.newwhatsupp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.mohit.newwhatsupp.Models.userInformation;
import com.mohit.newwhatsupp.R;
import com.mohit.newwhatsupp.databinding.ActivityResgiterYourselfBinding;

import org.jetbrains.annotations.NotNull;

public class ResgiterYourself extends AppCompatActivity {
   ActivityResgiterYourselfBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog dialog;
    FirebaseStorage storage;

    // this is for user have already signin
    @Override
    public void onStart() {
        super.onStart();
        if (auth.getCurrentUser()!=null)
        {
            startActivity(new Intent(ResgiterYourself.this,MainActivity.class));
            finish();
        }

    }
//    ---------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResgiterYourselfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // Below line is use to remove status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Button register = findViewById(R.id.register);
        ProgressBar progressBar = findViewById(R.id.progressbar_register_user);
        dialog = new ProgressDialog(this);
        dialog.setMessage("User is getting Register");
        dialog.setCancelable(false);
        processrequest();

        binding.facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                processlogin();



            }
        });

        binding.mobilenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResgiterYourself.this,phoneNumberActivity.class));
                
            }
        });

        binding.alredyregisterdemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResgiterYourself.this,Alreadyregister.class));
            }
        });

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.name.getText().toString().trim().isEmpty() &&!binding.email.getText().toString().trim().isEmpty() &&!binding.password.getText().toString().trim().isEmpty() && !binding.retypePassword.getText().toString().trim().isEmpty()) {

                    if(binding.password.getText().length()>7 )
                       {
                           if(binding.password.getText().toString().equals(binding.retypePassword.getText().toString()))
                           {
                        progressBar.setVisibility(View.VISIBLE);
                        register.setVisibility(View.INVISIBLE);
                        String email = binding.email.getText().toString();
                        String name = binding.name.getText().toString();
                        String password = binding.password.getText().toString();
                        String rtypass = binding.retypePassword.getText().toString();


                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(ResgiterYourself.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            userInformation user = new userInformation(auth.getUid(),binding.name.getText().toString(),binding.email.getText().toString(),"Not Available",binding.password.getText().toString(),"Not Available");
                                            database.getReference().child("userinfo").child(auth.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    register.setVisibility(View.VISIBLE);
                                                    binding.email.setText("");
                                                    binding.password.setText("");
                                                    binding.name.setText("");
                                                    binding.retypePassword.setText("");

                                                    Toast.makeText(ResgiterYourself.this, "User Register Successfully", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(ResgiterYourself.this,Alreadyregister.class);
                                                    startActivity(intent);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull @NotNull Exception e) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    register.setVisibility(View.VISIBLE);
                                                    Toast.makeText(ResgiterYourself.this, e.toString(), Toast.LENGTH_LONG).show();

                                                }
                                            });

                                        } else {
                                            binding.email.setText("");
                                            binding.password.setText("");
                                            binding.name.setText("");
                                            binding.retypePassword.setText("");
                                            progressBar.setVisibility(View.INVISIBLE);
                                            register.setVisibility(View.VISIBLE);
                                            Toast.makeText(ResgiterYourself.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else
                    {
                        binding.retypePassword.setError("Password Does not Matched");
                        binding.retypePassword.requestFocus();

                    }
                }
                    else
                    {
                        binding.password.setError("Password Must be of 8 Character");
                        binding.password.requestFocus();
                    }
                }
                else
                {
                    Toast.makeText(ResgiterYourself.this, "Please Input all Fields", Toast.LENGTH_SHORT).show();
                }





    }
});

}

// google signin process begin
    private void processrequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void processlogin() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 103);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 103) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                dialog.show();
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = auth.getCurrentUser();
                            String Email = user.getEmail();
                            String Name = user.getDisplayName();
                            String imageurl = user.getPhotoUrl().toString();
                            String uid = user.getUid().toString();
                            String phone = user.getPhoneNumber();

                                                userInformation dum = new userInformation(uid,Name,Email,imageurl,"null","Not Availabe");
                                                database.getReference().child("userinfo").child(uid).setValue(dum).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        dialog.dismiss();
                                                        startActivity(new Intent(ResgiterYourself.this,profileactivity.class));
                                                        Toast.makeText(ResgiterYourself.this, "User Register Successfully", Toast.LENGTH_SHORT).show();
                                                        finish();

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull @NotNull Exception e) {
                                                        Toast.makeText(ResgiterYourself.this, e.toString(), Toast.LENGTH_LONG).show();

                                                    }
                                                });











                        } else {
                            Toast.makeText(ResgiterYourself.this, task.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    // Google signing prcoess end

//     Facebook Login Process


}