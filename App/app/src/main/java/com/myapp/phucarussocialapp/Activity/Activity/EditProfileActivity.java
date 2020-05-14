package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myapp.phucarussocialapp.Activity.Fragment.HomeFragment;
import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.Activity.Object.Post;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    String condition;
    ImageView back,avt;
    RadioButton nam,nu;
    EditText name,dob,phone;
    TextView update;
    TextInputLayout tl_name,tl_dob,tl_phone;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
       AnhXa();
       Check();
       SetEvent();
    }

    private void AnhXa() {
        back = (ImageView) findViewById(R.id.edit_back);
        avt = (ImageView) findViewById(R.id.edit_avt);
        nam = (RadioButton) findViewById(R.id.edit_nam);
        nu = (RadioButton) findViewById(R.id.edit_nu);
        name = (EditText) findViewById(R.id.edit_et_ten);
        dob = (EditText) findViewById(R.id.edit_et_dob);
        phone = (EditText) findViewById(R.id.edit_et_phone);
        update = (TextView) findViewById(R.id.edit_tv_xn);
        tl_name = (TextInputLayout) findViewById(R.id.til_name);
        tl_dob = (TextInputLayout) findViewById(R.id.til_dob);
        tl_phone = (TextInputLayout) findViewById(R.id.til_phone);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void Check() {
        Intent intent = getIntent();
        condition = intent.getStringExtra("edit");
        if(condition.equals("name") == true){
            tl_dob.setVisibility(View.INVISIBLE);
            dob.setVisibility(View.INVISIBLE);
            tl_phone.setVisibility(View.INVISIBLE);
            phone.setVisibility(View.INVISIBLE);
            nam.setVisibility(View.INVISIBLE);
            nu.setVisibility(View.INVISIBLE);
        }else if(condition.equals("dob") == true) {
            tl_name.setVisibility(View.INVISIBLE);
            name.setVisibility(View.INVISIBLE);
            tl_phone.setVisibility(View.INVISIBLE);
            phone.setVisibility(View.INVISIBLE);
            nam.setVisibility(View.INVISIBLE);
            nu.setVisibility(View.INVISIBLE);
        }else if(condition.equals("phone") == true) {
            tl_dob.setVisibility(View.INVISIBLE);
            dob.setVisibility(View.INVISIBLE);
            name.setVisibility(View.INVISIBLE);
            tl_name.setVisibility(View.INVISIBLE);
            nam.setVisibility(View.INVISIBLE);
            nu.setVisibility(View.INVISIBLE);
        }else{
            tl_dob.setVisibility(View.INVISIBLE);
            dob.setVisibility(View.INVISIBLE);
            name.setVisibility(View.INVISIBLE);
            tl_name.setVisibility(View.INVISIBLE);
            tl_phone.setVisibility(View.INVISIBLE);
            phone.setVisibility(View.INVISIBLE);
        }
    }

    private void SetEvent() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Picasso.get().load(HomeFragment.auth.getAvatar()).into(avt);
        name.setText(HomeFragment.auth.getName());
        dob.setText(HomeFragment.auth.getDateOfBirth());
        if(HomeFragment.auth.getMale_female().equals("male") ==true) nam.setChecked(true);
        else nu.setChecked(true);
        phone.setText(HomeFragment.auth.getPhone());

        nam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nam.setChecked(true);
                nu.setChecked(false);
            }
        });

        nu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nam.setChecked(false);
                nu.setChecked(true);
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int ngay = calendar.get(Calendar.DATE);
                int thang = calendar.get(Calendar.MONTH);
                final int nam = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year,month,dayOfMonth);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        dob.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                },nam,thang,ngay);
                datePickerDialog.show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(condition.equals("name") == true) {
                    if(name.getText().toString().trim().equals("") == true){
                        Toast.makeText(EditProfileActivity.this, "Nội dung trống", Toast.LENGTH_SHORT).show();
                    }else{
                        Query query = databaseReference.child("user").orderByChild("uid")
                                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("name",name.getText().toString().trim());
                                    ds.getRef().updateChildren(hashMap);
                                    Toast.makeText(EditProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                    updateNAME(name.getText().toString().trim());
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
                else if(condition.equals("dob") == true) {
                    if (dob.getText().toString().trim().equals("") == true) {
                        Toast.makeText(EditProfileActivity.this, "Nội dung trống", Toast.LENGTH_SHORT).show();
                    } else {
                        Query query = databaseReference.child("user").orderByChild("uid").equalTo(HomeFragment.auth.getUid());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("dateOfBirth", dob.getText().toString().trim());
                                    ds.getRef().updateChildren(hashMap);
                                    Toast.makeText(EditProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }else if(condition.equals("phone") == true) {
                    if (phone.getText().toString().trim().equals("") == true) {
                        Toast.makeText(EditProfileActivity.this, "Nội dung trống", Toast.LENGTH_SHORT).show();
                    } else {
                        Query query = databaseReference.child("user").orderByChild("uid").equalTo(HomeFragment.auth.getUid());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("phone", phone.getText().toString().trim());
                                    ds.getRef().updateChildren(hashMap);
                                    Toast.makeText(EditProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }else{
                    Query query = databaseReference.child("user").orderByChild("uid").equalTo(HomeFragment.auth.getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds: dataSnapshot.getChildren()){
                                HashMap<String,Object> hashMap = new HashMap<>();
                                if(nu.isChecked()){
                                    hashMap.put("male_female","female");
                                }else hashMap.put("male_female","male");
                                ds.getRef().updateChildren(hashMap);
                                Toast.makeText(EditProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
    }

    private void updateNAME(final String trim) {
        if(HomeFragment.postArrayList.size()>0){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("name", trim);
            for(int i=0;i<HomeFragment.postArrayList.size();i++){
                if(HomeFragment.postArrayList.get(i).getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == true){
                    FirebaseDatabase.getInstance().getReference().child("post").child(HomeFragment.postArrayList.get(i).getTime())
                            .updateChildren(hashMap);
                }
            }
        }
    }
}
