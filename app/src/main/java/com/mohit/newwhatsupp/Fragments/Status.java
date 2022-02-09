package com.mohit.newwhatsupp.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohit.newwhatsupp.Activities.profileactivity;
import com.mohit.newwhatsupp.Adapters.topStatusAdapter;
import com.mohit.newwhatsupp.Models.multistatus;
import com.mohit.newwhatsupp.Models.statusmodel;
import com.mohit.newwhatsupp.Models.userInformation;
import com.mohit.newwhatsupp.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Status extends Fragment {
    RecyclerView recyclerView;
    ImageView mystatusimage;
    TextView mystattus,mystatustime;
    ArrayList<statusmodel> list;
    topStatusAdapter adapter;

    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    ProgressDialog dialog;
    userInformation user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_status, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        mystatusimage = view.findViewById(R.id.mystatusimage);
        mystattus = view.findViewById(R.id.mystatus);
        mystatustime = view.findViewById(R.id.mystatustime);
        list = new ArrayList<>();
        adapter = new topStatusAdapter(list,getContext());
        recyclerView.setAdapter(adapter);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.setTitle("Uploading Image...");
        database.getReference().child("userinfo").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                user = snapshot.getValue(userInformation.class);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        mystatusimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,30);

            }
        });
        database.getReference().child("Stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                if(snapshot.exists())
                {
                    for (DataSnapshot snapshot1 :snapshot.getChildren())
                    {
                        statusmodel status = new statusmodel();
                        status.setStatususerName(snapshot1.child("name").getValue(String.class));
                        status.setStatuserimage(snapshot1.child("profileImage").getValue(String.class));
                        status.setStatuslastupdatetime(snapshot1.child("lastUpdated").getValue(Long.class));

                        ArrayList<multistatus> statuses = new ArrayList<>();
                        for(DataSnapshot multistatuses :snapshot1.child("statuses").getChildren())
                        {
                            multistatus samplestatus = multistatuses.getValue(multistatus.class);
                            statuses.add(samplestatus);

                        }
                        status.setStatuses(statuses);
                        list.add(status);
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });



        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(data != null)
        {
            if(data.getData() !=null)
            {
                dialog.show();
                mystatusimage.setImageURI(data.getData());

                    Date date = new Date();
                    StorageReference reference = storage.getReference().child("Status").child(date.getTime() +"");

                    reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful())
                            {

                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageurl = uri.toString();
                                        statusmodel model = new statusmodel();
                                        model.setStatususerName(user.getName());
                                        model.setStatuserimage(user.getPhoto());
                                        model.setStatuslastupdatetime(date.getTime());
                                        HashMap<String ,Object> obj = new HashMap<>();
                                        obj.put("name",model.getStatususerName());
                                        obj.put("profileImage", model.getStatuserimage());
                                        obj.put("lastUpdated",model.getStatuslastupdatetime());
                                        multistatus  multi = new multistatus(imageurl,model.getStatuslastupdatetime());

                                        database.getReference().child("Stories").child(FirebaseAuth.getInstance().getUid()).updateChildren(obj);
                                        database.getReference().child("Stories").child(FirebaseAuth.getInstance().getUid())
                                                .child("statuses").push().setValue(multi);

                                        dialog.dismiss();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                            else
                            {
                                dialog.dismiss();
                                Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }

            }
        }


}