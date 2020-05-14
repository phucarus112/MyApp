package com.myapp.phucarussocialapp.Activity.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myapp.phucarussocialapp.Activity.Activity.ChatActivity;
import com.myapp.phucarussocialapp.Activity.Activity.HomeActivity;
import com.myapp.phucarussocialapp.Activity.Activity.SplashActivity;
import com.myapp.phucarussocialapp.Activity.Adapter.RecentlyChatAdapter;
import com.myapp.phucarussocialapp.Activity.Adapter.UserListAdapter;
import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.Activity.Object.RecentlyChat;
import com.myapp.phucarussocialapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.myapp.phucarussocialapp.Activity.Fragment.HomeFragment.postAdapter;

public class ChatFragment extends Fragment {

    ListView listView;
    ArrayList<RecentlyChat>recentlyChatArrayList;
    public static RecentlyChatAdapter recentlyChatAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chat_fragment, container, false);
        AnhXa(v);
        LoadList();
        SetUpValue();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        recentlyChatArrayList.clear();
        LoadList();
    }

    private void LoadList() {
        FirebaseDatabase.getInstance().getReference().child("recently").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recentlyChatArrayList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    RecentlyChat recentlyChat = ds.getValue(RecentlyChat.class);
                    recentlyChatArrayList.add(recentlyChat);
                    recentlyChatAdapter.notifyDataSetChanged();
                }
                if (recentlyChatArrayList.size() >= 2) {
                    RecentlyChat[] recentlyChats = new RecentlyChat[recentlyChatArrayList.size()];
                    for(int i=0;i<recentlyChats.length;i++){
                        recentlyChats[i] = recentlyChatArrayList.get(i);
                    }
                    for(int i=0;i<recentlyChats.length-1;i++){
                        for(int j=i+1;j<recentlyChats.length;j++){
                            if(Long.parseLong(recentlyChats[i].getTime()) < Long.parseLong(recentlyChats[j].getTime())){
                                RecentlyChat temp = recentlyChats[i];
                                recentlyChats[i] = recentlyChats[j];
                                recentlyChats[j] = temp;
                            }
                        }
                    }
                    recentlyChatArrayList.clear();
                    for(int i=0;i<recentlyChats.length;i++){
                        recentlyChatArrayList.add(recentlyChats[i]);
                    }
                    recentlyChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void SetUpValue() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Query query = FirebaseDatabase.getInstance().getReference().child("user").orderByChild("uid")
                        .equalTo(recentlyChatArrayList.get(position).getUid());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            Auth auth = ds.getValue(Auth.class);
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("receive",auth);
                            getActivity().startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void AnhXa(View v) {
        listView = (ListView)v.findViewById(R.id.lv_recently_chat);
        recentlyChatArrayList = new ArrayList<>();
        recentlyChatAdapter = new RecentlyChatAdapter(getActivity(),recentlyChatArrayList,R.layout.row_chat);
        listView.setAdapter(recentlyChatAdapter);
    }
}
