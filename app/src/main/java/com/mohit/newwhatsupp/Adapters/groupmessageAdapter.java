package com.mohit.newwhatsupp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohit.newwhatsupp.Models.messgaeModel;
import com.mohit.newwhatsupp.Models.profileinfo;
import com.mohit.newwhatsupp.Models.userInformation;
import com.mohit.newwhatsupp.R;
import com.mohit.newwhatsupp.databinding.ItemGrpReceiverBinding;
import com.mohit.newwhatsupp.databinding.ItemGrpSenderBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class groupmessageAdapter extends RecyclerView.Adapter{
    // isme 2 viewholder hai isliy kisi ke sath bhi extends nahi karnege
//    step 3
 Context context;
 ArrayList<messgaeModel> messages;

 // step 3 ------------------

    // step 7
 final int Item_sent = 1;
 final int Item_receive = 2;
 // step 7


// step 4
    public groupmessageAdapter(Context context, ArrayList<messgaeModel> messages) {
        this.context = context;
        this.messages = messages;

    }
//    step 4 ----------------------------


// step 5 implement methods
    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // step 8 isme layout ko set karne ke liy
        if(viewType==Item_sent)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_grp_sender,parent,false);
            return  new senderViewholder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_grp_receiver,parent,false);
            return  new receiverViewholder(view);
        }
    }
// step 6 override this method to infalte 2 xml layouts
    @Override
    public int getItemViewType(int position) {
//        model ka object banakr uska refernce lena hai jo hum onbind method me karte hai wohi
        messgaeModel Message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(Message.getSenderid())) {
            return Item_sent;
        }
        else
        {
            return Item_receive;
        }
    }
    // step 6 --------------------------

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        messgaeModel Message = messages.get(position);

//        step 9
    if (holder.getClass()==senderViewholder.class) {
        senderViewholder viewholder = (senderViewholder)holder;
        if(Message.getMessage().equals("Photo"))
        {
            viewholder.grpsenderimage.setVisibility(View.VISIBLE);
            viewholder.grpsendertext.setVisibility(View.GONE);
            Glide.with(context).load(Message.getImageurl()).placeholder(R.drawable.avatar).into(viewholder.grpsenderimage);
        }

        FirebaseDatabase.getInstance().getReference().child("userprofile").child(Message.getSenderid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            userInformation user = snapshot.getValue(userInformation.class);
                            viewholder.grpuser.setText("@" +user.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        viewholder.grpsendertext.setText(Message.getMessage());


    }
    else
    {
        receiverViewholder viewholder = (receiverViewholder) holder;
        if(Message.getMessage().equals("Photo"))
        {
            viewholder.grprecieverimage.setVisibility(View.VISIBLE);
            viewholder.grpreceivertext.setVisibility(View.GONE);
            Glide.with(context).load(Message.getImageurl()).placeholder(R.drawable.avatar).into(viewholder.grprecieverimage);
        }
        FirebaseDatabase.getInstance().getReference().child("userprofile").child(Message.getSenderid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            userInformation user = snapshot.getValue(userInformation.class);
                            viewholder.grpreceiver.setText("@" +user.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
        viewholder.grpreceivertext.setText(Message.getMessage());


    }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }


//     step 1

    public class senderViewholder extends RecyclerView.ViewHolder {
        ImageView grpsenderimage;
        TextView grpsendertext,grpsendertime,grpuser;
        public senderViewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            grpuser = itemView.findViewById(R.id.grpuser);
            grpsendertext=itemView.findViewById(R.id.sendertext);
            grpsenderimage= itemView.findViewById(R.id.imageView);
            grpsendertime = itemView.findViewById(R.id.sendertime);


        }
    }

//     step 2
    public class receiverViewholder extends RecyclerView.ViewHolder {
    ImageView grprecieverimage;
    TextView grpreceivertext,grpreceivertime,grpreceiver;
        public receiverViewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            grpreceiver = itemView.findViewById(R.id.grpreceiver);
            grpreceivertext=itemView.findViewById(R.id.recievertext);
            grprecieverimage= itemView.findViewById(R.id.imageView2);
            grpreceivertime = itemView.findViewById(R.id.receivertime);
        }
    }
}
