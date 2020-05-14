package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myapp.phucarussocialapp.Activity.Adapter.PlayistAdapter;
import com.myapp.phucarussocialapp.Activity.Adapter.VideoAdapter;
import com.myapp.phucarussocialapp.Activity.Adapter.WhoLikePostAdapter;
import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.Activity.Object.Post;
import com.myapp.phucarussocialapp.Activity.Object.Video;
import com.myapp.phucarussocialapp.Activity.Object.WhoLikePost;
import com.myapp.phucarussocialapp.R;

import java.util.ArrayList;
import java.util.Collections;

public class PlayistActivity extends AppCompatActivity {

    public static final String API_KEY = "AIzaSyCnpkNEzFkP7uhutlQH5gT5P1dhpr2Jpx8";
    GridView lv;
    public static PlayistAdapter playistAdapter;
    ArrayList<String> playistList;
    TextView none;
    ImageView iv_none,back,add;
    String choose_playist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playist);
        showDialogUsage();
        AnhXa();
        LoadPlayist();
       SetUpValue();
    }

    private void showDialogUsage() {
        final Dialog dialog = new Dialog(PlayistActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.usage_dialog);
        dialog.show();
        Button ok = (Button)dialog.findViewById(R.id.ok_btn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void SetUpValue() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                choose_playist = playistList.get(position);
                showDiag(view);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatePlayist();
            }
        });
    }

    private void AnhXa() {
        lv = (GridView)findViewById(R.id.lv_playist);
        playistList = new ArrayList<>();
        playistAdapter = new PlayistAdapter(PlayistActivity.this,playistList,R.layout.row_playist);
        lv.setAdapter(playistAdapter);
        none = (TextView)findViewById(R.id.tv_playist_none);
        iv_none = (ImageView)findViewById(R.id.iv_playist_img);
        back = (ImageView)findViewById(R.id.playist_back);
        add = (ImageView)findViewById(R.id.add_playist);
    }

    private boolean isExisted(String str){
        for(int i=0;i<playistList.size();i++){
            if(str.toLowerCase().equals(playistList.get(i).toLowerCase()) == true){
                return true;
            }
        }
        return false;
    }

    private void CreatePlayist() {
        final Dialog dialog = new Dialog(PlayistActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.create_playist_dialog);
        dialog.show();
        Button btn  =(Button) dialog.findViewById(R.id.btn_create_playist);
        final EditText et =  (EditText)dialog.findViewById(R.id.et_create_playist);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et.getText().toString().trim().equals("") == true){
                    Toast.makeText(PlayistActivity.this, "Nội dung trống", Toast.LENGTH_SHORT).show();
                }else if(isExisted(et.getText().toString().trim())) {
                    Toast.makeText(PlayistActivity.this, "Playist đã tồn tại", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(PlayistActivity.this,AddVideoActivity.class);
                    intent.putExtra("name",et.getText().toString().trim());
                    startActivity(intent);
                    dialog.dismiss();
                }
            }
        });
    }

    private void LoadPlayist() {
        FirebaseDatabase.getInstance().getReference().child("playist").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        playistList.clear();
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            String str = (String) ds.getValue();
                            playistList.add(str);
                            playistAdapter.notifyDataSetChanged();
                        }
                        if(playistList.size()>0){
                            none.setVisibility(View.INVISIBLE);
                            iv_none.setVisibility(View.INVISIBLE);
                        }else{
                            none.setVisibility(View.VISIBLE);
                            iv_none.setVisibility(View.VISIBLE);
                            playistAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void showDiag(final View v) {

        final View dialogView = View.inflate(PlayistActivity.this,R.layout.list_video_dialog,null);
        final Dialog dialog = new Dialog(PlayistActivity.this,R.style.MyFirstDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        ImageView img = (ImageView)dialog.findViewById(R.id.uhm);
        img.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                revealShow(dialogView,false,dialog,v);
            }
        });

        final ArrayList<Video> videoArrayList = new ArrayList<>();
        ListView listView = (ListView)dialog.findViewById(R.id.lv_video);
        final VideoAdapter videoAdapter = new VideoAdapter(PlayistActivity.this,videoArrayList,R.layout.row_video);
        listView.setAdapter(videoAdapter);
        loadListVideo(videoArrayList,videoAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PlayistActivity.this,VideoActivity.class);
                intent.putExtra("idVid",videoArrayList.get(position).getId());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(PlayistActivity.this);
                builder.setMessage("Bạn có chắc muốn video khỏi playist không?")
                        .setPositiveButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setNegativeButton("Có", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Query query = FirebaseDatabase.getInstance().getReference().child("video")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .orderByChild("time").equalTo(videoArrayList.get(position).getTime());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue();
                                    Toast.makeText(PlayistActivity.this, "Xoá thành công", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }).create().show();
                return false;
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onShow(DialogInterface dialog) {
                revealShow(dialogView,true,null,v);
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_BACK){
                    revealShow(dialogView,false, dialog,v);
                    return true;
                }
                return false;
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void loadListVideo(final ArrayList<Video> videoArrayList, final VideoAdapter videoAdapter) {
        FirebaseDatabase.getInstance().getReference().child("video").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                videoArrayList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Video video = ds.getValue(Video.class);
                    if(video.getPlayist().equals(choose_playist) == true) videoArrayList.add(video);
                    videoAdapter.notifyDataSetChanged();
                }
                if(videoArrayList.size()>0){
                    Collections.reverse(videoArrayList);
                    videoAdapter.notifyDataSetChanged();
                }else{
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void revealShow(View dialogView, boolean b, final Dialog dialog, View view ){
        final View v = dialogView.findViewById(R.id.unique_dialog);
        int w = v.getWidth();
        int h = v.getHeight();
        int endRadius = (int) Math.hypot(w,h);
        int cx = (int) (view.getX()+(view.getWidth()/2));
        int cy = (int) (view.getY())+ view.getHeight() + 56;

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
}
