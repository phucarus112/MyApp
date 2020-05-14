package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText email,phone,pass,xacnhanpass,fullname,dob;
    Button dangky;
    RadioButton nam,nu;
    TextView error;
    ProgressDialog progressDialog;
    public static String URL_avt="https://cdn.pixabay.com/photo/2016/08/08/09/17/avatar-1577909_960_720.png";
    ArrayList<String> emailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loadEmailList();
        setUpComponent();
    }

    private void setUpComponent() {
        email= (EditText)findViewById(R.id.et_email_dk);
        phone= (EditText)findViewById(R.id.et_sdt_dk);
        pass= (EditText)findViewById(R.id.et_mk_dk);
        xacnhanpass= (EditText)findViewById(R.id.et_nlmk_dk);
        fullname= (EditText)findViewById(R.id.et_name_dk);
        dob= (EditText)findViewById(R.id.et_dob_dk);
        error = (TextView)findViewById(R.id.error_dk);
        dangky = (Button)findViewById(R.id.btn_dk);
        nam = (RadioButton)findViewById(R.id.rb_nam_dk);
        nu = (RadioButton)findViewById(R.id.rb_nu_dk);
        nam.setOnClickListener(this);
        nu.setOnClickListener(this);
        dangky.setOnClickListener(this);
        dob.setOnClickListener(this);
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Đang đăng ký");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rb_nam_dk:
                nu.setChecked(false);
                break;
            case R.id.rb_nu_dk:
                nam.setChecked(false);
                break;
            case R.id.et_dob_dk:
                final Calendar calendar = Calendar.getInstance();
                int ngay = calendar.get(Calendar.DATE);
                int thang = calendar.get(Calendar.MONTH);
                int nam = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year,month,dayOfMonth);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        dob.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                },nam,thang,ngay);
                datePickerDialog.show();
                break;
            case R.id.btn_dk:
                 boolean check = checkData();
                 if(check == true) register();
                break;
        }
    }

    private boolean checkData() {
        if (email.getText().toString().trim().equals("") == true || pass.getText().toString().trim().equals("") == true
                || xacnhanpass.getText().toString().trim().equals("") == true || fullname.getText().toString().trim().equals("") == true
                || phone.getText().toString().trim().equals("") == true || dob.getText().toString().trim().equals("") == true) {
            error.setText("Chưa đăng ký đủ thông tin");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
            error.setText("Email sai định dạng");
            return false;
        } else if (pass.getText().toString().trim().length() < 6) {
            error.setText("Mật khẩu có từ 6 ký tự trở lên");
            return false;
        } else if (!Patterns.PHONE.matcher(phone.getText().toString().trim()).matches()) {
            error.setText("SĐT sai định dạng");
            return false;
        } else if (pass.getText().toString().trim().equals(xacnhanpass.getText().toString().trim()) == false) {
            error.setText("Xác nhận mật khẩu chưa đúng");
            return false;
        } else if (isExistedEmail(email.getText().toString().trim()) == true) {
            error.setText("Email đã được sử dụng. Vui lòng chọn email khác");
            return false;
        } else return true;
    }

    private void register() {
        progressDialog.show();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString().trim(), pass.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
                            HashMap<Object,String> hashMap = new HashMap<>();
                            hashMap.put("phone",phone.getText().toString().trim());
                            hashMap.put("email",email.getText().toString().trim());
                            hashMap.put("name",fullname.getText().toString().trim());
                            hashMap.put("dateOfBirth",dob.getText().toString().trim());
                            if(nam.isChecked()){
                                hashMap.put("male_female","male");
                            }else hashMap.put("male_female","female");
                            hashMap.put("avatar",URL_avt);
                            hashMap.put("uid",current.getUid());
                            hashMap.put("onlineStatus",currentDate());
                           FirebaseDatabase.getInstance().getReference().child("user").child(current.getUid())
                                   .setValue(hashMap);
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            updateNewUserToLike(current.getUid());
                            Intent intent1 = new Intent(RegisterActivity.this,LoginActivity.class);
                            startActivity(intent1);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại. Email đã tồn tại",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private String currentDate(){
        StringBuilder time = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm");
        time.append("Truy cập vào ");
        time.append(simpleDateFormat1.format(calendar.getTime())+" ");
        time.append(simpleDateFormat.format(calendar.getTime()));
        return time.toString();
    }

    private void loadEmailList() {
        emailList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Auth auth = dataSnapshot.getValue(Auth.class);
                    emailList.add(auth.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private boolean isExistedEmail(String my_email) {
        for(int i=0;i<emailList.size();i++){
            if(my_email.equals(emailList.get(i)) == true)return true;
        }
        return false;
    }
}
