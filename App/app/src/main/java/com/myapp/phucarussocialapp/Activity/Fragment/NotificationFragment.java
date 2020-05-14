package com.myapp.phucarussocialapp.Activity.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myapp.phucarussocialapp.Activity.Activity.DetailActivity;
import com.myapp.phucarussocialapp.Activity.Activity.HomeActivity;
import com.myapp.phucarussocialapp.Activity.Activity.SplashActivity;
import com.myapp.phucarussocialapp.Activity.Adapter.MessageAdapter;
import com.myapp.phucarussocialapp.Activity.Object.Message;
import com.myapp.phucarussocialapp.Activity.Object.Post;
import com.myapp.phucarussocialapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class NotificationFragment extends Fragment {

    TextView none;
    ListView lv;
    MessageAdapter adapter;
    ArrayList<Message> messageArrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notification_fragment,container,false);
        AnhXa(v);
        LoadNotificationList();
        setUpValue();
        updateFirebase();
        HomeActivity.fab_noti.setVisibility(View.INVISIBLE);
        return v;
    }

    private void updateFirebase() {
        FirebaseDatabase.getInstance().getReference().child("notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            String key = ds.getKey();
                            Message message = ds.getValue(Message.class);
                            if(message.getIsSeen().equals("sent") == true){
                                HashMap<String,Object> hm = new HashMap<>();
                                hm.put("isSeen","seen");
                                FirebaseDatabase.getInstance().getReference().child("notification")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).updateChildren(hm);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void setUpValue() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("data", messageArrayList.get(position).getTime_post());
                startActivity(intent);
            }
        });
    }

    private void LoadNotificationList() {
        FirebaseDatabase.getInstance().getReference().child("notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageArrayList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Message message = ds.getValue(Message.class);
                    messageArrayList.add(message);
                    adapter.notifyDataSetChanged();
                }
                if(messageArrayList.size()>=2){
                    if(Long.parseLong(messageArrayList.get(0).getTime()) < Long.parseLong(messageArrayList.get(1).getTime())){
                        Collections.reverse(messageArrayList);
                        adapter.notifyDataSetChanged();
                    }
                }
                if(messageArrayList.size() > 0 ){
                    none.setVisibility(View.INVISIBLE);
                }else none.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void AnhXa(View v) {
        none = (TextView)v.findViewById(R.id.noti_none);
        lv = (ListView)v.findViewById(R.id.noti_list);
        messageArrayList = new ArrayList<>();
        adapter = new MessageAdapter(getActivity(),messageArrayList,R.layout.row_notification);
        lv.setAdapter(adapter);
    }
}
