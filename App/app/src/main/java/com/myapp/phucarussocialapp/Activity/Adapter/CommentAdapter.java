package com.myapp.phucarussocialapp.Activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.myapp.phucarussocialapp.Activity.Activity.ChatActivity;
import com.myapp.phucarussocialapp.Activity.Activity.ProfileActivity;
import com.myapp.phucarussocialapp.Activity.Activity.UserActivity;
import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.Activity.Object.Comment;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {

    Context context;
    ArrayList<Comment> commentArrayList;
    int layout;

    public CommentAdapter(Context context, ArrayList<Comment> commentArrayList, int layout) {
        this.context = context;
        this.commentArrayList = commentArrayList;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return commentArrayList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(layout,null);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:ss");

        ImageView avt = (ImageView) convertView.findViewById(R.id.cmt_avt);
        TextView name = (TextView) convertView.findViewById(R.id.cmt_name);
        TextView content = (TextView) convertView.findViewById(R.id.cmt_content);
        TextView time = (TextView) convertView.findViewById(R.id.cmt_time);

        final Comment comment = commentArrayList.get(position);
        Picasso.get().load(comment.getAvt()).into(avt);
        name.setText(comment.getName());
        content.setText(comment.getContent());
        long milli = Long.parseLong(comment.getTime());
        time.setText(simpleDateFormat.format(milli));

        avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comment.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == true){
                    context.startActivity(new Intent(context, ProfileActivity.class));
                }else {
                    Intent intent = new Intent(context, UserActivity.class);
                    intent.putExtra("user_uid", comment.getUid());
                    intent.putExtra("user_name", comment.getName());
                    intent.putExtra("user_avt", comment.getAvt());
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }
}
