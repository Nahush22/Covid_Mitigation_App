package com.example.Covid19App;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {

    Context context;
    ArrayList<PlaylistModel> playList = new ArrayList<>();

    public PlaylistAdapter(Context context, ArrayList<PlaylistModel> playList) {
        this.context = context;
        this.playList = playList;
    }

    @NonNull
    @Override
    public PlaylistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.playlist_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.MyViewHolder holder, int position) {

        final PlaylistModel model = playList.get(position);

        if(model.thumbnail != null)
        {
            Glide.with(context)
                    .load(model.thumbnail)
                    .into(holder.img);
        }

        if(model.title!=null && model.publishedAt!= null && model.channelName != null && model.description != null)
        {
            holder.title.setText(model.title);
            holder.date.setText(model.publishedAt);
            holder.channel.setText(model.channelName);

            if(context.equals(PlayListActivity.class))
                holder.desc.setText(model.description);

        }

        holder.playListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(context, context.toString(), Toast.LENGTH_SHORT).show();

                if(context.toString().contains("PlayListActivity"))
                {
                    Intent intent = new Intent(context, VideoListActivity.class);
                    intent.putExtra("Id", model.playListId);
                    context.startActivity(intent);
                }
                else if(context.toString().contains("VideoListActivity"))
                {
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("Id", model.playListId);
                    intent.putExtra("title", model.title);
                    intent.putExtra("date", model.publishedAt);
                    intent.putExtra("channel", model.channelName);
                    intent.putExtra("desc", model.description);
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return playList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout playListLayout;
        ImageView img;
        TextView title, date, channel, desc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            playListLayout = itemView.findViewById(R.id.mainPlayList);
            img = itemView.findViewById(R.id.playListImg);
            title = itemView.findViewById(R.id.playListTitle);
            date = itemView.findViewById(R.id.playListDate);
            channel = itemView.findViewById(R.id.playListChannel);
            desc = itemView.findViewById(R.id.playListDescription);
        }
    }
}
