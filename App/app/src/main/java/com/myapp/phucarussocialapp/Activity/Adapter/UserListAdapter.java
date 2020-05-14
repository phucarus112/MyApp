package com.myapp.phucarussocialapp.Activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.phucarussocialapp.Activity.Activity.ChatActivity;
import com.myapp.phucarussocialapp.Activity.Activity.RegisterActivity;
import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter{

    Context context;
    ArrayList<Auth> authArrayList;
    int layout;

    public UserListAdapter(Context context, ArrayList<Auth> authArrayList, int layout) {
        this.context = context;
        this.authArrayList = authArrayList;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return authArrayList.size();
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

            TextView ten = (TextView) convertView.findViewById(R.id.row_name);
           ImageView avt = (ImageView) convertView.findViewById(R.id.row_avt);

        Auth auth = authArrayList.get(position);
        Picasso.get().load(authArrayList.get(position).getAvatar()).into(avt);
        ten.setText(authArrayList.get(position).getName());
        return convertView;
    }
}
