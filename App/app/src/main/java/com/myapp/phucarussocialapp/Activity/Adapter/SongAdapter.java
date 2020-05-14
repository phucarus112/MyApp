package com.myapp.phucarussocialapp.Activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.Activity.Object.Song;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SongAdapter extends BaseAdapter {
    Context context;
    ArrayList<Song> songArrayList;
    int layout;

    public SongAdapter(Context context, ArrayList<Song> songArrayList, int layout) {
        this.context = context;
        this.songArrayList = songArrayList;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return songArrayList.size();
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

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(layout,null);

        TextView ten = (TextView) convertView.findViewById(R.id.tv_name_music);
        TextView casi = (TextView) convertView.findViewById(R.id.tv_singer);

        Song song = songArrayList.get(position);
        ten.setText(song.getName());
        casi.setText(song.getSinger());
        return convertView;
    }
}
