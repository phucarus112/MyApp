package com.myapp.phucarussocialapp.Activity.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myapp.phucarussocialapp.Activity.Activity.AddVideoActivity;
import com.myapp.phucarussocialapp.Activity.Activity.PlayistActivity;
import com.myapp.phucarussocialapp.R;

import java.util.ArrayList;

public class PlayistAdapter  extends BaseAdapter {

    Context context;
    ArrayList<String> playistList;
    int layout;

    public PlayistAdapter(Context context, ArrayList<String> playistList, int layout) {
        this.context = context;
        this.playistList = playistList;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return playistList.size();
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layout,null);

        TextView name = (TextView)convertView.findViewById(R.id.row_playist_name);
        final ImageView op = (ImageView)convertView.findViewById(R.id.row_playist_option);

        name.setText(playistList.get(position));
        op.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(context, op);
                popupMenu.getMenuInflater().inflate(R.menu.playist_option, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.row_playist_add:
                                Intent intent = new Intent(context, AddVideoActivity.class);
                                intent.putExtra("name", playistList.get(position));
                                context.startActivity(intent);
                                break;
                            case R.id.row_playist_del:
                                ProgressDialog progressDialog = new ProgressDialog(context);
                                progressDialog.setMessage("Đang xoá playist");
                                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Bạn có chắc muốn xoá playist này không?")
                                        .setPositiveButton("Không", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).setNegativeButton("Có", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseDatabase.getInstance().getReference().child("playist")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child(playistList.get(position)).removeValue();
                                        Query query1 = FirebaseDatabase.getInstance().getReference().child("video")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .orderByChild("playist").equalTo(playistList.get(position));
                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    ds.getRef().removeValue();
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
                                        popupMenu.dismiss();
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

        return convertView;
    }
}