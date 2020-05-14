package com.myapp.phucarussocialapp.Activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.Activity.Object.ChatInfo;
import com.myapp.phucarussocialapp.Activity.Object.CountItem;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CountMessagesAdapter extends BaseAdapter {

    Context context;
    ArrayList<CountItem> authArrayList;
    int layout;

    public CountMessagesAdapter(Context context, ArrayList<CountItem> authArrayList, int layout) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(layout,null);

        TextView ten = (TextView) convertView.findViewById(R.id.count_name);
       TextView count = (TextView) convertView.findViewById(R.id.count_count);
        ImageView avt = (ImageView) convertView.findViewById(R.id.count_avt);

        final CountItem auth = authArrayList.get(position);
        Picasso.get().load(auth.getAvt()).into(avt);
        ten.setText(auth.getName());
        count.setText(auth.getCount()+"");

        return convertView;
    }
}
