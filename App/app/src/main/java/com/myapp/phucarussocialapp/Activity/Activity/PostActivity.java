package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myapp.phucarussocialapp.Activity.Fragment.HomeFragment;
import com.myapp.phucarussocialapp.Activity.Fragment.ProfileFragment;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    ImageView back, img,avt;
    Button choose;
    ImageButton up;
    EditText input;
    PopupMenu popupMenu;
    ProgressDialog progressDialog;
    boolean isHaveImg = false;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        AnhXa();
        SetUpValue();
    }

    private void SetUpValue() {
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://phucarus-social-app.appspot.com");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                builder.setMessage("Status sẽ bị huỷ bỏ")
                        .setPositiveButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              finish();
                            }
                        }).create().show();
            }
        });

        Picasso.get().load(HomeFragment.auth.getAvatar()).into(avt);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu = new PopupMenu(PostActivity.this,choose);
                popupMenu.getMenuInflater().inflate(R.menu.choose_img,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.img_camera:
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, ProfileFragment.REQUEST_CODE_CAMERA);
                                break;
                            case R.id.img_gallery:
                                Intent intent1 = new Intent(Intent.ACTION_PICK);
                                intent1.setType("image/*");
                                startActivityForResult(intent1,ProfileFragment.REQUEST_CODE_MEM);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        progressDialog = new ProgressDialog(PostActivity.this);
        progressDialog.setMessage("Đang đăng");

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().trim().equals("") == true) {
                    Toast.makeText(PostActivity.this, "Chưa nhập nội dung", Toast.LENGTH_SHORT).show();
                }else if(!isHaveImg){
                    final Calendar calendar = Calendar.getInstance();
                    HashMap<String,Object> hashMap= new HashMap<>();
                    hashMap.put("uid",HomeFragment.auth.getUid());
                    hashMap.put("avt", HomeFragment.auth.getAvatar());
                    hashMap.put("name",HomeFragment.auth.getName());
                    hashMap.put("time",String.valueOf(calendar.getTimeInMillis()));
                    hashMap.put("caption",input.getText().toString().trim());
                    hashMap.put("img","none");
                    hashMap.put("like","0");
                    hashMap.put("comment","0");
                    hashMap.put("share","0");
                    AddNodeLike(String.valueOf(calendar.getTimeInMillis()));
                    FirebaseDatabase.getInstance().getReference().child("post").child(String.valueOf(calendar.getTimeInMillis()))
                            .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(PostActivity.this, "Đã đăng", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(PostActivity.this, "Đăng bài thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    progressDialog.show();
                    final Calendar calendar = Calendar.getInstance();
                    final StorageReference sref = storageReference.child("post_"+calendar.getTimeInMillis()+ "_" +HomeFragment.auth.getUid()+".png");
                    // Get the data from an ImageView as bytes
                    img.setDrawingCacheEnabled(true);
                    img.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();

                    final UploadTask uploadTask = sref.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(PostActivity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            sref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String profileImageUrl = task.getResult().toString();
                                    HashMap<String,Object> hashMap= new HashMap<>();
                                    hashMap.put("uid",HomeFragment.auth.getUid());
                                    hashMap.put("avt", HomeFragment.auth.getAvatar());
                                    hashMap.put("name",HomeFragment.auth.getName());
                                    hashMap.put("time",String.valueOf(calendar.getTimeInMillis()));
                                    hashMap.put("caption",input.getText().toString().trim());
                                    hashMap.put("img",profileImageUrl);
                                    hashMap.put("like","0");
                                    hashMap.put("comment","0");
                                    hashMap.put("share","0");
                                    AddNodeLike(String.valueOf(calendar.getTimeInMillis()));
                                    FirebaseDatabase.getInstance().getReference().child("post").child(String.valueOf(calendar.getTimeInMillis()))
                                            .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Toast.makeText(PostActivity.this, "Đã đăng", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(PostActivity.this, "Đăng bài thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void AddNodeLike(String s) {
        FirebaseDatabase.getInstance().getReference().child("like").child(s).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue("unliked");
        for(int i=0;i<HomeActivity.authArrayList.size();i++){
            FirebaseDatabase.getInstance().getReference().child("like").child(s).child(HomeActivity.authArrayList.get(i).getUid())
                    .setValue("unliked");
        }
    }

    private void AnhXa() {
        back = (ImageView)findViewById(R.id.post_back);
        img = (ImageView)findViewById(R.id.post_img);
        avt = (ImageView)findViewById(R.id.post_up_avt);
        up = (ImageButton)findViewById(R.id.post_up);
        choose = (Button)findViewById(R.id.post_choose);
        input = (EditText)findViewById(R.id.post_input);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {

        if(requestCode == ProfileFragment.REQUEST_CODE_CAMERA && resultCode == RESULT_OK && data != null){
            Bitmap bit = (Bitmap) data.getExtras().get("data");
            img.setImageBitmap(bit);
            isHaveImg = true;
        }

        if(requestCode == ProfileFragment.REQUEST_CODE_MEM && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bit = BitmapFactory.decodeStream(inputStream);
            img.setImageBitmap(bit);
            isHaveImg = true;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
