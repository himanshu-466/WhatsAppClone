package com.mohit.newwhatsupp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohit.newwhatsupp.Activities.chatdetailedActivity;
import com.mohit.newwhatsupp.Models.chatmodel;
import com.mohit.newwhatsupp.Models.statusmodel;
import com.mohit.newwhatsupp.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class
chatAdapter extends RecyclerView.Adapter<chatAdapter.viewholder> {
    ArrayList<chatmodel> model;
    Context context;

    public chatAdapter(ArrayList<chatmodel> model, Context context) {
        this.model = model;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public chatAdapter.viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chatsample,parent,false);
        return new chatAdapter.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull chatAdapter.viewholder holder, int position) {
        chatmodel modelnot =model.get(position);
        String senderid = FirebaseAuth.getInstance().getUid();
        String sender_Room = senderid + modelnot.getUid();
        FirebaseDatabase.getInstance().getReference().child("Chats")
                .child(sender_Room).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()) {

                    String lastmsg = snapshot.child("lastmsg").getValue(String.class);
                    long time = snapshot.child("lastmsgtime").getValue(Long.class);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm ");
                    holder.chatuserlastmsgtime.setText(dateFormat.format(new Date(time)));
                    holder.chatuserlastmsgtime.setVisibility(View.VISIBLE);
                    holder.chatuserlastmsg.setText(lastmsg);

                }
                else
                {
                    holder.chatuserlastmsg.setText("Tap to chat");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

//        holder.chatuserimage.setImageResource(modelnot.getChatuserimage());
        holder.chatusername.setText(modelnot.getName());
        (Glide.with(context).load(modelnot.getPhoto())).placeholder(R.drawable.placeholder).into(holder.chatuserimage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, chatdetailedActivity.class);
                intent.putExtra("username",modelnot.getName());
                intent.putExtra("userimage",modelnot.getPhoto());
                intent.putExtra("uid",modelnot.getUid());
                intent.putExtra("token",modelnot.getToken());
                context.startActivity(intent);
            }
        });
//        holder.chatuserlastmsg.setText(modelnot.getChatuserlastmsg());
//        holder.chatuserlastmsgtime.setText(modelnot.getChatuserlastmsgtime());


    }

    @Override
    public int getItemCount() {

        return model.size();
    }

    public class  viewholder extends RecyclerView.ViewHolder {
        ImageView chatuserimage;
        TextView chatusername,chatuserlastmsg,chatuserlastmsgtime;
        public viewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            chatuserimage = itemView.findViewById(R.id.chatuserimage);
            chatusername = itemView.findViewById(R.id.ChatuserName);
            chatuserlastmsg = itemView.findViewById(R.id.Chatuserlastmessage);
            chatuserlastmsgtime = itemView.findViewById(R.id.chatlastmsgtime);


        }
    }
}
