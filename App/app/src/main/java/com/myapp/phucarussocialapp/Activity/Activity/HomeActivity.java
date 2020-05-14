package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.myapp.phucarussocialapp.Activity.Fragment.ChatFragment;
import com.myapp.phucarussocialapp.Activity.Fragment.HomeFragment;
import com.myapp.phucarussocialapp.Activity.Fragment.NotificationFragment;
import com.myapp.phucarussocialapp.Activity.Fragment.ProfileFragment;
import com.myapp.phucarussocialapp.Activity.Notification.Token;
import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.Activity.Object.ChatInfo;
import com.myapp.phucarussocialapp.Activity.Object.Message;
import com.myapp.phucarussocialapp.R;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    public static ArrayList<Auth> authArrayList;
    ChipNavigationBar chipNavigationBar;
    ImageView search;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static FloatingActionButton fab_chat,fab_noti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setUpNavigation();
        authArrayList = new ArrayList<>();
        search = (ImageView)findViewById(R.id.wall_search);
        loadUserList();
        //updateToken
        checkUserStatus();
        updateToken(FirebaseInstanceId.getInstance().getToken());
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,SearchActivity.class));
            }
        });
    }

    private void setUpFab() {
        fab_chat = (FloatingActionButton) findViewById(R.id.fab_tn);
        fab_noti = (FloatingActionButton)findViewById(R.id.fab_noti);
        fab_chat.setVisibility(View.INVISIBLE);
        fab_noti.setVisibility(View.INVISIBLE);

        FirebaseDatabase.getInstance().getReference().child("chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ChatInfo chat = ds.getValue(ChatInfo.class);
                    if(chat.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == true){
                        if(chat.getIsSeen().equals("Đã gửi") == true){
                            fab_chat.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        FirebaseDatabase.getInstance().getReference().child("notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            Message message = ds.getValue(Message.class);
                            if(message.getIsSeen().equals("sent") == true){
                                fab_noti.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        fab_chat.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                FragmentTransaction chatTransaction = getFragmentManager().beginTransaction();
                chatTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                ChatFragment chatFragment = new ChatFragment();
                chatTransaction.replace(R.id.frameLayout,chatFragment);
                chatTransaction.commit();
                chipNavigationBar.setItemSelected(R.id.bottomnavigation_chat,true);
            }
        });
        fab_noti.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                FragmentTransaction notiTransaction = getFragmentManager().beginTransaction();
                notiTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                NotificationFragment notiFragment = new NotificationFragment();
                notiTransaction.replace(R.id.frameLayout,notiFragment);
                notiTransaction.commit();
                chipNavigationBar.setItemSelected(R.id.bottomnavigation_notification,true);

            }
        });
    }

    private void loadUserList() {
        FirebaseDatabase.getInstance().getReference().child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                authArrayList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Auth user = ds.getValue(Auth.class);
                    if(user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == false)
                        authArrayList.add(user);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onResume() {
        checkUserStatus();
        handleDataNotification();
        setUpFab();
        super.onResume();
    }

    @SuppressLint("ResourceType")
    private void handleDataNotification() {
        sharedPreferences = getSharedPreferences("notificationSP",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String temp = "";
        temp  = sharedPreferences.getString("typeNoti","");
        if(temp.equals("") == false){
            if(temp.equals("1") == true){ //chat
                editor.putString("typeNoti","");
                editor.commit();
                String data = sharedPreferences.getString("data","");
                Query query = FirebaseDatabase.getInstance().getReference().child("user").orderByChild("uid").equalTo(data);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Auth auth = ds.getValue(Auth.class);
                            Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                            intent.putExtra("receive", auth);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }else{ //like,cmt
                editor.putString("typeNoti","");
                editor.commit();
                Intent intent = new Intent(HomeActivity.this,DetailActivity.class);
                intent.putExtra("data",sharedPreferences.getString("data",""));
                startActivity(intent);
            }
        }
    }

    private void checkUserStatus() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!= null){
            SharedPreferences sharedPreferences = getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Current_USERID",firebaseUser.getUid());
            editor.commit();
        }
    }

    public void setUpActionBar(String str) {
        ActionBar actionBar = getSupportActionBar();
        Drawable drawable = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = getDrawable(R.drawable.bg1);
        }
        actionBar.setBackgroundDrawable(drawable);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(str);
    }

    @SuppressLint("ResourceType")
    private void setUpNavigation() {

        chipNavigationBar = (ChipNavigationBar)findViewById(R.id.cnv);
        chipNavigationBar.setItemSelected(R.id.bottomnavigation_home,true);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        transaction.replace(R.id.frameLayout,homeFragment);
        transaction.commit();
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i){
                    case R.id.bottomnavigation_home:
                        FragmentTransaction homeTransaction = getFragmentManager().beginTransaction();
                        homeTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                        HomeFragment homeFragment = new HomeFragment();
                        homeTransaction.replace(R.id.frameLayout,homeFragment);
                        homeTransaction.commit();
                        break;
                    case R.id.bottomnavigation_chat:
                        FragmentTransaction chatTransaction = getFragmentManager().beginTransaction();
                        chatTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                        ChatFragment chatFragment = new ChatFragment();
                        chatTransaction.replace(R.id.frameLayout,chatFragment);
                        chatTransaction.commit();
                        break;
                    case R.id.bottomnavigation_notification:
                        FragmentTransaction notiTransaction = getFragmentManager().beginTransaction();
                        notiTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                        NotificationFragment notiFragment = new NotificationFragment();
                        notiTransaction.replace(R.id.frameLayout,notiFragment);
                        notiTransaction.commit();
                        break;
                    case R.id.bottomnavigation_profile:
                        FragmentTransaction profileTransaction = getFragmentManager().beginTransaction();
                        profileTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                        ProfileFragment profileFragment = new ProfileFragment();
                        profileTransaction.replace(R.id.frameLayout,profileFragment);
                        profileTransaction.commit();
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("token");
        Token mtoken = new Token(token);
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(mtoken);
    }
}
