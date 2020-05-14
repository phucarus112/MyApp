package com.myapp.phucarussocialapp.Activity.Adapter;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.phucarussocialapp.Activity.Activity.ChatActivity;
import com.myapp.phucarussocialapp.Activity.Activity.HomeActivity;
import com.myapp.phucarussocialapp.Activity.Activity.ProfileActivity;
import com.myapp.phucarussocialapp.Activity.Activity.UserActivity;
import com.myapp.phucarussocialapp.Activity.Fragment.HomeFragment;
import com.myapp.phucarussocialapp.Activity.Object.ChatInfo;
import com.myapp.phucarussocialapp.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {

    Context context;
    ArrayList<ChatInfo> chatArrayList;
    int layout1,layout2,layout3,layout4;

    public ChatAdapter(Context context, ArrayList<ChatInfo> chatArrayList, int layout1, int layout2,int layout3,int layout4) {
        this.context = context;
        this.chatArrayList = chatArrayList;
        this.layout1 = layout1;
        this.layout2 = layout2;
        this.layout3 = layout3;
        this.layout4 = layout4;
    }

    @Override
    public int getCount() {
        return chatArrayList.size();
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

        final ChatInfo chat = chatArrayList.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:ss");

        if (chat.getSender().equals(HomeFragment.auth.getUid()) == true) {
                if(chat.getType().equals("text") == true){
                    convertView = inflater.inflate(layout1, null);
                    final TextView content = (TextView) convertView.findViewById(R.id.chat_content_send);
                    TextView time = (TextView) convertView.findViewById(R.id.chat_time_send);
                    ImageView avt = (ImageView)convertView.findViewById(R.id.chat_avt_send);
                    TextView isSeen = (TextView) convertView.findViewById(R.id.chat_seen);

                    content.setText(chat.getContent());
                    long milli = Long.parseLong(chat.getTime());
                    time.setText(simpleDateFormat.format(milli));
                    Picasso.get().load(HomeFragment.auth.getAvatar()).into(avt);
                    isSeen.setText(chat.getIsSeen());

                    avt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, ProfileActivity.class));
                        }
                    });
                }
                else{
                    convertView = inflater.inflate(layout3, null);
                    ImageView content = (ImageView) convertView.findViewById(R.id.chat_content_send_image);
                    TextView time = (TextView) convertView.findViewById(R.id.chat_time_send_image);
                    ImageView avt = (ImageView)convertView.findViewById(R.id.chat_avt_send_image);
                    TextView isSeen = (TextView) convertView.findViewById(R.id.chat_seen_image);

                    Picasso.get().load(chat.getContent()).into(content);
                    long milli = Long.parseLong(chat.getTime());
                    time.setText(simpleDateFormat.format(milli));
                    Picasso.get().load(HomeFragment.auth.getAvatar()).into(avt);
                    isSeen.setText(chat.getIsSeen());

                    content.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            watchDialog(chat.getContent());
                        }
                    });

                    avt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, ProfileActivity.class));
                        }
                    });
                }
        }
        else {
            if(chat.getType().equals("text") == true){
                convertView = inflater.inflate(layout2, null);
                TextView content = (TextView) convertView.findViewById(R.id.chat_content_receive);
                TextView time = (TextView) convertView.findViewById(R.id.chat_time_receive);
                ImageView avt = (ImageView)convertView.findViewById(R.id.chat_avt_receive);

                content.setText(chat.getContent());
                long milli = Long.parseLong(chat.getTime());
                time.setText(simpleDateFormat.format(milli));
                Picasso.get().load(ChatActivity.receiver.getAvatar()).into(avt);

                avt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=  new Intent(context, UserActivity.class);
                        intent.putExtra("user_uid",ChatActivity.receiver.getUid());
                        intent.putExtra("user_name",ChatActivity.receiver.getName());
                        intent.putExtra("user_avt",ChatActivity.receiver.getAvatar());
                        context.startActivity(intent);
                    }
                });
            }
            else{
                convertView = inflater.inflate(layout4, null);
                ImageView content = (ImageView) convertView.findViewById(R.id.chat_content_receive_image);
                TextView time = (TextView) convertView.findViewById(R.id.chat_time_receive_image);
                ImageView avt = (ImageView)convertView.findViewById(R.id.chat_avt_receive_image);

                Picasso.get().load(chat.getContent()).into(content);
                long milli = Long.parseLong(chat.getTime());
                time.setText(simpleDateFormat.format(milli));
                Picasso.get().load(ChatActivity.receiver.getAvatar()).into(avt);

                content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        watchDialog(chat.getContent());
                    }
                });

                avt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, UserActivity.class);
                        intent.putExtra("user_uid", ChatActivity.receiver.getUid());
                        intent.putExtra("user_name", ChatActivity.receiver.getName());
                        intent.putExtra("user_avt", ChatActivity.receiver.getAvatar());
                        context.startActivity(intent);
                    }
                });
            }
        }
        return convertView;
    }

    private void watchDialog(final String img) {
        final Dialog dialog= new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.watch_image_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        ImageView close = (ImageView)dialog.findViewById(R.id.w_close);
        ImageView image = (ImageView)dialog.findViewById(R.id.w_img);
        ImageView down = (ImageView)dialog.findViewById(R.id.w_download);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage(img);
                Toast.makeText(context, "Đã tải xuống hình ảnh thành công", Toast.LENGTH_SHORT).show();
            }
        });
        Picasso.get().load(img).into(image);
    }

    private void downloadImage(String img) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(img));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download");
        request.setDescription("Downloading image...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,""+System.currentTimeMillis());
        DownloadManager  manager  = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}
