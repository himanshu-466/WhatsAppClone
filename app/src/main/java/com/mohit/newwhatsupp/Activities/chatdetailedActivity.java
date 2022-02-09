package com.mohit.newwhatsupp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
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
import com.mohit.newwhatsupp.Adapters.messageAdapter;
import com.mohit.newwhatsupp.Models.messgaeModel;
import com.mohit.newwhatsupp.R;
import com.mohit.newwhatsupp.databinding.ActivityChatdetailedBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class chatdetailedActivity extends AppCompatActivity {
ActivityChatdetailedBinding binding;
ArrayList<messgaeModel> msgmodel;
String sender_Room,receiver_Room;
FirebaseAuth auth;
FirebaseDatabase database;
FirebaseStorage storage;
messageAdapter adapter;
RecyclerView chatdetailrecyclerview;
ProgressDialog dialog;
String senderuid;


 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatdetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        msgmodel = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        chatdetailrecyclerview = findViewById(R.id.chatdetailrecyclerview);
        adapter  = new messageAdapter(msgmodel,chatdetailedActivity.this,sender_Room,receiver_Room);
       chatdetailrecyclerview.setAdapter(adapter);
       dialog = new ProgressDialog(this);
       dialog.setTitle("Uploading image ....");
       dialog.setCancelable(false);




        String image = getIntent().getStringExtra("userimage");
        String name = getIntent().getStringExtra("username");
        String token = getIntent().getStringExtra("token");
        Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
//        isme  receiveruid isliy aai kyoki hmane chilgren get kiy or sabko show kiya apni main activity mein
        String receiveruid = getIntent().getStringExtra("uid");
         senderuid = auth.getUid();

        sender_Room = senderuid+receiveruid;
        receiver_Room = receiveruid + senderuid;



        binding.chatdetailName.setText(name);


        Glide.with(getApplicationContext()).load(image).placeholder(R.drawable.placeholder).into(binding.chatdetailimage);

        binding.backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.mesgtype.getText().toString();
                Date date = new Date();
                messgaeModel model = new messgaeModel(message,senderuid,date.getTime());
                binding.mesgtype.setText("");
                String randomkey =  database.getReference().push().getKey();
                HashMap<String ,Object> lastmsgobj = new HashMap<>();
                lastmsgobj.put("lastmsg",model.getMessage());
                lastmsgobj.put("lastmsgtime",date.getTime());
                database.getReference().child("Chats").child(sender_Room).updateChildren(lastmsgobj);
                database.getReference().child("Chats").child(receiver_Room).updateChildren(lastmsgobj);

                database.getReference().child("Chats").child(sender_Room).child("Message").child(randomkey).setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("Chats").child(receiver_Room).child("Message").child(randomkey).setValue(model)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        sendnotification(name,model.getMessage(),token);

                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                    }
                });





            }
        });
        final Handler handler = new Handler();
        binding.mesgtype.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                database.getReference().child("presence").child(senderuid).setValue("Typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedtyping,1000);


            }
            Runnable userStoppedtyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderuid).setValue("Online");
                }
            };
        });

        database.getReference().child("presence").child(receiveruid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String status = snapshot.getValue(String.class);
                    if(!status.isEmpty())
                    {
                        if(status.equals("offline")) {
                            binding.chatonline.setVisibility(View.GONE);

                        }
                        else
                        {
                            binding.chatonline.setVisibility(View.VISIBLE);
                            binding.chatonline.setText(status);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        binding.attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(chatdetailedActivity.this)
                        .crop()
                        //Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)
                        .galleryOnly()//Final image resolution will be less than 1080 x 1080(Optional)
                        .start(20);
            }
        });

        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(chatdetailedActivity.this)
                        .crop()
                        //Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)
                        .cameraOnly()//Final image resolution will be less than 1080 x 1080(Optional)
                        .start(30);
            }
        });
        database.getReference().child("Chats").child(sender_Room).child("Message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                msgmodel.clear();
                for(DataSnapshot snapshot1 :snapshot.getChildren())
                {
                    messgaeModel  model  = snapshot1.getValue(messgaeModel.class);
                    model.setMessageid(snapshot1.getKey());
                    msgmodel.add(model);
                }
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==30)
        {

        }
        else if(requestCode==20)
        {
            if(data!=null)
            {
                if(data.getData()!=null)
                {
                    Uri selectedimage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("Chats").child(calendar.getTimeInMillis()+"");
                    dialog.show();
                    reference.putFile(selectedimage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if(task.isSuccessful())
                            {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageurl = uri.toString();
                                        String message = binding.mesgtype.getText().toString();
                                        Date date = new Date();
                                        messgaeModel model = new messgaeModel(message,senderuid,date.getTime());
                                        model.setMessage("Photo");
                                        model.setImageurl(imageurl);
                                        binding.mesgtype.setText("");
                                        String randomkey =  database.getReference().push().getKey();
                                        HashMap<String ,Object> lastmsgobj = new HashMap<>();
                                        lastmsgobj.put("lastmsg",model.getMessage());
                                        lastmsgobj.put("lastmsgtime",date.getTime());
                                        database.getReference().child("Chats").child(sender_Room).updateChildren(lastmsgobj);
                                        database.getReference().child("Chats").child(receiver_Room).updateChildren(lastmsgobj);

                                        database.getReference().child("Chats").child(sender_Room).child("Message").child(randomkey).setValue(model)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        database.getReference().child("Chats").child(receiver_Room).child("Message").child(randomkey).setValue(model)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {

                                                                    }
                                                                });

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {

                                            }
                                        });


                                    }
                                });
                            }

                        }
                    });


                }
            }
        }
        else
        {
            Toast.makeText(this, "Something went worng", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("online");

    }
    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("offline");
    }
    void sendnotification(String name,String message,String token)
    {
        try
            {
                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "https://fcm.googleapis.com/fcm/send";
                JSONObject data = new JSONObject();
                data.put("title",name);
                data.put("body",message);
                JSONObject notifiationdata = new JSONObject();
                notifiationdata.put("notifiation",data);
                notifiationdata.put("to",token);
                JsonObjectRequest request = new JsonObjectRequest(url, notifiationdata, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // Toast.makeText(chatdetailedActivity.this, "Success", Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(chatdetailedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }
                })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String,String> map = new HashMap<>();
                        String key = "Key=AAAALY5SFa0:APA91bG_w1S-X_GiT0F5Q5lr4M0tuRlnjGlj8lw8iNZ4Qzu3Q6pTEnvN9WrVc4B1c28m1rPvdoFgK0On3IdNdGkAw7bnlSTTsYFeNl2v2OcytQDVaiejDFALo0NeC7h713iPZ9lbQ4-F";
                        map.put("Authorization",key);
                        map.put("Content_Type","application/json");

                        return map;
                    }
                };
                queue.add(request);
            }
            catch(Exception e)
            {

            }


    }
}