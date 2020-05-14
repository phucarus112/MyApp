package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myapp.phucarussocialapp.Activity.Adapter.WallAdapter;
import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.Activity.Object.Post;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import static com.myapp.phucarussocialapp.Activity.Fragment.HomeFragment.auth;
import static com.myapp.phucarussocialapp.Activity.Fragment.HomeFragment.postAdapter;

public class UserActivity extends AppCompatActivity {

    ImageView avt,back,op,h_avt;
    TextView name,h_name;
    ListView statusList;
    WallAdapter wallAdapter;
    ArrayList<Post> postArrayList;
    Post wall;
    String id;
    TextView none;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Auth user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        AnhXa();
        SetUpValue();
        LoadNf();
    }

    private void LoadNf() {
        FirebaseDatabase.getInstance().getReference().child("post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postArrayList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Post post = ds.getValue(Post.class);
                    if(post.getUid().equals(id) == true){
                        postArrayList.add(post);
                        wallAdapter.notifyDataSetChanged();
                    }
                    if (postArrayList.size() >= 2) {
                        if (Long.parseLong(postArrayList.get(0).getTime()) < Long.parseLong(postArrayList.get(1).getTime())) {
                            Collections.reverse(postArrayList);
                            postAdapter.notifyDataSetChanged();
                        }
                        none.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void SetUpValue() {

        final Intent intent = getIntent();
        wall = (Post) intent.getSerializableExtra("wall_obj");
        if(wall != null){
            Picasso.get().load(wall.getAvt()).into(avt);
            name.setText(wall.getName());
            id = wall.getUid();
        }else{
            id = intent.getStringExtra("user_uid");
            Picasso.get().load(intent.getStringExtra("user_avt")).into(avt);
            name.setText(intent.getStringExtra("user_name"));
        }
        findUser(id);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        op.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.RIGHT);
                navigationView = (NavigationView)drawerLayout.findViewById(R.id.nv);
                h_avt = (ImageView)navigationView.findViewById(R.id.h_avt);
                h_name =(TextView)navigationView.findViewById(R.id.h_name);
                if(wall != null){
                    Picasso.get().load(wall.getAvt()).into(h_avt);
                    h_name.setText(wall.getName());
                }else{
                    Picasso.get().load(intent.getStringExtra("user_avt")).into(h_avt);
                    h_name.setText(intent.getStringExtra("user_name"));
                }
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nv_watch:
                               showDiag(h_avt);
                                break;
                            case R.id.nv_chat:
                                Intent intent = new Intent(UserActivity.this, ChatActivity.class);
                                intent.putExtra("receive",user);
                                startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });
            }
        });
    }

    private void findUser(String id) {
        Query query = FirebaseDatabase.getInstance().getReference().child("user").orderByChild("uid").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Auth temp = ds.getValue(Auth.class);
                    user = new Auth(temp.getPhone(),temp.getEmail(),temp.getName(),temp.getDateOfBirth(),temp.getMale_female()
                            ,temp.getAvatar(),temp.getUid(),temp.getOnlineStatus());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showDiag(final ImageView edit) {
        final View dialogView = View.inflate(UserActivity.this,R.layout.my_info_dialog,null);
        final Dialog dialog = new Dialog(UserActivity.this,R.style.MyFirstDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        ImageView img = (ImageView)dialog.findViewById(R.id.close);
        img.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                revealShow(dialogView,false,dialog,edit);
            }
        });
        TextView ten = (TextView)dialog.findViewById(R.id.my_profile_name);
        TextView email = (TextView)dialog.findViewById(R.id.my_profile_email);
        TextView sdt = (TextView)dialog.findViewById(R.id.my_profile_sdt);
        TextView gt = (TextView)dialog.findViewById(R.id.my_profile_gt);
        TextView dob = (TextView)dialog.findViewById(R.id.my_profile_dob);
        ten.setText("Nickname: "+ user.getName());
        email.setText("Email: "+ user.getEmail());
        String phone = user.getPhone();
        for(int i=0;i<phone.length();i++)phone = phone.replace(phone.charAt(i),'*');
        sdt.setText("Số điện thoại: "+phone);
        dob.setText("Ngày tháng năm sinh: "+ user.getDateOfBirth());
        if(user.getMale_female().equals("male") == true){
            gt.setText("Giới tính: Nam");
        }else gt.setText("Giới tính: Nữ");

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onShow(DialogInterface dialog) {
                revealShow(dialogView,true,null,edit);
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_BACK){
                    revealShow(dialogView,false, dialog,edit);
                    return true;
                }
                return false;
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void revealShow(View dialogView, boolean b, final Dialog dialog, ImageView edit) {
        final View v = dialogView.findViewById(R.id.special_dialog);
        int w = v.getWidth();
        int h = v.getHeight();
        int endRadius = (int) Math.hypot(w,h);
        int cx = (int) (edit.getX()+(edit.getWidth()/2));
        int cy = (int) (edit.getY())+ edit.getHeight() + 56;

        if(b){
            Animator animator = ViewAnimationUtils.createCircularReveal(v,cx,cy,0,endRadius);
            v.setVisibility(View.VISIBLE);
            animator.setDuration(700);
            animator.start();
        }else {
            Animator anim = ViewAnimationUtils.createCircularReveal(v,cx,cy,endRadius,0);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    dialog.dismiss();
                    v.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onAnimationCancel(Animator animation) {
                }
                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            anim.setDuration(700);
            anim.start();
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(Gravity.RIGHT)){
            drawerLayout.closeDrawer(Gravity.RIGHT);
        }else super.onBackPressed();
    }

    private void AnhXa() {
        avt = (ImageView)findViewById(R.id.wall_avatar);
        back = (ImageView)findViewById(R.id.wall_back);
        name = (TextView)findViewById(R.id.wall_fullname);
        statusList = (ListView)findViewById(R.id.wall_lv);
        postArrayList = new ArrayList<>();
        wallAdapter = new WallAdapter(UserActivity.this,postArrayList,R.layout.row_post_no_img,R.layout.row_post);
        statusList.setAdapter(wallAdapter);
        none = (TextView)findViewById(R.id.user_none);
        drawerLayout=  (DrawerLayout) findViewById(R.id.dl);
        op = (ImageView)findViewById(R.id.open_nv);
    }
}
