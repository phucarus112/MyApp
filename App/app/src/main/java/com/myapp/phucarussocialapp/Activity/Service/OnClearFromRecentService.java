package com.myapp.phucarussocialapp.Activity.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.myapp.phucarussocialapp.Activity.Activity.MusicActivity;
import com.myapp.phucarussocialapp.Activity.Activity.PlayingMusicActivity;
import com.myapp.phucarussocialapp.Activity.Notification.MusicNotification;
import com.myapp.phucarussocialapp.Activity.Object.Song;
import com.myapp.phucarussocialapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public class OnClearFromRecentService extends Service {

    NotificationManager manager;
    public static MediaPlayer mediaPlayer = null;
    public static ArrayList<Song> listSongs;
    public static int position;
    public static boolean isExist;

    @Override
    public void onCreate() {
        listSongs = new ArrayList<>();
        listSongs = MusicActivity.songArrayList;
        createChannel();
        isExist=true;
        registerReceiver(broadcastReceiver,new IntentFilter("SONG_PLAYING"));
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       String data = intent.getStringExtra("stt");
       position = Integer.parseInt(data);
       return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            manager.cancelAll();
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }

    private void createChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(MusicNotification.CHANNEL_ID,"Playing Music",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init(int vitri) {
        if(OnClearFromRecentService.mediaPlayer != null){
            if(OnClearFromRecentService.mediaPlayer.isPlaying()){
                OnClearFromRecentService.mediaPlayer.stop();
                OnClearFromRecentService.mediaPlayer.release();
            }
        }
        MusicNotification.createNotification(this, listSongs.get(vitri), vitri, listSongs.size() - 1);
        mediaPlayer = MediaPlayer.create(this, Uri.parse(listSongs.get(vitri).getLocation()));
        mediaPlayer.start();
        handleFinish();
    }

    private void handleFinish() {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    OnClearFromRecentService.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            SharedPreferences sharedPreferences = getSharedPreferences("song",MODE_PRIVATE);
                            if(sharedPreferences.getString("shuffle","").equals("true") == true){
                                position = new Random().nextInt(OnClearFromRecentService.listSongs.size()-1);
                                init(position);
                            }else{
                                if(position + 1 > OnClearFromRecentService.listSongs.size() - 1) {
                                    position = 0;
                                    init(position);
                                }else{
                                    position++;
                                    init(position);
                                }
                            }
                        }
                    });
                    handler.postDelayed(this,1000);
                }
            },100);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void handlePrevious() {
        SharedPreferences sharedPreferences = getSharedPreferences("song", MODE_PRIVATE);
        if (sharedPreferences.getString("shuffle", "").equals("true") == true) {
            position = new Random().nextInt(listSongs.size() - 1);
        } else {
            if (position - 1 < 0) position = listSongs.size() - 1;
            else --position;
        }
        init(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void handleNext(){
        SharedPreferences sharedPreferences = getSharedPreferences("song", MODE_PRIVATE);
        if (sharedPreferences.getString("shuffle", "").equals("true") == true) {
            position = new Random().nextInt(listSongs.size() - 1);
        } else {
            if (position + 1 > listSongs.size() - 1) position = 0;
            else ++position;
        }
        init(position);
    }

    private void handlePause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("action_name");
            switch(action){
                case MusicNotification.ACTION_PRE:
                    if(isExist == false) handlePrevious();
                    break;
                case MusicNotification.ACTION_PLAY:
                    if(isExist == false) handlePause();
                    break;
                case MusicNotification.ACTION_NEXT:
                    if(isExist == false) handleNext();
                    break;
            }
        }
    };
}
