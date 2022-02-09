package com.mohit.newwhatsupp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mohit.newwhatsupp.Activities.MainActivity;
import com.mohit.newwhatsupp.Fragments.Status;
import com.mohit.newwhatsupp.Models.multistatus;
import com.mohit.newwhatsupp.Models.statusmodel;
import com.mohit.newwhatsupp.R;
import com.mohit.newwhatsupp.databinding.FragmentStatusBinding;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class topStatusAdapter extends  RecyclerView.Adapter<topStatusAdapter.viewwholder>{
    ArrayList<statusmodel> model;
    Context context;

    public topStatusAdapter(ArrayList<statusmodel> model, Context context) {
        this.model = model;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public viewwholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_status,parent,false);
        return  new viewwholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull topStatusAdapter.viewwholder holder, int position) {
        statusmodel status = model.get(position);
        multistatus laststatus = status.getStatuses().get(status.getStatuses().size()-1);
        Glide.with(context).load(laststatus.getImageurl()).into(holder.circularimage);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm ");
        holder.statususertime.setText(dateFormat.format(new Date(status.getStatuslastupdatetime())));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for(multistatus story: status.getStatuses()){
                    myStories.add(new MyStory(
                            story.getImageurl()

                    ));
                }
                new StoryView.Builder(((MainActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(status.getStatususerName()) // Default is Hidden
                        .setSubtitleText("Damascus") // Default is Hidden
                        .setTitleLogoUrl("some-link") // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
                
            }
        });


    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class viewwholder extends RecyclerView.ViewHolder {
        ImageView circularimage;
        TextView statususername,statususertime;

        public viewwholder(@NonNull @NotNull View itemView) {
            super(itemView);
            circularimage = itemView.findViewById(R.id.stautsimage);
            statususername = itemView.findViewById(R.id.statutuser);
            statususertime = itemView.findViewById(R.id.statustime);
        }
    }
}
