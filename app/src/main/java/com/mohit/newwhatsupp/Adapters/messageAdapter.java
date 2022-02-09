package com.mohit.newwhatsupp.Adapters;

import android.annotation.SuppressLint;
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
import com.google.firebase.database.FirebaseDatabase;
import com.mohit.newwhatsupp.Models.messgaeModel;
import com.mohit.newwhatsupp.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class messageAdapter extends RecyclerView.Adapter{
    ArrayList<messgaeModel> msgmodel;
    Context context;
    String sender_Room;
    String receiver_Room;
    final int Item_sent =1;
    final int Item_receive =2;
    String senderuid;
    String receiveruid;

    public messageAdapter(ArrayList<messgaeModel> msgmodel, Context context, String sender_Room, String receiver_Room) {
        this.msgmodel = msgmodel;
        this.context = context;
        this.sender_Room = sender_Room;
        this.receiver_Room = receiver_Room;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(viewType==Item_sent)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
            return  new sendervieholder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout,parent,false);
            return  new recievervieholder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        messgaeModel model = msgmodel.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(model.getSenderid()))
        {
            return Item_sent;
        }
        else {
            return Item_receive;
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {


        messgaeModel message= msgmodel.get(position);
        String senderId = FirebaseAuth.getInstance().getUid();
        String receiver = message.getSenderid();
         sender_Room = senderId +receiver;
         receiver_Room = receiver +senderId;

        int reaction [] = new int[]{
                R.drawable.smile,
                R.drawable.sad,
                R.drawable.love,
                R.drawable.angryy

        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reaction)
                .build();
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(holder.getClass()==sendervieholder.class) {
                sendervieholder viewholder = (sendervieholder) holder;
                viewholder.senderfeeling.setImageResource(reaction[pos]);

            }
            else
            {
                recievervieholder viewholder = (recievervieholder) holder;
                viewholder.receiverfeeling.setImageResource(reaction[pos]);

            }
            message.setFeeling(pos);
//
            FirebaseDatabase.getInstance().getReference().child("Chats").child(sender_Room).child("Message").child(message.getMessageid()).setValue(message);
            FirebaseDatabase.getInstance().getReference().child("Chats").child(receiver_Room).child("Message").child(message.getMessageid()).setValue(message);
            return true; // true is closing popup, false is requesting a new selection

        });

        if(holder.getClass()==sendervieholder.class)
        {
            sendervieholder viewholder = (sendervieholder)holder;
            viewholder.sendermsg.setText(message.getMessage());
            if(message.getMessage().equals("Photo"))
            {
                viewholder.senderimage.setVisibility(View.VISIBLE);
                viewholder.sendermsg.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageurl()).placeholder(R.drawable.placeholder).into(viewholder.senderimage);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm ");
            viewholder.sendermsgtime.setText(dateFormat.format(new Date(message.getTimestamp())));

            if(message.getFeeling()>=0)
            {
                message.setFeeling(reaction[(int) message.getFeeling()]);
                viewholder.senderfeeling.setVisibility(View.VISIBLE);
            }
            else
            {
                viewholder.senderfeeling.setVisibility(View.GONE);
            }


            viewholder.sendermsg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return true;
                }
            });

        }
        else
        {

                recievervieholder viewholder = (recievervieholder)holder;
                viewholder.recievermsg.setText(message.getMessage());
            if(message.getMessage().equals("Photo"))
            {
                viewholder.receiverimage.setVisibility(View.VISIBLE);
                viewholder.receiverimage.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageurl()).placeholder(R.drawable.placeholder).into(viewholder.receiverimage);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm ");
            viewholder.receivermsgtime.setText(dateFormat.format(new Date(message.getTimestamp())));
            if(message.getFeeling()>=1)
            {
                message.setFeeling(reaction[(int) message.getFeeling()]);
                viewholder.receiverfeeling.setVisibility(View.VISIBLE);
            }
            else
            {
                viewholder.receiverfeeling.setVisibility(View.GONE);
            }
            viewholder.recievermsg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return true;
                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return msgmodel.size();
    }

    public class sendervieholder extends RecyclerView.ViewHolder {
        TextView sendermsg,sendermsgtime;
        ImageView senderfeeling,senderimage;
        public sendervieholder(@NonNull @NotNull View itemView) {
            super(itemView);
            senderfeeling = itemView.findViewById(R.id.senderfeeling);
            senderimage = itemView.findViewById(R.id.senderimage);
            sendermsg = itemView.findViewById(R.id.sendermsg);
            sendermsgtime = itemView.findViewById(R.id.sendertime);
        }
    }

    public class recievervieholder extends RecyclerView.ViewHolder {
        TextView recievermsg,receivermsgtime;
        ImageView receiverfeeling,receiverimage;
        public recievervieholder(@NonNull @NotNull View itemView) {
            super(itemView);
            receiverfeeling = itemView.findViewById(R.id.receiversfeeling);
            recievermsg = itemView.findViewById(R.id.receivermsg);
            receiverimage = itemView.findViewById(R.id.receiverimage);
            receivermsgtime = itemView.findViewById(R.id.receivertime);
        }
    }
}
