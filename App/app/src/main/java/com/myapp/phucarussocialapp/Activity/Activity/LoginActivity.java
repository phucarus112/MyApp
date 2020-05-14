package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.Activity.Object.CheckInternet;
import com.myapp.phucarussocialapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText email, pass;
    Button dangnhap;
    TextView quenmk,dangky,error;
    ProgressDialog progressDialog,progressDialog1;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    public static final int RC_SIGN_IN = 999;
    public static String MY_AVT;
    public static CheckInternet checkInternet;
    String URL_avt="https://cdn.pixabay.com/photo/2016/08/08/09/17/avatar-1577909_960_720.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkInternet = new CheckInternet(LoginActivity.this);
        setUpComponent();
    }

    @Override
    protected void onResume() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            databaseReference.child("user").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Auth auth = dataSnapshot.getValue(Auth.class);
                    if(user.getUid().equals(auth.getUid()) ==true){
                        MY_AVT = auth.getAvatar();
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

            if(checkInternet.IsNetworkConnected() == false)MY_AVT=null;
            Intent intent1 = new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent1);
            finish();

        }
        super.onResume();
    }

    private void setUpComponent() {
        email= (EditText)findViewById(R.id.et_tdn);
        pass =(EditText)findViewById(R.id.et_mk);
        dangnhap =(Button)findViewById(R.id.btn_dn);
        dangky =(TextView) findViewById(R.id.tv_dk);
        quenmk =(TextView) findViewById(R.id.tv_qmk);
        error = (TextView)findViewById(R.id.error_dn);
        dangky.setOnClickListener(this);
        dangnhap.setOnClickListener(this);
        quenmk.setOnClickListener(this);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Đang đăng nhập");
        progressDialog1 = new ProgressDialog(LoginActivity.this);
        progressDialog1.setMessage("Đang gửi Email");
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_dk:
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_dn:
                login();
                break;
            case R.id.tv_qmk:
                showDialogQMK();
                break;
        }
    }

    private void updateNewUserToLike(String uid) {
        final HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put(uid,"unliked");
        FirebaseDatabase.getInstance().getReference().child("like").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                dataSnapshot.getRef().updateChildren(hashMap);
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

    private void login() {
        if(email.getText().toString().trim().equals("") == true || pass.getText().toString().trim().equals("") == true){
            error.setText("Bạn chưa nhập email hoặc mật khẩu");
            return;
        }
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email.getText().toString().trim(), pass.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            progressDialog.dismiss();
                            Intent intent1 = new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(intent1);
                            finish();
                        } else {
                            progressDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại. Kiểm tra lại Email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogQMK() {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forget_password_dialog);
        dialog.show();
        final EditText tenEmail = (EditText)dialog.findViewById(R.id.forget_pass_ten_email);
        Button ok = (Button)dialog.findViewById(R.id.forget_pass_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(tenEmail.getText().toString().trim().equals("") == true){
                        Toast.makeText(LoginActivity.this, "Tên Email không để trống", Toast.LENGTH_SHORT).show();
                    }else{
                        progressDialog1.show();
                        firebaseAuth.sendPasswordResetEmail(tenEmail.getText().toString().trim())
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressDialog1.dismiss();
                                            Toast.makeText(LoginActivity.this, "Gửi thành công. Vui lòng check Email để đổi mật khẩu", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }else{
                                            progressDialog1.dismiss();
                                            Toast.makeText(LoginActivity.this, "Gửi Email thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog1.dismiss();
                                Toast.makeText(LoginActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
            }
        });
    }

}
