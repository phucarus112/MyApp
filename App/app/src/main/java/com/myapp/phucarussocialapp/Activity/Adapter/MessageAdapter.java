package com.myapp.phucarussocialapp.Activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.phucarussocialapp.Activity.Object.Message;
import com.myapp.phucarussocialapp.Activity.Object.Song;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MessageAdapter extends BaseAdapter {

    Context context;
    ArrayList<Message> songArrayList;
    int layout;

    public MessageAdapter(Context context, ArrayList<Message> songArrayList, int layout) {
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

        ImageView avt = (ImageView)convertView.findViewById(R.id.noti_avt) ;
        TextView content = (TextView) convertView.findViewById(R.id.noti_content);
        TextView time = (TextView) convertView.findViewById(R.id.noti_time);

        Message message = songArrayList.get(position);
        content.setText(message.getContent());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        time.setText(simpleDateFormat.format(Long.parseLong(message.getTime())));
        Picasso.get().load(message.getAvt()).into(avt);
        return convertView;
    }
}

