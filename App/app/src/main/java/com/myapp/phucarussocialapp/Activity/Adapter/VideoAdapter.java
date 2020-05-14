package com.myapp.phucarussocialapp.Activity.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.phucarussocialapp.Activity.Activity.EditPostActivity;
import com.myapp.phucarussocialapp.Activity.Object.Video;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VideoAdapter extends BaseAdapter {

    Context context;
    ArrayList<Video> videoArrayList;
    int layout;

    public VideoAdapter(Context context, ArrayList<Video> videoArrayList, int layout) {
        this.context = context;
        this.videoArrayList = videoArrayList;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return videoArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layout,null);
      TextView title = (TextView) convertView.findViewById(R.id.row_video_name);
        ImageView img = (ImageView) convertView.findViewById(R.id.row_video_img);

        Video video = videoArrayList.get(position);
        title.setText(video.getTitle());
        Picasso.get().load(video.getLinkThumbnail()).into(img);

        return convertView;
    }
}