package com.myapp.phucarussocialapp.Activity.Fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.myapp.phucarussocialapp.Activity.Activity.HomeActivity;
import com.myapp.phucarussocialapp.Activity.Activity.PostActivity;
import com.myapp.phucarussocialapp.Activity.Adapter.PostAdapter;
import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.Activity.Object.Post;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {

    ImageView my_avt,story;
    TextView cap;
    ListView lv;
    public static ArrayList<Post> postArrayList;
    public static PostAdapter postAdapter;
    public static Auth auth;;
    boolean check = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_fragment, container, false);
        AnhXa(v);
        setUpCurrentUser();

        SetUpValue();
        LoadNf();
        return v;
    }

    private void setUpCurrentUser() {
        Query query = FirebaseDatabase.getInstance().getReference().child("user").orderByChild("email")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    auth.setPhone(ds.child("phone").getValue().toString());
                    auth.setEmail(ds.child("email").getValue().toString());
                    auth.setName(ds.child("name").getValue().toString());
                    auth.setDateOfBirth(ds.child("dateOfBirth").getValue().toString());
                    auth.setMale_female(ds.child("male_female").getValue().toString());
                    auth.setAvatar(ds.child("avatar").getValue().toString());
                    auth.setUid(ds.child("uid").getValue().toString());
                    auth.setOnlineStatus(ds.child("onlineStatus").getValue().toString());
                    Picasso.get().load(auth.getAvatar()).into(my_avt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SetUpValue() {

        cap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                getActivity().startActivity(intent);
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("ResourceType")
            @Override
            public void run() {
                if(check){
                    story.setImageResource(R.drawable.story);
                    check = false;
                }else{
                    story.setImageResource(R.drawable.files);
                    check = true;
                }
                handler.postDelayed(this,1000);
            }
        },1000);
    }

    private void AnhXa(View v) {
        my_avt = (ImageView)v.findViewById(R.id.nf_cap_avt);
        cap = (TextView)v.findViewById(R.id.nf_cap);
       lv = (ListView) v.findViewById(R.id.nf_lv);
        auth = new Auth();
        postArrayList = new ArrayList<>();
        postAdapter  = new PostAdapter(getActivity(),postArrayList,R.layout.row_post_no_img,R.layout.row_post);
        lv.setAdapter(postAdapter);
        story = (ImageView)v.findViewById(R.id.story);
    }

    private void LoadNf() {

        FirebaseDatabase.getInstance().getReference().child("post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postArrayList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Post post = ds.getValue(Post.class);
                    postArrayList.add(post);
                   postAdapter.notifyDataSetChanged();
                }
                if(postArrayList.size()>=2){
                    if(Long.parseLong(postArrayList.get(0).getTime()) < Long.parseLong(postArrayList.get(1).getTime())){
                        Collections.reverse(postArrayList);
                        postAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
