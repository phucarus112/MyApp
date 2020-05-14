package com.myapp.phucarussocialapp.Activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.phucarussocialapp.Activity.Activity.ChatActivity;
import com.myapp.phucarussocialapp.Activity.Activity.ProfileActivity;
import com.myapp.phucarussocialapp.Activity.Activity.UserActivity;
import com.myapp.phucarussocialapp.Activity.Object.Comment;
import com.myapp.phucarussocialapp.Activity.Object.WhoLikePost;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class WhoLikePostAdapter extends BaseAdapter {

    Context context;
    ArrayList<WhoLikePost> whoLikePostArrayList;
    int layout;

    public WhoLikePostAdapter(Context context, ArrayList<WhoLikePost> whoLikePostArrayList, int layout) {
        this.context = context;
        this.whoLikePostArrayList = whoLikePostArrayList;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return whoLikePostArrayList.size();
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
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(layout,null);

        ImageView avt = (ImageView) convertView.findViewById(R.id.who_avt);
        TextView name = (TextView) convertView.findViewById(R.id.who_name);
        LinearLayout ll = (LinearLayout)convertView.findViewById(R.id.specific_ll);

        final WhoLikePost whoLikePost = whoLikePostArrayList.get(position);
        Picasso.get().load(whoLikePost.getAvt()).into(avt);
        name.setText(whoLikePost.getName());
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(whoLikePost.getUid()) == false){
                    Intent intent=  new Intent(context ,UserActivity.class);
                    intent.putExtra("user_uid",whoLikePost.getUid());
                    intent.putExtra("user_name",whoLikePost.getName());
                    intent.putExtra("user_avt",whoLikePost.getAvt());
                    context.startActivity(intent);
                }else {
                    context.startActivity(new Intent(context, ProfileActivity.class));
                }
            }
        });
        return convertView;
    }
}
