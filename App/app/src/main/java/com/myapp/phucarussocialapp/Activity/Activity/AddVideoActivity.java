package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.phucarussocialapp.Activity.Adapter.VideoAdapter;
import com.myapp.phucarussocialapp.Activity.Object.Video;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class AddVideoActivity extends AppCompatActivity {

    EditText input;
    Button btn_search,btn_deny,btn_acc;
    ImageView img,back;
    TextView title;
    String name_playist;
    RequestQueue requestQueue;
    Video video;
    ProgressDialog progressDialog;
    ArrayList<Video> tempList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);
        AnhXa();
        SetUpValue();
    }

    private void AnhXa() {
        input = (EditText)findViewById(R.id.et_add_video);
        btn_search = (Button)findViewById(R.id.btn_add_video_search);
        btn_deny = (Button)findViewById(R.id.btn_add_video_deny);
        btn_acc = (Button)findViewById(R.id.btn_add_video_acc);
        img = (ImageView)findViewById(R.id.iv_add_video_kq);
        back = (ImageView)findViewById(R.id.addVideo_back);
        title = (TextView)findViewById(R.id.tv_add_video_kq);
        btn_acc.setVisibility(View.INVISIBLE);
        btn_deny.setVisibility(View.INVISIBLE);
        video = new Video();
        progressDialog = new ProgressDialog(AddVideoActivity.this);
        progressDialog.setMessage("Đang tìm kiếm");
        tempList = new ArrayList<>();
        loadListVideo();
    }

    private void loadListVideo() {
        tempList.clear();
        FirebaseDatabase.getInstance().getReference().child("video").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Video video = dataSnapshot.getValue(Video.class);
                        if(video.getPlayist().equals(name_playist) == true) tempList.add(video);
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

    private boolean isExist(String str){
        for(int i=0;i<tempList.size();i++){
            if(tempList.get(i).getId().equals(str) == true)return true;
        }
        return false;
    }

    private void SetUpValue() {
        Intent intent = getIntent();
        name_playist = intent.getStringExtra("name");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input.getText().toString().trim().equals("") == true) {
                    Toast.makeText(AddVideoActivity.this, "Nội dung trống", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.show();
                    final String[] token = input.getText().toString().trim().split("https://youtu.be/");
                    String url = "https://www.googleapis.com/youtube/v3/search/?key="+PlayistActivity.API_KEY
                            +"&part=snippet&q="+token[1];
                    video.setId(token[1]);

                    requestQueue = Volley.newRequestQueue(AddVideoActivity.this);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONArray itemsObj = response.getJSONArray("items");
                                        JSONObject snippet = itemsObj.getJSONObject(0).getJSONObject("snippet");
                                        JSONObject thumbnail = snippet.getJSONObject("thumbnails");
                                        JSONObject medium = thumbnail.getJSONObject("medium");
                                        title.setText(snippet.getString("title"));
                                        Picasso.get().load(medium.getString("url")).into(img);
                                        //luu tt video
                                        video.setTitle(snippet.getString("title"));
                                        video.setLinkThumbnail(medium.getString("url"));
                                        progressDialog.dismiss();
                                        btn_acc.setVisibility(View.VISIBLE);
                                        btn_deny.setVisibility(View.VISIBLE);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(AddVideoActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                }
            }
        });

        btn_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddVideoActivity.this);
                builder.setMessage("Bạn có muốn huỷ thêm video này?")
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               finish();
                            }
                        }).create().show();
            }
        });

        btn_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().trim().equals("") == true) {
                    Toast.makeText(AddVideoActivity.this, "Không thêm vào được do chưa tìm được video", Toast.LENGTH_SHORT).show();
                }else if(isExist(video.getId())){
                    Toast.makeText(AddVideoActivity.this, "Video đã có trong playist", Toast.LENGTH_SHORT).show();
                }else{
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("id",video.getId());
                    hashMap.put("title",video.getTitle());
                    hashMap.put("linkThumbnail",video.getLinkThumbnail());
                    hashMap.put("playist",name_playist);
                    Calendar calendar = Calendar.getInstance();
                    hashMap.put("time",String.valueOf(calendar.getTimeInMillis()));
                    FirebaseDatabase.getInstance().getReference().child("video").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                           .push().setValue(hashMap);
                    FirebaseDatabase.getInstance().getReference().child("playist").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(name_playist).setValue(name_playist);
                    Toast.makeText(AddVideoActivity.this, "Đã thêm vào playist", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
