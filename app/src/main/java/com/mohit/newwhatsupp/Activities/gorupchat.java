package com.mohit.newwhatsupp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohit.newwhatsupp.Adapters.groupmessageAdapter;
import com.mohit.newwhatsupp.Models.messgaeModel;
import com.mohit.newwhatsupp.R;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class gorupchat extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;
    String senderRoom, recieverRoom;
    String senderUid;
    String recieveruid;
    ArrayList<messgaeModel> messages;
    groupmessageAdapter adapter;
    RecyclerView recyclerview;
    ImageView grpattach, grpcamera, grpsendbtn, grpbackarrow;
    EditText grpmsgtype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gorupchat);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Image");
        dialog.setCancelable(false);
        senderUid = FirebaseAuth.getInstance().getUid();
        messages = new ArrayList<>();
        adapter = new groupmessageAdapter(this, messages);
        recyclerview = findViewById(R.id.grpchatrecyclerview);
        grpattach = findViewById(R.id.grpattach);
        grpcamera = findViewById(R.id.grpcamera);
        grpmsgtype = findViewById(R.id.grpmesgtype);
        grpsendbtn = findViewById(R.id.grpsendbtn);
        grpbackarrow = findViewById(R.id.grpbackarrow);

        recyclerview.setAdapter(adapter);


        grpsendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messagetxt = grpmsgtype.getText().toString();
                Date date = new Date();
                messgaeModel model = new messgaeModel(messagetxt, senderUid, date.getTime());
                grpmsgtype.setText("");

                database.getReference().child("public").push().setValue(model);


            }
        });
        grpattach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(gorupchat.this)
                        .crop()
                        //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start(20);
            }
        });
        grpbackarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(gorupchat.this, MainActivity.class));
                finish();
            }
        });

        database.getReference().child("public")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            messgaeModel model = snapshot1.getValue(messgaeModel.class);
                            model.setMessageid(snapshot1.getKey());
                            messages.add(model);

                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(gorupchat.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20) {
            if (data != null) {
                if (data.getData() != null) {
//            get data uri ka ek method hai
//                    binding.ima.setImageURI(data.getData());
//                jo bhi image aai yeh use ek variable me store karwana hai
                    Calendar calendar = Calendar.getInstance();
                    Uri selectedImage = data.getData();
                    StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + " ");
                    dialog.show();
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filepath = uri.toString();
                                        String msgtext = grpmsgtype.getText().toString();
                                        Date date = new Date();
                                        messgaeModel model = new messgaeModel(msgtext,senderUid,date.getTime());
                                        model.setMessage("Photo");
                                        model.setImageurl(filepath);
                                        grpmsgtype.setText("");
                                       database.getReference().child("public").push().setValue(model);


                                    }
                                });
                            }

                        }
                    });

                }
            }
        }

    }
}