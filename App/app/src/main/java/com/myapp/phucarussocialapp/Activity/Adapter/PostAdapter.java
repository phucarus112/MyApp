package com.myapp.phucarussocialapp.Activity.Adapter;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.myapp.phucarussocialapp.Activity.Activity.ChatActivity;
import com.myapp.phucarussocialapp.Activity.Activity.EditPostActivity;
import com.myapp.phucarussocialapp.Activity.Activity.LoginActivity;
import com.myapp.phucarussocialapp.Activity.Activity.ProfileActivity;
import com.myapp.phucarussocialapp.Activity.Activity.SplashActivity;
import com.myapp.phucarussocialapp.Activity.Activity.UserActivity;
import com.myapp.phucarussocialapp.Activity.Fragment.HomeFragment;
import com.myapp.phucarussocialapp.Activity.Notification.Data;
import com.myapp.phucarussocialapp.Activity.Notification.Sender;
import com.myapp.phucarussocialapp.Activity.Notification.Token;
import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.Activity.Object.Comment;
import com.myapp.phucarussocialapp.Activity.Object.Post;
import com.myapp.phucarussocialapp.Activity.Object.WhoLikePost;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PostAdapter extends BaseAdapter {

    private static final int REQUEST_CODE_DOWNLOAD = 100;
    Context context;
    ArrayList<Post> postArrayList;
    int layout1, layout2;

    public PostAdapter(Context context, ArrayList<Post> postArrayList, int layout1, int layout2) {
        this.context = context;
        this.postArrayList = postArrayList;
        this.layout1 = layout1;
        this.layout2 = layout2;
    }

    @Override
    public int getCount() {
        return postArrayList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Post post = postArrayList.get(position);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        if (post.getImg().equals("none") == true) {

            convertView = inflater.inflate(layout1, null);
            ImageView avt = (ImageView) convertView.findViewById(R.id.post_avt_no_img);
            TextView name = (TextView) convertView.findViewById(R.id.post_ten_no_img);
            TextView time = (TextView) convertView.findViewById(R.id.post_time_no_img);
            final ImageView option = (ImageView) convertView.findViewById(R.id.post_option_no_img);
            TextView caption = (TextView) convertView.findViewById(R.id.post_caption_no_img);
            final ImageView tym = (ImageView) convertView.findViewById(R.id.post_tym_no_img);
            ImageView cmt = (ImageView) convertView.findViewById(R.id.post_comment_no_img);
            ImageView share = (ImageView) convertView.findViewById(R.id.post_share_no_img);
            final TextView count_like = (TextView) convertView.findViewById(R.id.post_tv_tym_no_img);
            TextView count_cmt = (TextView) convertView.findViewById(R.id.post_tv_comment_no_img);
            TextView count_share = (TextView) convertView.findViewById(R.id.post_tv_share_no_img);

            Picasso.get().load(post.getAvt()).into(avt);
            name.setText(post.getName());
            long milli = Long.parseLong(post.getTime());
            time.setText(simpleDateFormat.format(milli));
            caption.setText(post.getCaption());
            count_like.setText(post.getLike()+" lượt thích");
            count_cmt.setText("");
            count_share.setText("");

            if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(post.getUid()) == true) {
                option.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(context,option);
                        popupMenu.getMenuInflater().inflate(R.menu.option_menu,popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.op_edit:
                                        Intent intent = new Intent(context, EditPostActivity.class);
                                        intent.putExtra("edit_obj",post);
                                        context.startActivity(intent);
                                        break;
                                    case R.id.op_delete:
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
            } else option.setVisibility(View.INVISIBLE);
            loadLike(post.getTime(),tym);
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
                                    int like = Integer.parseInt(post.getLike());
                                    HashMap<String, Object> hashMap1 = new HashMap<>();
                                    hashMap1.put("like", String.valueOf(--like));
                                    FirebaseDatabase.getInstance().getReference().child("post").child(post.getTime()).updateChildren(hashMap1);
                                } else {
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put(FirebaseAuth.getInstance().getCurrentUser().getUid(),"liked");
                                    FirebaseDatabase.getInstance().getReference().child("like").child(post.getTime()).updateChildren(hashMap);
                                    int like = Integer.parseInt(post.getLike());
                                    HashMap<String,Object> hashMap1 = new HashMap<>();
                                    hashMap1.put("like",String.valueOf(++like));
                                    FirebaseDatabase.getInstance().getReference().child("post").child(post.getTime()).updateChildren(hashMap1);
                                    //
                                    if(post.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == false) //khong phai do minh post
                                    {
                                        HashMap<String,Object> hm = new HashMap<>();
                                        hm.put("time_post",post.getTime());
                                        hm.put("avt",HomeFragment.auth.getAvatar());
                                        hm.put("content",HomeFragment.auth.getName()+" đã thích bài viết của bạn");
                                        hm.put("time",String.valueOf(Calendar.getInstance().getTimeInMillis()));
                                        hm.put("isSeen","sent");
                                        hm.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        FirebaseDatabase.getInstance().getReference().child("notification").child(post.getUid())
                                                .push().setValue(hm);
                                        sendNoti(post.getTime(),post.getUid(),HomeFragment.auth.getName()+" đã thích bài viết của bạn");
                                    }
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

            avt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(post.getUid().equals(HomeFragment.auth.getUid()) == true){
                        context.startActivity(new Intent(context, ProfileActivity.class));
                    }else{
                        Intent intent=  new Intent(context, UserActivity.class);
                        intent.putExtra("wall_obj",postArrayList.get(position));
                        context.startActivity(intent);
                    }
                }
            });

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(post.getUid().equals(HomeFragment.auth.getUid()) == true){
                    }else{
                        Intent intent=  new Intent(context, UserActivity.class);
                        intent.putExtra("wall_obj",postArrayList.get(position));
                        context.startActivity(intent);
                    }
                }
            });

            cmt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(post);
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        shareOnlyText(post.getCaption());
                }
            });
        }
        else {
            convertView = inflater.inflate(layout2, null);
            ImageView avt = (ImageView) convertView.findViewById(R.id.post_avt);
            TextView name = (TextView) convertView.findViewById(R.id.post_ten);
            TextView time = (TextView) convertView.findViewById(R.id.post_time);
            final ImageView option = (ImageView) convertView.findViewById(R.id.post_option);
            final TextView caption = (TextView) convertView.findViewById(R.id.post_caption);
            final ImageView tym = (ImageView) convertView.findViewById(R.id.post_tym);
            ImageView cmt = (ImageView) convertView.findViewById(R.id.post_comment);
            ImageView share = (ImageView) convertView.findViewById(R.id.post_share);
            final ImageView img = (ImageView) convertView.findViewById(R.id.post_img);
            final TextView count_like = (TextView) convertView.findViewById(R.id.post_tv_tym);
            TextView count_cmt = (TextView) convertView.findViewById(R.id.post_tv_comment);
            TextView count_share = (TextView) convertView.findViewById(R.id.post_tv_share);

            Picasso.get().load(post.getAvt()).into(avt);
            name.setText(post.getName());
            long milli = Long.parseLong(post.getTime());
            time.setText(simpleDateFormat.format(milli));
            caption.setText(post.getCaption());
            Picasso.get().load(post.getImg()).into(img);
            count_like.setText(post.getLike()+" lượt thích");
            count_cmt.setText("");
            count_share.setText("");

            loadLike(post.getTime(),tym);
            if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(post.getUid()) == true) {
                option.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(context, option);
                        popupMenu.getMenuInflater().inflate(R.menu.option_menu, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.op_edit:
                                        Intent intent = new Intent(context, EditPostActivity.class);
                                        intent.putExtra("edit_obj", post);
                                        context.startActivity(intent);
                                        break;
                                    case R.id.op_delete:
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
            } else option.setVisibility(View.INVISIBLE);
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
                                    int like = Integer.parseInt(post.getLike());
                                    HashMap<String, Object> hashMap1 = new HashMap<>();
                                    hashMap1.put("like", String.valueOf(--like));
                                    FirebaseDatabase.getInstance().getReference().child("post").child(post.getTime()).updateChildren(hashMap1);
                                } else {
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put(FirebaseAuth.getInstance().getCurrentUser().getUid(),"liked");
                                    FirebaseDatabase.getInstance().getReference().child("like").child(post.getTime()).updateChildren(hashMap);
                                    int like = Integer.parseInt(post.getLike());
                                    HashMap<String,Object> hashMap1 = new HashMap<>();
                                    hashMap1.put("like",String.valueOf(++like));
                                    FirebaseDatabase.getInstance().getReference().child("post").child(post.getTime()).updateChildren(hashMap1);
                                    //
                                    if(post.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == false) //khong phai do minh post
                                    {
                                        HashMap<String,Object> hm = new HashMap<>();
                                        hm.put("time_post",post.getTime());
                                        hm.put("avt",HomeFragment.auth.getAvatar());
                                        hm.put("content",HomeFragment.auth.getName()+" đã thích bài viết của bạn");
                                        hm.put("time",String.valueOf(Calendar.getInstance().getTimeInMillis()));
                                        hm.put("isSeen","sent");
                                        hm.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        FirebaseDatabase.getInstance().getReference().child("notification").child(post.getUid())
                                                .push().setValue(hm);
                                        sendNoti(post.getTime(),post.getUid(),HomeFragment.auth.getName()+" đã thích bài viết của bạn");
                                    }
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

            avt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (post.getUid().equals(HomeFragment.auth.getUid()) == true) {
                        context.startActivity(new Intent(context, ProfileActivity.class));
                    } else {
                        Intent intent = new Intent(context, UserActivity.class);
                        intent.putExtra("wall_obj", postArrayList.get(position));
                        context.startActivity(intent);
                    }
                }
            });

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (post.getUid().equals(HomeFragment.auth.getUid()) == true) {
                    } else {
                        Intent intent = new Intent(context, UserActivity.class);
                        intent.putExtra("wall_obj", postArrayList.get(position));
                        context.startActivity(intent);
                    }
                }
            });

            cmt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(post);
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) img.getDrawable();
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        shareImageAndText(post.getCaption(),bitmap);
                }
            });

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    watchDialog(post.getImg());
                }
            });
        }
        return convertView;
    }

    private void sendNoti(final String time,final String uid, final String content) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("token");
        Query query = allTokens.orderByKey().equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(),content,
                            "Thông báo mới",uid,time,R.drawable.ic_launcher_background);
                    Sender sender = new Sender(data,token.getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("XXX","thanh cong");
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("XXX","that bai");
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String,String> header = new HashMap<>();
                                header.put("Content-Type","application/json");
                                header.put("Authorization","key=AAAAX3KamYA:APA91bGoIHiR8nlqac-YXcXTFXQ3zOwlhGqhWN8AW6RwjfA-o6vHVpX-ApLD3RwbiYCkBmwNpLscv1RbpCNpU9uvnUqgXH3LUA4Jn7BPsI2QvgnFFAQfplhx7FaGM9dUyMcuI6ryFmsA");

                                return header;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void watchDialog(final String img) {
        final Dialog dialog= new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.watch_image_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        ImageView close = (ImageView)dialog.findViewById(R.id.w_close);
        ImageView image = (ImageView)dialog.findViewById(R.id.w_img);
        ImageView down  =(ImageView)dialog.findViewById(R.id.w_download);
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
                Toast.makeText(context, "Đã tải xuống hình ảnh thành công", Toast.LENGTH_SHORT).show();
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
        DownloadManager  manager  = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    private void shareImageAndText(String caption, Bitmap bitmap) {
        Uri uri = saveImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Chủ đề: ");
        intent.putExtra(intent.EXTRA_TEXT,caption);
        intent.setType("image/png");
        context.startActivity(Intent.createChooser(intent,"Chia sẻ qua"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File file = new File(context.getCacheDir(),"images");
        Uri uri = null;
        try{
            file.mkdirs();
            File file1 = new File(file, "shared_image.png");
            FileOutputStream fileOutputStream = new FileOutputStream(file1);
            bitmap.compress(Bitmap.CompressFormat.PNG,90,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            uri = FileProvider.getUriForFile(context,"com.myapp.phucarussocialapp.fileprovider",file1);
        }catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return  uri;
    }

    private void shareOnlyText(String caption) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Chủ đề: ");
        intent.putExtra(intent.EXTRA_TEXT,caption);
        context.startActivity(Intent.createChooser(intent,"Chia sẻ qua"));
    }

    private void showDialog(final Post temp) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.comment_dialog);
        dialog.show();
        final EditText input = (EditText)dialog.findViewById(R.id.dialog_cmt_input);
        ImageButton send= (ImageButton)dialog.findViewById(R.id.dialog_cmt_send);
        TextView like = (TextView)dialog.findViewById(R.id.dialog_cmt_like);
        ImageView back = (ImageView)dialog.findViewById(R.id.dialog_cmt_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        like.setText(temp.getLike()+" lượt thích");
        final LinearLayout ll = (LinearLayout)dialog.findViewById(R.id.special_ll);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiag(ll,temp);
            }
        });
        TextView none = (TextView)dialog.findViewById(R.id.cmt_none);
        ImageView none_img = (ImageView)dialog.findViewById(R.id.cmt_none_img);
        ListView lv_cmt = (ListView)dialog.findViewById(R.id.lv_cmt);
        final ArrayList<Comment> commentArrayList = new ArrayList<>();
        final CommentAdapter commentAdapter = new CommentAdapter(context,commentArrayList,R.layout.row_comment);
        lv_cmt.setAdapter(commentAdapter);
        loadCommentListOfStatus(temp.getTime(),commentArrayList,commentAdapter,none,none_img);
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Đang xoá bình luận");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input.getText().toString().trim().equals("") == true){
                    Toast.makeText(context, "Nội dung trống", Toast.LENGTH_SHORT).show();
                }else{
                    Calendar calendar = Calendar.getInstance();
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("content",input.getText().toString().trim());
                    hashMap.put("time",String.valueOf(calendar.getTimeInMillis()));
                    hashMap.put("name",HomeFragment.auth.getName());
                    hashMap.put("avt",HomeFragment.auth.getAvatar());
                    hashMap.put("uid",HomeFragment.auth.getUid());
                    FirebaseDatabase.getInstance().getReference().child("comment").child(temp.getTime()).push().setValue(hashMap);
                    input.setText("");
                    //update cmt +1
                    int count_comment = Integer.parseInt(temp.getComment());
                    HashMap<String,Object> hashMap1 = new HashMap<>();
                    hashMap1.put("comment",String.valueOf(++count_comment));
                    temp.setComment(String.valueOf(count_comment));
                    FirebaseDatabase.getInstance().getReference().child("post").child(temp.getTime()).updateChildren(hashMap1);
                    //noti
                    if(temp.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == false) //khong phai do minh post
                    {
                        HashMap<String,Object> hm = new HashMap<>();
                        hm.put("time_post",temp.getTime());
                        hm.put("avt",HomeFragment.auth.getAvatar());
                        hm.put("content",HomeFragment.auth.getName()+" đã bình luận về bài viết của bạn");
                        hm.put("time",String.valueOf(Calendar.getInstance().getTimeInMillis()));
                        hm.put("isSeen","sent");
                        hm.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        FirebaseDatabase.getInstance().getReference().child("notification").child(temp.getUid())
                                .push().setValue(hm);
                        sendNoti(temp.getTime(),temp.getUid(),HomeFragment.auth.getName()+" đã bình luận về bài viết của bạn");
                    }
                }
            }
        });
        lv_cmt.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Bạn có muốn xoá bình luận không?")
                            .setPositiveButton("Không", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setNegativeButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(commentArrayList.get(position).getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == true) {
                                        progressDialog.show();
                                        Query query = FirebaseDatabase.getInstance().getReference().child("comment").child(temp.getTime())
                                                .orderByChild("time").equalTo(commentArrayList.get(position).getTime());
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    ds.getRef().removeValue();
                                                    Toast.makeText(context, "Đã xoá bình luận", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    int count_comment = Integer.parseInt(temp.getComment());
                                                    HashMap<String,Object> hashMap1 = new HashMap<>();
                                                    hashMap1.put("comment",String.valueOf(--count_comment));
                                                    temp.setComment(String.valueOf(count_comment));
                                                    FirebaseDatabase.getInstance().getReference().child("post").child(temp.getTime()).updateChildren(hashMap1);
                                                    commentAdapter.notifyDataSetChanged();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
                                    } else Toast.makeText(context, "Bạn chỉ có thể xoá được bình luận của chính mình", Toast.LENGTH_SHORT).show();
                                }
                            }).create().show();
                return false;
            }
        });
    }

    private void loadCommentListOfStatus(String time, final ArrayList<Comment> commentArrayList, final CommentAdapter commentAdapter, final TextView none, final ImageView none_img) {
        FirebaseDatabase.getInstance().getReference().child("comment").child(time).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentArrayList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Comment comment = ds.getValue(Comment.class);
                    commentArrayList.add(comment);
                    commentAdapter.notifyDataSetChanged();
                }
                if(commentArrayList.size()>0) {
                    none.setText("");
                    none_img.setVisibility(View.INVISIBLE);
                }else{
                    none.setText("Chưa có bình luận nào");
                    none_img.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showDiag(final LinearLayout ll, Post temp) {

        final View dialogView = View.inflate(context,R.layout.who_like_post_dialog,null);
        final Dialog dialog = new Dialog(context,R.style.MyFirstDialogStyle);
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
        WhoLikePostAdapter whoLikePostAdapter = new WhoLikePostAdapter(context,whoLikePostArrayList,R.layout.row_who_like_post);
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
    private void revealShow(View dialogView, boolean b, final Dialog dialog, LinearLayout linearLayout) {
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

    private void deletePost(String time) {
        Query query = FirebaseDatabase.getInstance().getReference().child("post").orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.getRef().removeValue();
                    Toast.makeText(context, "Xoá thành công bài viết", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        HomeFragment.postAdapter.notifyDataSetChanged();
        FirebaseDatabase.getInstance().getReference().child("like").child(time).getRef().removeValue();
        FirebaseDatabase.getInstance().getReference().child("comment").child(time).getRef().removeValue();
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
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}