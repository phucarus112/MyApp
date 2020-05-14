package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.myapp.phucarussocialapp.Activity.Adapter.ChatAdapter;
import com.myapp.phucarussocialapp.Activity.Fragment.HomeFragment;
import com.myapp.phucarussocialapp.Activity.Fragment.ProfileFragment;
import com.myapp.phucarussocialapp.Activity.Notification.Data;
import com.myapp.phucarussocialapp.Activity.Notification.Sender;
import com.myapp.phucarussocialapp.Activity.Notification.Token;
import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.Activity.Object.ChatInfo;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    public static Auth receiver;
    ImageButton send,send_image;
    ImageView avt,call,back,temp_img;
    TextView name,status;
    EditText input;
    ListView lv_chat;
    ArrayList<ChatInfo> chatInfoArrayList;
    ChatAdapter chatAdapter;
    ProgressDialog progressDialog,progressDialog1;
    boolean CheckData= false;
    RequestQueue requestQueue;
    boolean notify = false;
    DatabaseReference generalRef;
    final int REQUEST_CODE_CAMERA = 101;
    final int REQUEST_CODE_MEM = 202;
    StorageReference storageReference;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        generalRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://phucarus-social-app.appspot.com");
        SetUpComponent();
        SetUpValue();
        ShowMessage();
        DeleteMessage();
    }

    private void SetUpValue() {
        Intent intent = getIntent();
        receiver = (Auth) intent.getSerializableExtra("receive");
        Picasso.get().load(receiver.getAvatar()).into(avt);
        name.setText(receiver.getName());

        avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=  new Intent(ChatActivity.this, UserActivity.class);
                intent.putExtra("user_uid",ChatActivity.receiver.getUid());
                intent.putExtra("user_name",ChatActivity.receiver.getName());
                intent.putExtra("user_avt",ChatActivity.receiver.getAvatar());
                startActivity(intent);
            }
        });

        generalRef.child("user").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Auth tam= dataSnapshot.getValue(Auth.class);
                if(receiver.getUid().equals(tam.getUid()) == true){
                    status.setText(tam.getOnlineStatus());
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

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("onlineStatus","Đang nhập tin..");
                generalRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(hashMap);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void SetUpComponent() {
        back = (ImageView)findViewById(R.id.chat_back);
        avt = (ImageView)findViewById(R.id.chat_avatar_user);
        name = (TextView)findViewById(R.id.chat_userChat);
        input = (EditText)findViewById(R.id.chat_nhaptn);
        send = (ImageButton)findViewById(R.id.chat_guitn);
        send_image= (ImageButton)findViewById(R.id.chat_send_img);
        lv_chat = (ListView)findViewById(R.id.lv_ndchat);
        back.setOnClickListener(this);
        send.setOnClickListener(this);
        send_image.setOnClickListener(this);
        status = (TextView)findViewById(R.id.chat_online_status);
        call = (ImageView)findViewById(R.id.chat_call);
        call.setOnClickListener(this);
        progressDialog = new ProgressDialog(ChatActivity.this);
        progressDialog.setMessage("Đang xoá");
        progressDialog1 = new ProgressDialog(ChatActivity.this);
        progressDialog1.setMessage("Đang gửi");
        requestQueue = Volley.newRequestQueue(ChatActivity.this);
        temp_img = (ImageView)findViewById(R.id.img_temp);

    }

    @Override
    protected void onResume() {
        updateOnStatus();
        listenChangeOnlineStatus();
        super.onResume();
    }

    private void updateOnStatus() {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus","online");
        generalRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(hashMap);
    }

    private void listenChangeOnlineStatus(){
        generalRef.child("user").child(receiver.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        status.setText(dataSnapshot.getValue().toString());
                        if(status.getText().toString().trim().equals("online") == true) {
                            for(int i=0;i<chatInfoArrayList.size();i++){
                                chatInfoArrayList.get(i).setIsSeen("Đã xem");
                            }
                            chatAdapter.notifyDataSetChanged();
                        }
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

    private void updateOffStatus() {
        StringBuilder time = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mm");
        time.append("Truy cập vào ");
        time.append(simpleDateFormat1.format(calendar.getTime())+" ");
        time.append(simpleDateFormat.format(calendar.getTime()));

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus",time.toString());
        generalRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(hashMap);
    }

    @Override
    protected void onStop() {
        updateOffStatus();
        super.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void ShowMessage() {
        chatInfoArrayList = new ArrayList<>();
        chatAdapter = new ChatAdapter(ChatActivity.this,chatInfoArrayList,R.layout.row_send,R.layout.row_receive
                                                                    ,R.layout.row_send_image,R.layout.row_receive_image);
        lv_chat.setStackFromBottom(true);
        lv_chat.setNestedScrollingEnabled(true);
        lv_chat.setAdapter(chatAdapter);

        generalRef.child("chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatInfo item = dataSnapshot.getValue(ChatInfo.class);
                if((item.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == true &&
                        item.getReceiver().equals(receiver.getUid()))
                    || (item.getSender().equals(receiver.getUid()) == true &&
                        item.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))){

                    if(item.getSender().equals(receiver.getUid()) == true &&
                            item.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        Query query = generalRef.child("chat").orderByChild("time").equalTo(item.getTime());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("isSeen","Đã xem");
                                    ds.getRef().updateChildren(hashMap);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                    chatInfoArrayList.add(item);
                    if(status.getText().toString().trim().equals("online") == true) {
                        chatInfoArrayList.get(chatInfoArrayList.size()-1).setIsSeen("Đã xem");
                        chatAdapter.notifyDataSetChanged();
                    }
                }
                chatAdapter.notifyDataSetChanged();
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
        chatAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chat_back:
                finish();
                break;
            case R.id.chat_guitn:
                notify = true;
                handleSendMessage("");
                break;
            case R.id.chat_send_img:
                handleImageMessage();
                break;
            case R.id.chat_call:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+receiver.getPhone()));
                startActivity(intent);
                break;
        }
    }

    private void handleImageMessage() {
        PopupMenu popupMenu = new PopupMenu(ChatActivity.this,send_image);
        popupMenu.getMenuInflater().inflate(R.menu.choose_avatar,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.avt_camera:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CODE_CAMERA);
                        break;
                    case R.id.avt_gallery:
                        Intent intent1 = new Intent(Intent.ACTION_PICK);
                        intent1.setType("image/*");
                        startActivityForResult(intent1,REQUEST_CODE_MEM);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void handleSendMessage(String linkImg) {

        if(linkImg.equals("") == true){
            if (input.getText().toString().trim().equals("") == true) {
                Toast.makeText(ChatActivity.this, "Nội dung trống", Toast.LENGTH_SHORT).show();
            }
            else {
                Calendar calendar = Calendar.getInstance();
                final long milli = calendar.getTimeInMillis();
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("content",input.getText().toString().trim());
                hashMap.put("isSeen","Đã gửi");
                hashMap.put("time",String.valueOf(milli));
                hashMap.put("sender",FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("receiver",receiver.getUid());
                hashMap.put("type","text");
                //
                HashMap<String,Object> hashMap1 = new HashMap<>();
                hashMap1.put("uid",receiver.getUid());
                hashMap1.put("avt",receiver.getAvatar());
                hashMap1.put("name",receiver.getName());
                hashMap1.put("message",input.getText().toString().trim());
                hashMap1.put("time",String.valueOf(milli));
                hashMap1.put("type","text");
                hashMap1.put("notSeen","0");
                FirebaseDatabase.getInstance().getReference().child("recently").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(receiver.getUid()).setValue(hashMap1);
                //
                HashMap<String,Object> hashMap2 = new HashMap<>();
                hashMap2.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap2.put("avt", HomeFragment.auth.getAvatar());
                hashMap2.put("name",HomeFragment.auth.getName());
                hashMap2.put("message",input.getText().toString().trim());
                hashMap2.put("time",String.valueOf(milli));
                hashMap2.put("type","text");
                FirebaseDatabase.getInstance().getReference().child("recently").child(receiver.getUid()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(hashMap2);

                final String msg = input.getText().toString().trim();
                input.setText("");
                generalRef.child("chat").push().setValue(hashMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable final DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if(databaseError == null){
                            DatabaseReference db =FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            db.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Auth model = dataSnapshot.getValue(Auth.class);
                                    if(notify == true){
                                        sendNoti(receiver.getUid(),model.getName(),msg);
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });

                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("onlineStatus","online");
                            generalRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .updateChildren(hashMap);
                            if(status.getText().toString().trim().equals("online") == true){
                                Query query = generalRef.child("chat").orderByChild("time").equalTo(milli);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                                            HashMap<String,Object> hashMap = new HashMap<>();
                                            hashMap.put("isSeen","Đã xem");
                                            ds.getRef().updateChildren(hashMap);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
        else{
            Calendar calendar = Calendar.getInstance();
            final long milli = calendar.getTimeInMillis();
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("content",linkImg);
            hashMap.put("isSeen","Đã gửi");
            hashMap.put("time",String.valueOf(milli));
            hashMap.put("sender",FirebaseAuth.getInstance().getCurrentUser().getUid());
            hashMap.put("receiver",receiver.getUid());
            hashMap.put("type","image");
            //
            HashMap<String,Object> hashMap1 = new HashMap<>();
            hashMap1.put("uid",receiver.getUid());
            hashMap1.put("avt",receiver.getAvatar());
            hashMap1.put("name",receiver.getName());
            hashMap1.put("message",input.getText().toString().trim());
            hashMap1.put("time",String.valueOf(milli));
            hashMap1.put("type","image");
            FirebaseDatabase.getInstance().getReference().child("recently").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(receiver.getUid()).setValue(hashMap1);
            //
            HashMap<String,Object> hashMap2 = new HashMap<>();
            hashMap2.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
            hashMap2.put("avt", HomeFragment.auth.getAvatar());
            hashMap2.put("name",HomeFragment.auth.getName());
            hashMap2.put("message",input.getText().toString().trim());
            hashMap2.put("time",String.valueOf(milli));
            hashMap2.put("type","image");

            FirebaseDatabase.getInstance().getReference().child("recently").child(receiver.getUid()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(hashMap2);

            final String msg = "gửi ảnh cho bạn";
            input.setText("");
            generalRef.child("chat").push().setValue(hashMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable final DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError == null){
                        DatabaseReference db =FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        db.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Auth model = dataSnapshot.getValue(Auth.class);
                                if(notify == true){
                                    sendNoti(receiver.getUid(),model.getName(),msg);
                                }
                                notify = false;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("onlineStatus","online");
                        generalRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .updateChildren(hashMap);
                        if(status.getText().toString().trim().equals("online") == true){
                            Query query = generalRef.child("chat").orderByChild("time").equalTo(milli);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                                        HashMap<String,Object> hashMap = new HashMap<>();
                                        hashMap.put("isSeen","Đã xem");
                                        ds.getRef().updateChildren(hashMap);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private void sendNoti(final String uid, final String name, final String trim) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("token");
        Query query = allTokens.orderByKey().equalTo(receiver.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(),name+" : "+trim,
                            "Tin nhắn mới",uid,"",R.drawable.ic_launcher_background);
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

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {

        if(requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK && data != null) {
            Calendar calendar = Calendar.getInstance();
            final StorageReference img = storageReference.child("img" + calendar.getTimeInMillis() + ".png");
            progressDialog1.show();
            Bitmap bit = (Bitmap) data.getExtras().get("data");
            temp_img.setImageBitmap(bit);
            // Get the data from an ImageView as bytes
            temp_img.setDrawingCacheEnabled(true);
            temp_img.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) temp_img.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] dataArr = baos.toByteArray();

            final UploadTask uploadTask = img.putBytes(dataArr);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(ChatActivity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    img.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            final String profileImageUrl = task.getResult().toString();
                            progressDialog1.dismiss();
                            notify = true;
                            handleSendMessage(profileImageUrl);
                        }
                    });
                }
            });
        }

        if(requestCode == REQUEST_CODE_MEM && resultCode == RESULT_OK && data != null) {
            progressDialog1.show();
            Uri uri = data.getData();
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bit = BitmapFactory.decodeStream(inputStream);
            temp_img.setImageBitmap(bit);

            Calendar calendar = Calendar.getInstance();
            final StorageReference img = storageReference.child("img" + calendar.getTimeInMillis() + ".png");
            temp_img.setDrawingCacheEnabled(true);
            temp_img.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) temp_img.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] dataArr = baos.toByteArray();

            final UploadTask uploadTask = img.putBytes(dataArr);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(ChatActivity.this, "Có lỗi xảy ra" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    img.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            final String profileImageUrl = task.getResult().toString();
                            progressDialog1.dismiss();
                            notify = true;
                            handleSendMessage(profileImageUrl);
                        }
                    });

                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void DeleteMessage() {
        lv_chat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(chatInfoArrayList.get(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == true){
                    //tin nhan nguoi gui chinh là auth
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setMessage("Bạn có chắc chắn muốn xoá không?")
                            .setPositiveButton("Không", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setNegativeButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.show();
                                    Query query = generalRef.child("chat").orderByChild("time")
                                            .equalTo(chatInfoArrayList.get(position).getTime());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                ds.getRef().removeValue();
                                                chatInfoArrayList.remove(position);
                                                chatAdapter.notifyDataSetChanged();
                                                progressDialog.dismiss();
                                                updateRecentlyMessage();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }).create().show();
                }else{
                    Toast.makeText(ChatActivity.this, "Bạn chỉ có thể xoá tin nhắn của chính bạn", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private void updateRecentlyMessage() {
        HashMap<String,Object> hashMap=  new HashMap<>();
        if(chatInfoArrayList.get(chatInfoArrayList.size()-1).getType().equals("text") == true)
            hashMap.put("message",chatInfoArrayList.get(chatInfoArrayList.size()-1).getContent());
        else  hashMap.put("message","Ảnh [File]");

        FirebaseDatabase.getInstance().getReference().child("recently").child(receiver.getUid()).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap);
        FirebaseDatabase.getInstance().getReference().child("recently").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(receiver.getUid()).updateChildren(hashMap);
    }
}
