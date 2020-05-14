package com.myapp.phucarussocialapp.Activity.Fragment;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.phucarussocialapp.Activity.Activity.EditProfileActivity;
import com.myapp.phucarussocialapp.Activity.Activity.HomeActivity;
import com.myapp.phucarussocialapp.Activity.Activity.LoginActivity;
import com.myapp.phucarussocialapp.Activity.Activity.MusicActivity;
import com.myapp.phucarussocialapp.Activity.Activity.NewsActivity;
import com.myapp.phucarussocialapp.Activity.Activity.PlayistActivity;
import com.myapp.phucarussocialapp.Activity.Activity.ProfileActivity;
import com.myapp.phucarussocialapp.Activity.Activity.QRActivity;
import com.myapp.phucarussocialapp.Activity.Activity.SplashActivity;
import com.myapp.phucarussocialapp.Activity.Adapter.CountMessagesAdapter;
import com.myapp.phucarussocialapp.Activity.Object.ChatInfo;
import com.myapp.phucarussocialapp.Activity.Object.CountItem;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.myapp.phucarussocialapp.Activity.Fragment.HomeFragment.auth;

public class ProfileFragment extends Fragment {

    CardView watch_profile, watch_info, edit_info,my_playist,count_message,my_music,qr, news;
    ImageView avt,edit_profile;
    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_MEM = 2;
    ArrayList<CountItem> countItemArrayList;
    CountMessagesAdapter adapter;
    ListView listView;
    TextView logout;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);
        AnhXa(v);
       SetUpValue();
        return v;
    }

    private void AnhXa(View v) {
        watch_profile = (CardView)v.findViewById(R.id.cv_watch_profile);
        watch_info = (CardView)v.findViewById(R.id.cv_watch_info);
        edit_info = (CardView)v.findViewById(R.id.cv_edit_info);
        count_message = (CardView)v.findViewById(R.id.cv_count_message);
        my_playist = (CardView)v.findViewById(R.id.cv_my_playist);
        my_music = (CardView)v.findViewById(R.id.cv_my_music);
        news = (CardView)v.findViewById(R.id.cv_news);
        qr = (CardView)v.findViewById(R.id.cv_qr);
        avt = (ImageView)v.findViewById(R.id.setting_avt);
        edit_profile = (ImageView)v.findViewById(R.id.setting);
        logout = (TextView)v.findViewById(R.id.cv_logout);
        countItemArrayList = new ArrayList<>();
        adapter = new CountMessagesAdapter(getActivity(),countItemArrayList,R.layout.row_count_messages);
    }

    private void SetUpValue() {

        logout.setPaintFlags(logout.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Đang đăng xuất");
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Bạn có chắc chắn đăng xuất không?")
                        .setPositiveButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                progressDialog.dismiss();
                                startActivity(new Intent(getActivity(),LoginActivity.class));
                                getActivity().finish();
                            }
                        }).create().show();
            }
        });

        loadList(countItemArrayList,adapter);

        Picasso.get().load(HomeFragment.auth.getAvatar()).into(avt);
        watch_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        my_playist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PlayistActivity.class));
            }
        });

        my_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MusicActivity.class));
            }
        });

        edit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SplashActivity.checkInternet.IsNetworkConnected() == true){
                    PopupMenu popupMenu = new PopupMenu(getActivity(),edit_info);
                    popupMenu.getMenuInflater().inflate(R.menu.edit_profile,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Intent intent= new Intent(getActivity(), EditProfileActivity.class);
                            switch (item.getItemId()){
                                case R.id.e_name:
                                    intent.putExtra("edit","name");
                                    break;
                                case R.id.e_dob:
                                    intent.putExtra("edit","dob");
                                    break;
                                case R.id.e_gt:
                                    intent.putExtra("edit","gt");
                                    break;
                                case R.id.e_phone:
                                    intent.putExtra("edit","phone");
                                    break;
                            }
                            getActivity().startActivity(intent);
                            return false;
                        }
                    });
                    popupMenu.show();
                }  else Toast.makeText(getActivity(), "Không có internet", Toast.LENGTH_SHORT).show();
            }
        });

        watch_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SplashActivity.checkInternet.IsNetworkConnected() == true)
                showDiag(edit_profile);
                else Toast.makeText(getActivity(), "Không có internet", Toast.LENGTH_SHORT).show();
            }
        });

        count_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog= new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.count_messages_dialog);
                dialog.show();
                ImageView imageView = (ImageView)dialog.findViewById(R.id.count_close);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                listView = (ListView)dialog.findViewById(R.id.count_lv);

                listView.setAdapter(adapter);
                sortCountList();
               // Toast.makeText(getActivity(), countItemArrayList.get(0).getCount()+"", Toast.LENGTH_SHORT).show();
            }
        });

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NewsActivity.class));
            }
        });

        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), QRActivity.class));
            }
        });
    }

    private void sortCountList() {
        CountItem[] countItems = new CountItem[countItemArrayList.size()];
        for(int i=0;i<countItems.length;i++){
            countItems[i] = countItemArrayList.get(i);
        }
        for(int i=0;i<countItems.length-1;i++){
            for(int j=i+1;j<countItems.length;j++){
                if(countItems[i].getCount() < countItems[j].getCount()){
                    CountItem temp = countItems[i];
                    countItems[i] = countItems[j];
                    countItems[j] = temp;
                }
            }
        }
        countItemArrayList.clear();
        for(int i=0;i<countItems.length;i++){
            countItemArrayList.add(countItems[i]);
        }
        adapter.notifyDataSetChanged();

    }

    private void loadList(final ArrayList<CountItem> countItemArrayList, final CountMessagesAdapter adapter) {
        for(int i=0;i<HomeActivity.authArrayList.size();i++){
            final CountItem countItem = new CountItem();
            countItem.setAvt(HomeActivity.authArrayList.get(i).getAvatar());
            countItem.setName(HomeActivity.authArrayList.get(i).getName());
            countItem.setCount(0);
            countItemArrayList.add(countItem);

            final int index = i;
            FirebaseDatabase.getInstance().getReference().child("chat").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        ChatInfo chat = ds.getValue(ChatInfo.class);
                        if((chat.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == true
                                && chat.getReceiver().equals(HomeActivity.authArrayList.get(index).getUid()) == true) ||
                                (chat.getSender().equals(HomeActivity.authArrayList.get(index).getUid()) == true
                                        && chat.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == true)){
                            int num = countItemArrayList.get(index).getCount();
                            countItemArrayList.get(index).setCount(++num);
                            adapter.notifyDataSetChanged();
                         //  Toast.makeText(getActivity(), countItem.getCount()+"", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            //Toast.makeText(getActivity(), countItem.getCount()+"", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDiag(final ImageView edit) {
        final View dialogView = View.inflate(getActivity(),R.layout.my_info_dialog,null);
        final Dialog dialog = new Dialog(getActivity(),R.style.MyFirstDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        ImageView img = (ImageView)dialog.findViewById(R.id.close);
        img.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                revealShow(dialogView,false,dialog,edit);
            }
        });
        TextView ten = (TextView)dialog.findViewById(R.id.my_profile_name);
        TextView email = (TextView)dialog.findViewById(R.id.my_profile_email);
        TextView sdt = (TextView)dialog.findViewById(R.id.my_profile_sdt);
        TextView gt = (TextView)dialog.findViewById(R.id.my_profile_gt);
        TextView dob = (TextView)dialog.findViewById(R.id.my_profile_dob);
        ten.setText("Nickname: "+ auth.getName());
        email.setText("Email: "+ auth.getEmail());
        sdt.setText("Số điện thoại: "+ auth.getPhone());
        dob.setText("Ngày tháng năm sinh: "+ auth.getDateOfBirth());
        if(auth.getMale_female().equals("male") == true){
            gt.setText("Giới tính: Nam");
        }else gt.setText("Giới tính: Nữ");

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onShow(DialogInterface dialog) {
                revealShow(dialogView,true,null,edit);
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_BACK){
                    revealShow(dialogView,false, dialog,edit);
                    return true;
                }
                return false;
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void revealShow(View dialogView, boolean b, final Dialog dialog, ImageView edit) {
        final View v = dialogView.findViewById(R.id.special_dialog);
        int w = v.getWidth();
        int h = v.getHeight();
        int endRadius = (int) Math.hypot(w,h);
        int cx = (int) (edit.getX()+(edit.getWidth()/2));
        int cy = (int) (edit.getY())+ edit.getHeight() + 56;

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

