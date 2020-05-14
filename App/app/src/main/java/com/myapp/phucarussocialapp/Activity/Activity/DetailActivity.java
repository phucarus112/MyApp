package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myapp.phucarussocialapp.Activity.Adapter.CommentAdapter;
import com.myapp.phucarussocialapp.Activity.Adapter.CommentAdapter_2;
import com.myapp.phucarussocialapp.Activity.Adapter.WhoLikePostAdapter;
import com.myapp.phucarussocialapp.Activity.Fragment.HomeFragment;
import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.Activity.Object.Comment;
import com.myapp.phucarussocialapp.Activity.Object.Post;
import com.myapp.phucarussocialapp.Activity.Object.WhoLikePost;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {

    Post post;
    ImageView back,img,avt,tym,share,option;
    public static String data;
    TextView name,time,tv_tym,none,caption;
    ArrayList<Comment> commentArrayList;
    CommentAdapter_2 adapter;
    RecyclerView recyclerView;
    EditText input;
    ImageButton send;
    int totalLike;
    public static int totalCmt;
    public static boolean checkEditted= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        AnhXa();
        data = getIntent().getStringExtra("data");
        SetUpValue();
    }

    private void AnhXa() {
        back = (ImageView)findViewById(R.id.the_post_back);
        data = getIntent().getStringExtra("isHaveImg");
        avt = (ImageView)findViewById(R.id.the_post_avt);
        name = (TextView)findViewById(R.id.the_post_ten);
        time = (TextView)findViewById(R.id.the_post_time);
        tv_tym = (TextView)findViewById(R.id.the_post_tv_tym);
        caption = (TextView)findViewById(R.id.the_post_caption);
        tym = (ImageView)findViewById(R.id.the_post_tym);
        img = (ImageView)findViewById(R.id.the_post_img);
        share = (ImageView)findViewById(R.id.the_post_share);
        option = (ImageView)findViewById(R.id.the_post_option);
        recyclerView = (RecyclerView)findViewById(R.id.the_post_rv);
        none = (TextView)findViewById(R.id.deletedPost);
        commentArrayList = new ArrayList<>();
        adapter = new CommentAdapter_2(DetailActivity.this,commentArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        send = (ImageButton)findViewById(R.id.the_post_send);
        input = (EditText)findViewById(R.id.the_post_input);
    }

    private void SetUpValue() {
        Query query = FirebaseDatabase.getInstance().getReference().child("post").orderByChild("time").equalTo(data);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    post = ds.getValue(Post.class);
                    none.setVisibility(View.INVISIBLE);
                    avt.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    time.setVisibility(View.VISIBLE);
                    option.setVisibility(View.VISIBLE);
                    caption.setVisibility(View.VISIBLE);
                    img.setVisibility(View.VISIBLE);
                    tv_tym.setVisibility(View.VISIBLE);
                    tym.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);
                    input.setVisibility(View.VISIBLE);
                    send.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    SetUpEvent();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkEditted){
            if(EditPostActivity.newCaption.equals("") == false){
                caption.setText(EditPostActivity.newCaption);
                Picasso.get().load(EditPostActivity.newImg).into(img);
            }
            SetUpValue();
        }else SetUpValue();

    }

    private void SetUpEvent() {

        Picasso.get().load(post.getAvt()).into(avt);
        name.setText(post.getName());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:ss");
        time.setText(simpleDateFormat.format(Long.parseLong(post.getTime())));
        caption.setText(post.getCaption());
        tv_tym.setText(post.getLike()+" lượt thích");
        totalLike = Integer.parseInt(post.getLike());
        totalCmt = Integer.parseInt(post.getComment());
        if(post.getImg().equals("none")== true)img.setImageResource(0);
        else Picasso.get().load(post.getImg()).into(img);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(DetailActivity.this, option);
                popupMenu.getMenuInflater().inflate(R.menu.option_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.op_edit:
                                checkEditted = true;
                                Intent intent = new Intent(DetailActivity.this, EditPostActivity.class);
                                intent.putExtra("edit_obj", post);
                                startActivity(intent);
                                break;
                            case R.id.op_delete:
                                final AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                                builder.setMessage("Bạn có chắc chắn muốn xoá bài viết này không?")
                                        .setPositiveButton("Không", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setNegativeButton("Có", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deletePost(post.getTime());
                                            }
                                        }).create().show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailActivity.this,ProfileActivity.class));
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.equals("false") == true)
                    shareOnlyText(post.getCaption());
                else{
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) img.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(post.getCaption(),bitmap);
                }
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchDialog(post.getImg());
            }
        });

        loadCommentList(post.getTime());

        loadLike(post.getTime(), tym);

        tym.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("like").child(post.getTime()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String key = dataSnapshot.getKey();
                        if (key.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == true) {
                            if (dataSnapshot.getValue().equals("liked") == true) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), "unliked");
                                FirebaseDatabase.getInstance().getReference().child("like").child(post.getTime()).updateChildren(hashMap);
                                HashMap<String, Object> hashMap1 = new HashMap<>();
                                hashMap1.put("like", String.valueOf(--totalLike));
                                FirebaseDatabase.getInstance().getReference().child("post").child(post.getTime()).updateChildren(hashMap1);
                                tv_tym.setText(totalLike+" lượt thích");
                            } else {
                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put(FirebaseAuth.getInstance().getCurrentUser().getUid(),"liked");
                                FirebaseDatabase.getInstance().getReference().child("like").child(post.getTime()).updateChildren(hashMap);
                                HashMap<String,Object> hashMap1 = new HashMap<>();
                                hashMap1.put("like",String.valueOf(++totalLike));
                                FirebaseDatabase.getInstance().getReference().child("post").child(post.getTime()).updateChildren(hashMap1);
                                tv_tym.setText(totalLike+" lượt thích");
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        tv_tym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiag(tv_tym,post);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().trim().equals("") == true) {
                    Toast.makeText(DetailActivity.this, "Nội dung trống", Toast.LENGTH_SHORT).show();
                } else {
                    Calendar calendar = Calendar.getInstance();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("content", input.getText().toString().trim());
                    hashMap.put("time", String.valueOf(calendar.getTimeInMillis()));
                    hashMap.put("name", HomeFragment.auth.getName());
                    hashMap.put("avt", HomeFragment.auth.getAvatar());
                    hashMap.put("uid", HomeFragment.auth.getUid());
                    FirebaseDatabase.getInstance().getReference().child("comment").child(post.getTime()).push().setValue(hashMap);
                    input.setText("");
                    //update cmt +1
                    HashMap<String, Object> hashMap1 = new HashMap<>();
                    hashMap1.put("comment", String.valueOf(++totalCmt));
                    FirebaseDatabase.getInstance().getReference().child("post").child(post.getTime()).updateChildren(hashMap1);
                }
            }
        });
    }

    private void loadCommentList(String time) {
        FirebaseDatabase.getInstance().getReference().child("comment").child(time).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentArrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Comment comment = ds.getValue(Comment.class);
                    commentArrayList.add(comment);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void deletePost(String time) {
        Query query = FirebaseDatabase.getInstance().getReference().child("post").orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.getRef().removeValue();
                    Toast.makeText(DetailActivity.this, "Xoá thành công bài viết", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        FirebaseDatabase.getInstance().getReference().child("like").child(time).getRef().removeValue();
        FirebaseDatabase.getInstance().getReference().child("comment").child(time).getRef().removeValue();
        finish();
    }

    private void loadLike(String time, final ImageView tym) {
        FirebaseDatabase.getInstance().getReference().child("like").child(time).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String key = ds.getKey();
                    if (key.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == true) {
                        String stt = (String) ds.getValue();
                        if (stt.equals("liked") == true) {
                            tym.setImageResource(R.drawable.heart_full);
                        }else tym.setImageResource(R.drawable.heat);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void watchDialog(final String img) {
        final Dialog dialog= new Dialog(DetailActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.watch_image_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        ImageView close = (ImageView)dialog.findViewById(R.id.w_close);
        ImageView image = (ImageView)dialog.findViewById(R.id.w_img);
        ImageView down = (ImageView)dialog.findViewById(R.id.w_download);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage(img);
                Toast.makeText(DetailActivity.this, "Đã tải xuống hình ảnh thành công", Toast.LENGTH_SHORT).show();
            }
        });
        Picasso.get().load(img).into(image);
    }

    private void downloadImage(String img) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(img));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download");
        request.setDescription("Downloading image...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,""+System.currentTimeMillis());
        DownloadManager  manager  = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    private void shareImageAndText(String caption, Bitmap bitmap) {
        Uri uri = saveImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Chủ đề: ");
        intent.putExtra(intent.EXTRA_TEXT,caption);
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent,"Chia sẻ qua"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File file = new File(getCacheDir(),"images");
        Uri uri = null;
        try{
            file.mkdirs();
            File file1 = new File(file, "shared_image.png");
            FileOutputStream fileOutputStream = new FileOutputStream(file1);
            bitmap.compress(Bitmap.CompressFormat.PNG,90,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            uri = FileProvider.getUriForFile(DetailActivity.this,"com.myapp.phucarussocialapp.fileprovider",file1);
        }catch (Exception e){
            Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return  uri;
    }

    private void shareOnlyText(String caption) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Chủ đề: ");
        intent.putExtra(intent.EXTRA_TEXT,caption);
        startActivity(Intent.createChooser(intent,"Chia sẻ qua"));
    }

    private void showDiag(final TextView ll, Post temp) {

        final View dialogView = View.inflate(DetailActivity.this,R.layout.who_like_post_dialog,null);
        final Dialog dialog = new Dialog(DetailActivity.this,R.style.MyFirstDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        ImageView img = (ImageView)dialog.findViewById(R.id.closeImgDialog);
        img.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                revealShow(dialogView,false,dialog,ll);
            }
        });
        ListView listView = (ListView)dialog.findViewById(R.id.lv_wlp);
        ArrayList<WhoLikePost> whoLikePostArrayList= new ArrayList<>();
        WhoLikePostAdapter whoLikePostAdapter = new WhoLikePostAdapter(DetailActivity.this,whoLikePostArrayList,R.layout.row_who_like_post);
        listView.setAdapter(whoLikePostAdapter);
        loadWhoLikePostOfStatus(whoLikePostArrayList,whoLikePostAdapter,listView,temp.getTime());

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onShow(DialogInterface dialog) {
                revealShow(dialogView,true,null,ll);
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_BACK){
                    revealShow(dialogView,false, dialog,ll);
                    return true;
                }
                return false;
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void loadWhoLikePostOfStatus(final ArrayList<WhoLikePost> whoLikePostArrayList, final WhoLikePostAdapter whoLikePostAdapter, ListView listView, String time) {
        FirebaseDatabase.getInstance().getReference().child("like").child(time).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String value = (String) ds.getValue();
                    String key = ds.getKey();
                    if(value.equals("liked")==true){
                        Query query = FirebaseDatabase.getInstance().getReference().child("user").orderByChild("uid").equalTo(key);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    Auth auth = ds.getValue(Auth.class);
                                    whoLikePostArrayList.add(new WhoLikePost(auth.getAvatar(),auth.getName(),auth.getUid()));
                                    whoLikePostAdapter.notifyDataSetChanged();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void revealShow(View dialogView, boolean b, final Dialog dialog, TextView linearLayout) {
        final View v = dialogView.findViewById(R.id.special_dialog);
        int w = v.getWidth();
        int h = v.getHeight();
        int endRadius = (int) Math.hypot(w,h);
        int cx = (int) (linearLayout.getX()+(linearLayout.getWidth()/2));
        int cy = (int) (linearLayout.getY())+ linearLayout.getHeight() + 56;

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

