package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.myapp.phucarussocialapp.Activity.Notification.MusicNotification;
import com.myapp.phucarussocialapp.Activity.Service.OnClearFromRecentService;
import com.myapp.phucarussocialapp.R;

import java.text.SimpleDateFormat;
import java.util.Random;

public class PlayingMusicActivity extends AppCompatActivity {

    TextView tv_title, tv_start, tv_end, tv_singer;
    ImageView iv_pause, iv_disk, back,ib_pre, ib_next;
    CheckBox iv_shuffle;
    SeekBar sb;
    int pos;
    Animation animation;
    Intent intent1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_music);
        Intent intent = getIntent();
        pos = intent.getIntExtra("pos", 0);
        intent1 = new Intent(PlayingMusicActivity.this, OnClearFromRecentService.class);
        intent1.putExtra("stt", String.valueOf(pos));
        startService(intent1);
        AnhXa();
        checkChoice();
        SetEvent();
        init(pos);
    }

    private void AnhXa() {
        tv_title = (TextView) findViewById(R.id.pm_song_name);
        tv_singer = (TextView) findViewById(R.id.pm_singer);
        back = (ImageView) findViewById(R.id.pm_back);
        iv_disk = (ImageView) findViewById(R.id.pm_disk);
        sb = (SeekBar) findViewById(R.id.pm_bar);
        tv_start = (TextView) findViewById(R.id.pm_start);
        tv_end = (TextView) findViewById(R.id.pm_end);
        iv_shuffle = (CheckBox) findViewById(R.id.pm_shuffle);
        iv_pause = (ImageView) findViewById(R.id.pm_pause);
        ib_pre = (ImageView) findViewById(R.id.pm_pre);
        ib_next = (ImageView) findViewById(R.id.pm_next);
    }

    private void checkChoice() {
        SharedPreferences sharedPreferences = getSharedPreferences("song", MODE_PRIVATE);
            String check = sharedPreferences.getString("shuffle", "");
            if (check.equals("true")) { //dc chon
                iv_shuffle.setChecked(true);
            } else {
                iv_shuffle.setChecked(false);
            }
    }

    private void SetEvent() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OnClearFromRecentService.mediaPlayer.isPlaying()) {
                    OnClearFromRecentService.mediaPlayer.pause();
                    iv_pause.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    OnClearFromRecentService.mediaPlayer.start();
                    iv_pause.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });

        ib_pre.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                iv_pause.setImageResource(android.R.drawable.ic_media_pause);
                SharedPreferences sharedPreferences = getSharedPreferences("song", MODE_PRIVATE);
                if (sharedPreferences.getString("shuffle", "").equals("true") == true) {
                    pos = new Random().nextInt(OnClearFromRecentService.listSongs.size() - 1);
                } else {
                    if (pos - 1 < 0) pos = OnClearFromRecentService.listSongs.size() - 1;
                    else pos--;
                }
                init(pos);
            }
        });

        ib_next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                iv_pause.setImageResource(android.R.drawable.ic_media_pause);
                SharedPreferences sharedPreferences = getSharedPreferences("song", MODE_PRIVATE);
                if (sharedPreferences.getString("shuffle", "").equals("true") == true) {
                    pos = new Random().nextInt( OnClearFromRecentService.listSongs.size() - 1);
                } else {
                    if (pos + 1 >  OnClearFromRecentService.listSongs.size() - 1) pos = 0;
                    else pos++;
                }
                init(pos);
            }
        });

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                OnClearFromRecentService.mediaPlayer.seekTo(sb.getProgress());
            }
        });

        iv_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("song", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (sharedPreferences.getString("shuffle", "").equals("true") == true) {
                    iv_shuffle.setChecked(false);
                    editor.putString("shuffle", "false");
                } else {
                    iv_shuffle.setChecked(true);
                    editor.putString("shuffle", "true");
                }
                editor.commit();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init(int vitri) {

        if(OnClearFromRecentService.mediaPlayer != null){
            if(OnClearFromRecentService.mediaPlayer.isPlaying()){
                OnClearFromRecentService.mediaPlayer.stop();
                OnClearFromRecentService.mediaPlayer.release();
            }
        }
        MusicNotification.createNotification(this, MusicActivity.songArrayList.get(pos), pos,
                MusicActivity.songArrayList.size() - 1);
        OnClearFromRecentService.mediaPlayer = MediaPlayer.create(this,
                Uri.parse(MusicActivity.songArrayList.get(vitri).getLocation()));
        OnClearFromRecentService.mediaPlayer.start();

        tv_title.setText(MusicActivity.songArrayList.get(vitri).getName());
        tv_singer.setText(MusicActivity.songArrayList.get(vitri).getSinger());
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        iv_disk.setAnimation(animation);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        tv_end.setText(""+simpleDateFormat.format(OnClearFromRecentService.mediaPlayer.getDuration()));
        sb.setMax(OnClearFromRecentService.mediaPlayer.getDuration());
        controller();
    }

    private void controller(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                tv_start.setText(""+simpleDateFormat.format(OnClearFromRecentService.mediaPlayer.getCurrentPosition()));
                sb.setProgress(OnClearFromRecentService.mediaPlayer.getCurrentPosition());

                OnClearFromRecentService.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        iv_pause.setImageResource(android.R.drawable.ic_media_pause);
                        SharedPreferences sharedPreferences = getSharedPreferences("song",MODE_PRIVATE);
                        if(sharedPreferences.getString("shuffle","").equals("true") == true){
                            pos = new Random().nextInt(OnClearFromRecentService.listSongs.size()-1);
                            init(pos);
                        }else{
                            if(pos + 1 > OnClearFromRecentService.listSongs.size() - 1) {
                                    pos = 0;
                                    init(pos);
                            }else{
                                pos++;
                                init(pos);
                            }
                        }
                    }
                });
                handler.postDelayed(this,1000);
            }
        },100);
    }

    @Override
    public void onBackPressed() {
        OnClearFromRecentService.isExist=false;
        super.onBackPressed();
    }
}

