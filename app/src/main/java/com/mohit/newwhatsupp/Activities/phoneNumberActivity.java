package com.mohit.newwhatsupp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mohit.newwhatsupp.R;
import com.mohit.newwhatsupp.databinding.ActivityPhoneNumberBinding;

public class phoneNumberActivity extends AppCompatActivity {
ActivityPhoneNumberBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ProgressBar progressBar = findViewById(R.id.progressbar_sending_otp);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.numberotp1.getText().toString().trim().isEmpty())
                {
                if(binding.numberotp1.length()!=10)
                {
                    Toast.makeText(phoneNumberActivity.this, "Please Input valid Number", Toast.LENGTH_SHORT).show();
                }

                else {
                    progressBar.setVisibility(View.VISIBLE);
                    binding.otp.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(phoneNumberActivity.this, verifyOtp.class);
                    intent.putExtra("mobilenumber", binding.numberotp1.getText().toString());
                    startActivity(intent);
                }


                }
                else
                {
                    Toast.makeText(phoneNumberActivity.this, "Please Input a number", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

   }
