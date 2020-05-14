package com.myapp.phucarussocialapp.Activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.phucarussocialapp.Activity.Fragment.ChatFragment;
import com.myapp.phucarussocialapp.Activity.Notification.Token;
import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.Activity.Object.ChatInfo;
import com.myapp.phucarussocialapp.Activity.Object.RecentlyChat;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecentlyChatAdapter extends BaseAdapter {

    Context context;
    ArrayList<RecentlyChat> recentlyChatAdapterArrayList;
    int layout;

    public RecentlyChatAdapter(Context context, ArrayList<RecentlyChat> recentlyChatAdapterArrayList, int layout) {
        this.context = context;
        this.recentlyChatAdapterArrayList = recentlyChatAdapterArrayList;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return recentlyChatAdapterArrayList.size();
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

        TextView ten = (TextView) convertView.findViewById(R.id.talk_name);
        ImageView avt = (ImageView) convertView.findViewById(R.id.talk_avt);
        TextView message = (TextView)convertView.findViewById(R.id.talk_message);
        TextView count = (TextView)convertView.findViewById(R.id.talk_num);

        RecentlyChat recentlyChat = recentlyChatAdapterArrayList.get(position);
        Picasso.get().load(recentlyChat.getAvt()).into(avt);
        ten.setText(recentlyChat.getName());
        if(recentlyChat.getType().equals("text") == true)
            message.setText(recentlyChat.getMessage());
        else message.setText("Ảnh [File]");
        count.setText("0");
        calculateCount(count,recentlyChat);

        return convertView;
    }

    private void calculateCount(final TextView count, final RecentlyChat recentlyChat) {
        FirebaseDatabase.getInstance().getReference().child("chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ChatInfo chatInfo = ds.getValue(ChatInfo.class);
                    if(chatInfo.getSender().equals(recentlyChat.getUid()) == true
                            && chatInfo.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == true){
                        if(chatInfo.getIsSeen().equals("Đã xem") == false){
                            int num  = Integer.parseInt(count.getText().toString().trim());
                            count.setText(String.valueOf(++num));
                        }
                    }
                    if(count.getText().toString().trim().equals("0") == true){
                        count.setVisibility(View.INVISIBLE);

                    }
                    else{
                        count.setVisibility(View.VISIBLE);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
