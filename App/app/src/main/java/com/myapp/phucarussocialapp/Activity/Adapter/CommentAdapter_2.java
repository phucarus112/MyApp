package com.myapp.phucarussocialapp.Activity.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myapp.phucarussocialapp.Activity.Activity.DetailActivity;
import com.myapp.phucarussocialapp.Activity.Activity.ProfileActivity;
import com.myapp.phucarussocialapp.Activity.Activity.UserActivity;
import com.myapp.phucarussocialapp.Activity.Object.Comment;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class CommentAdapter_2 extends RecyclerView.Adapter<CommentAdapter_2.ViewHolder>{

    Context context;
    ArrayList<Comment> commentArrayList;

    public CommentAdapter_2(Context context, ArrayList<Comment> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.row_comment,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        Picasso.get().load(commentArrayList.get(position).getAvt()).into(holder.avt);
        holder.name.setText(commentArrayList.get(position).getName());
        holder.content.setText(commentArrayList.get(position).getContent());
        long milli = Long.parseLong(commentArrayList.get(position).getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:ss");
        holder.time.setText(simpleDateFormat.format(milli));

        holder.avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentArrayList.get(position).getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == true){
                    context.startActivity(new Intent(context, ProfileActivity.class));
                }else {
                    Intent intent = new Intent(context, UserActivity.class);
                    intent.putExtra("user_uid", commentArrayList.get(position).getUid());
                    intent.putExtra("user_name", commentArrayList.get(position).getName());
                    intent.putExtra("user_avt", commentArrayList.get(position).getAvt());
                    context.startActivity(intent);
                }
            }
        });

        holder.content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Đang xoá bình luận");
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Bạn có muốn xoá bình luận không?")
                        .setPositiveButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(commentArrayList.get(position).getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) == true) {
                                    progressDialog.show();
                                    Query query = FirebaseDatabase.getInstance().getReference().child("comment")
                                            .child(DetailActivity.data)
                                            .orderByChild("time").equalTo(commentArrayList.get(position).getTime());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                ds.getRef().removeValue();
                                                Toast.makeText(context, "Đã xoá bình luận", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                HashMap<String,Object> hashMap1 = new HashMap<>();
                                                hashMap1.put("comment",String.valueOf(--DetailActivity.totalCmt));
                                                FirebaseDatabase.getInstance().getReference().child("post")
                                                        .child(DetailActivity.data).updateChildren(hashMap1);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                } else Toast.makeText(context, "Bạn chỉ có thể xoá được bình luận của chính mình", Toast.LENGTH_SHORT).show();
                            }
                        }).create().show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView avt;
        TextView name; ;
        TextView content;
        TextView time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avt = (ImageView) itemView.findViewById(R.id.cmt_avt);
            name = (TextView) itemView.findViewById(R.id.cmt_name);
            content = (TextView) itemView.findViewById(R.id.cmt_content);
            time = (TextView) itemView.findViewById(R.id.cmt_time);
        }
    }

}
