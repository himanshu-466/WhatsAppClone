package com.mohit.newwhatsupp.Fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.mohit.newwhatsupp.Activities.MainActivity;
import com.mohit.newwhatsupp.Adapters.chatAdapter;
import com.mohit.newwhatsupp.Models.chatmodel;
import com.mohit.newwhatsupp.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class chat extends Fragment {
    RecyclerView chatRecycler;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ArrayList<chatmodel> Chatmodel;
    chatAdapter adapter;
    ImageView background;




    public chat() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_chat, container, false);
        chatRecycler = view.findViewById(R.id.chatRecycler);
        Chatmodel = new ArrayList<>();
        adapter = new chatAdapter(Chatmodel,getContext());
        auth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        background = view.findViewById(R.id.background);

        chatRecycler.setAdapter(adapter);

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.fetchAndActivate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                String backgroundimage = mFirebaseRemoteConfig.getString("backgroundimage");
//                Glide.with(getContext()).load(backgroundimage).into(background);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });



        database.getReference().child("userprofile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Chatmodel.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    chatmodel  model = snapshot1.getValue(chatmodel.class);
                    if(!model.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                        Chatmodel.add(model);
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        return view;
    }
}