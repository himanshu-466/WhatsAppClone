package com.mohit.newwhatsupp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohit.newwhatsupp.Models.profileinfo;
import com.mohit.newwhatsupp.R;
import com.mohit.newwhatsupp.databinding.ActivityProfileactivityBinding;

import org.jetbrains.annotations.NotNull;

public class profileactivity extends AppCompatActivity {
    ActivityProfileactivityBinding binding;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityProfileactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ProgressBar progressBar = findViewById(R.id.progressBarsetupprofile);
        auth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        binding.usernamebox.requestFocus();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null)
        {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();


//            Glide.with(this).load(String.valueOf(personPhoto)).into(binding.userimage);
//            selectedImage = personPhoto;


        }

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(profileactivity.this)
                        .crop()
                        //Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start(10);

//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent,45);
            }
        });
        binding.setupprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = binding.usernamebox.getText().toString();
                if (name.isEmpty())
                {
                    binding.usernamebox.setError("Please type a namwe");
                    return;
                }

                if (selectedImage != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    binding.setupprofile.setVisibility(View.INVISIBLE);


                    StorageReference reference = storage.getReference().child("Profiles").child((auth.getUid()));
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful())
                            {

                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // above uri me ek image ka path aa raha hai
                                        String imageUrl = uri.toString();
                                        String uid = auth.getCurrentUser().getUid();
                                        String name = binding.usernamebox.getText().toString();
                                        String phone = auth.getCurrentUser().getPhoneNumber();

//                                                  Users name ka ek model hai jiske contrutor ko yeh 4 value chahiy
                                        profileinfo user = new profileinfo(uid,name,imageUrl,phone);
                                        database.getReference().child("userprofile").child(uid).setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressBar.setVisibility(View.GONE);
                                                        binding.setupprofile.setVisibility(View.VISIBLE);
                                                        startActivity(new Intent(profileactivity.this, MainActivity.class));
                                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {
                                                progressBar.setVisibility(View.GONE);
                                                binding.setupprofile.setVisibility(View.VISIBLE);
                                                Toast.makeText(profileactivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                            }
                                        });



                                    }
                                });
                            }
                            else
                            {
                                progressBar.setVisibility(View.GONE);
                                binding.setupprofile.setVisibility(View.VISIBLE);
                                Toast.makeText(profileactivity.this, task.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });



                }
                else {

                    progressBar.setVisibility(View.VISIBLE);
                    binding.setupprofile.setVisibility(View.INVISIBLE);
                    String uid = auth.getUid();
                    String phone = auth.getCurrentUser().getPhoneNumber();
                    String email = auth.getCurrentUser().getEmail();

//                        String name = binding.namebox.getText().toString();   


                    profileinfo user = new profileinfo(uid,name,"No Image",phone);
                    database.getReference().child("userprofile").child(uid).setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressBar.setVisibility(View.GONE);
                                    binding.setupprofile.setVisibility(View.VISIBLE);
                                    startActivity(new Intent(profileactivity.this,MainActivity.class));
                                    finish();
                                }
                            });



//
                }
            }


        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10)
        {
//            get data uri ka ek method hai
            binding.userimage.setImageURI(data.getData());
//                jo bhi image aai yeh use ek variable me store karwana hai
            selectedImage = data.getData();
        }

    }
}