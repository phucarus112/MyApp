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
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myapp.phucarussocialapp.Activity.Adapter.PrivatedWallAdapter;
import com.myapp.phucarussocialapp.Activity.Fragment.HomeFragment;
import com.myapp.phucarussocialapp.Activity.Fragment.ProfileFragment;
import com.myapp.phucarussocialapp.Activity.Object.Post;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import static com.myapp.phucarussocialapp.Activity.Fragment.HomeFragment.postAdapter;

public class ProfileActivity extends AppCompatActivity {

    ImageView anhbia,avatar,back;
    TextView ten,none;
    ProgressDialog progressDialog;
    StorageReference storageReference;
    ListView lv;
    public static PrivatedWallAdapter privatedWallAdapter;
    public static ArrayList<Post> myPost;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        AnhXa();
        LoadMyPost();
        SetUpValue();
    }

    private void SetUpValue() {
        Picasso.get().load(HomeFragment.auth.getAvatar()).into(avatar);
        ten.setText(HomeFragment.auth.getName());
        privatedWallAdapter.notifyDataSetChanged();
        registerForContextMenu(avatar);
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://phucarus-social-app.appspot.com");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void AnhXa() {
        anhbia = (ImageView)findViewById(R.id.mywall_anhbia);
        avatar = (ImageView)findViewById(R.id.mywall_avatar);
        ten= (TextView)findViewById(R.id.mywall_fullname);
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Đang cập nhật");
        lv = (ListView)findViewById(R.id.mywall_lv);
        lv.setNestedScrollingEnabled(true);
        lv.hasNestedScrollingParent();
        myPost = new ArrayList<>();
        privatedWallAdapter = new PrivatedWallAdapter(ProfileActivity.this,myPost,R.layout.row_post_no_img,R.layout.row_post);
        lv.setAdapter(privatedWallAdapter);
        none = (TextView)findViewById(R.id.mywall_none);
        back = (ImageView)findViewById(R.id.mywall_back);
    }

    private void LoadMyPost() {
        FirebaseDatabase.getInstance().getReference().child("post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myPost.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    if (post.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == true) {
                        myPost.add(post);
                        privatedWallAdapter.notifyDataSetChanged();
                    }
                    if (myPost.size() >= 2) {
                        if (Long.parseLong(myPost.get(0).getTime()) < Long.parseLong(myPost.get(1).getTime())) {
                            Collections.reverse(myPost);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.choose_avatar,menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.avt_camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, ProfileFragment.REQUEST_CODE_CAMERA);
                break;
            case R.id.avt_gallery:
                Intent intent1 = new Intent(Intent.ACTION_PICK);
                intent1.setType("image/*");
                startActivityForResult(intent1,ProfileFragment.REQUEST_CODE_MEM);
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {

        if(requestCode == ProfileFragment.REQUEST_CODE_CAMERA && resultCode == RESULT_OK && data != null){
            final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setMessage("Bạn có đồng ý đổi avatar không?")
                    .setPositiveButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { }
                    })
                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Calendar calendar = Calendar.getInstance();
                            final StorageReference img = storageReference.child("img"+calendar.getTimeInMillis()+".png");
                            progressDialog.show();
                            Bitmap bit = (Bitmap) data.getExtras().get("data");
                            avatar.setImageBitmap(bit);
                            // Get the data from an ImageView as bytes
                            avatar.setDrawingCacheEnabled(true);
                            avatar.buildDrawingCache();
                            Bitmap bitmap = ((BitmapDrawable) avatar.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] data = baos.toByteArray();

                            final UploadTask uploadTask = img.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(ProfileActivity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                    img.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            final String profileImageUrl = task.getResult().toString();
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("avatar", profileImageUrl);
                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ProfileActivity.this, "Cập nhật avatar thành công", Toast.LENGTH_SHORT).show();
                                                    updateAVT(profileImageUrl);
                                                }
                                            })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(ProfileActivity.this, "Cập nhật không thành công", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });

                                }
                            });
                        }
                    }).create().show();
        }

        if(requestCode == ProfileFragment.REQUEST_CODE_MEM && resultCode == RESULT_OK && data != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setMessage("Bạn có đồng ý đổi avatar không?")
                    .setPositiveButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { }
                    })
                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.show();
                            Uri uri = data.getData();
                            InputStream inputStream = null;
                            try {
                                inputStream = getContentResolver().openInputStream(uri);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            Bitmap bit = BitmapFactory.decodeStream(inputStream);
                            avatar.setImageBitmap(bit);

                            Calendar calendar = Calendar.getInstance();
                            final StorageReference img = storageReference.child("img" + calendar.getTimeInMillis() + ".png");
                            avatar.setDrawingCacheEnabled(true);
                            avatar.buildDrawingCache();
                            Bitmap bitmap = ((BitmapDrawable) avatar.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] data = baos.toByteArray();

                            final UploadTask uploadTask = img.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(ProfileActivity.this, "Có lỗi xảy ra"+exception.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                    img.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            final String profileImageUrl = task.getResult().toString();
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("avatar", profileImageUrl);
                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ProfileActivity.this, "Cập nhật avatar thành công", Toast.LENGTH_SHORT).show();
                                                    updateAVT(profileImageUrl);
                                                }
                                            })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(ProfileActivity.this, "Cập nhật không thành công", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                                    Toast.makeText(ProfileActivity.this, "Cập nhật avatar thành công", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }).create().show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateAVT(final String link){
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("avt", link);

        if(myPost.size()>0){
            for(int i=0;i<myPost.size();i++){
                FirebaseDatabase.getInstance().getReference().child("post").child(myPost.get(i).getTime())
                        .updateChildren(hashMap);
            }
        }

        FirebaseDatabase.getInstance().getReference().child("recently").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == false){
                    final String key = dataSnapshot.getKey();
                    Query query = FirebaseDatabase.getInstance().getReference().child("recently").child(key)
                            .orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds: dataSnapshot.getChildren()){
                                FirebaseDatabase.getInstance().getReference().child("recently").child(key)
                                        .child(ds.child("uid").getValue().toString()).updateChildren(hashMap);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
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

        FirebaseDatabase.getInstance().getReference().child("comment").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    final String key = dataSnapshot.getKey();
                    Query query = FirebaseDatabase.getInstance().getReference().child("comment").child(key)
                            .orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds: dataSnapshot.getChildren()){
                                FirebaseDatabase.getInstance().getReference().child("comment").child(key).child(ds.getRef().getKey())
                                        .updateChildren(hashMap);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
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

        FirebaseDatabase.getInstance().getReference().child("notification").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String key = dataSnapshot.getKey();
                if(key.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == false){
                    Query query = FirebaseDatabase.getInstance().getReference().child("notification").child(key)
                            .orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds: dataSnapshot.getChildren()){
                                FirebaseDatabase.getInstance().getReference().child("notification").child(key)
                                        .child(ds.getRef().getKey()).updateChildren(hashMap);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
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

}
