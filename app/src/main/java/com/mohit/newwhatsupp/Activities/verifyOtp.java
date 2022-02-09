package com.mohit.newwhatsupp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mohit.newwhatsupp.R;
import com.mohit.newwhatsupp.databinding.ActivityVerifyOtpBinding;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class verifyOtp extends AppCompatActivity {
ActivityVerifyOtpBinding binding;
FirebaseAuth auth;
String VerificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Button getotpbutton = findViewById(R.id.numberotpbtn2);
        Button resendotpButton = findViewById(R.id.resendotp);
        ProgressBar progressBar = findViewById(R.id.progressbar_verify_otp);
        ProgressBar progressBar1 = findViewById(R.id.progressbar_verify_otp1);
        String number=getIntent().getStringExtra("mobilenumber");
        binding.otp3text3.setText("Verify " +number);


        auth = FirebaseAuth.getInstance();


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + number, 60, TimeUnit.SECONDS,
                verifyOtp.this,new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull @org.jetbrains.annotations.NotNull PhoneAuthCredential phoneAuthCredential) {
                        progressBar.setVisibility(View.GONE);
                        getotpbutton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull @org.jetbrains.annotations.NotNull FirebaseException e) {
                        progressBar.setVisibility(View.GONE);
                        getotpbutton.setVisibility(View.VISIBLE);
                        Toast.makeText(verifyOtp.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onCodeSent(@NonNull @NotNull String verifyid, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyid, forceResendingToken);
                        VerificationId = verifyid;
                    }
                });


        binding.numberotpbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.inputopt1.getText().toString().isEmpty() && !binding.inputopt2.getText().toString().isEmpty() && !binding.inputopt3.getText().toString().isEmpty()
                        && !binding.inputopt4.getText().toString().isEmpty()&& !binding.inputopt5.getText().toString().isEmpty()&& !binding.inputopt6.getText().toString().isEmpty())
                {
                    String entercodeotp = binding.inputopt1.getText().toString() +
                            binding.inputopt2.getText().toString() +
                            binding.inputopt3.getText().toString() +
                            binding.inputopt4.getText().toString() +
                            binding.inputopt5.getText().toString() +
                            binding.inputopt6.getText().toString() ;

                    if(VerificationId!=null)
                    {

                        progressBar.setVisibility(View.VISIBLE);
                        getotpbutton.setVisibility(View.INVISIBLE);

                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(VerificationId,entercodeotp);
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressBar.setVisibility(View.GONE);
                                        getotpbutton.setVisibility(View.VISIBLE);

                                        if(task.isSuccessful())
                                        {
                                            Intent intent = new Intent(getApplicationContext(), profileactivity.class);
//                                            yeh code isliy taki us activity me return na a pay
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            Toast.makeText(verifyOtp.this, "Enter the valid otp", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else {
                        Toast.makeText(verifyOtp.this,"Verification Failed" , Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(verifyOtp.this, "Please Enter all number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        numberotpmove();

        binding.resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar1.setVisibility(View.VISIBLE);
                resendotpButton.setVisibility(View.GONE);
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + getIntent().getStringExtra("mobilenumber"), 60, TimeUnit.SECONDS,
                        verifyOtp.this,new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull @org.jetbrains.annotations.NotNull PhoneAuthCredential phoneAuthCredential) {
                                progressBar1.setVisibility(View.GONE);
                                resendotpButton.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull @org.jetbrains.annotations.NotNull FirebaseException e) {
                                progressBar1.setVisibility(View.GONE);
                                resendotpButton .setVisibility(View.VISIBLE);
                                Toast.makeText(verifyOtp.this, "Otp deliver failed", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCodeSent(@NonNull @NotNull String newVerification, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(newVerification, forceResendingToken);
                                VerificationId = newVerification;
                                progressBar1.setVisibility(View.GONE);
                                resendotpButton .setVisibility(View.VISIBLE);
                                Toast.makeText(verifyOtp.this, "OTP Sends Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });





    }

    private void numberotpmove() {

// Handling Delete events

        binding.inputopt1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL)
                {
                    binding.inputopt1.requestFocus();
                }
                return false;
            }
        });
        binding.inputopt2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL)
                {
                    binding.inputopt1.requestFocus();
                }
                return false;
            }
        });
        binding.inputopt3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL)
                {
                    binding.inputopt2.requestFocus();
                }
                return false;
            }
        });
        binding.inputopt4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL)
                {
                    binding.inputopt3.requestFocus();
                }
                return false;
            }
        });
        binding.inputopt5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL)
                {
                    binding.inputopt4.requestFocus();
                }
                return false;
            }
        });
        binding.inputopt6.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL)
                {
                    binding.inputopt5.requestFocus();
                }
                return false;
            }
        });
        binding.inputopt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty())
                {
                    binding.inputopt2.requestFocus();
                }


            }


            @Override
            public void afterTextChanged(Editable s) {


            }
        });



        binding.inputopt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty())
                {
                    binding.inputopt3.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.inputopt3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty())
                {
                    binding.inputopt4.requestFocus();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.inputopt4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 if(!s.toString().trim().isEmpty())
                {
                    binding.inputopt5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.inputopt5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty())
                {
                    binding.inputopt6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }

}