package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.myapp.phucarussocialapp.Activity.Adapter.SongAdapter;
import com.myapp.phucarussocialapp.Activity.Object.Song;
import com.myapp.phucarussocialapp.R;

import java.util.ArrayList;
import java.util.Collections;

public class MusicActivity extends AppCompatActivity {

    ListView lv;
    SongAdapter songAdapter;
    public static ArrayList<Song> songArrayList;
    final int MY_PERMISSION_REQUEST_CODE = 1001;
    ImageView back;

    boolean isNull = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        loadSongsFromStorage();
        Collections.reverse(songArrayList);
        songAdapter.notifyDataSetChanged();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MusicActivity.this,PlayingMusicActivity.class);
                intent.putExtra("pos",position);
                startActivity(intent);
            }
        });
    }

    private void loadSongsFromStorage() {
        if(ContextCompat.checkSelfPermission(MusicActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MusicActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(MusicActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST_CODE);
            }else{
                ActivityCompat.requestPermissions(MusicActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST_CODE);
            }
        }else{
            doStuff();
        }
    }

    private void doStuff() {
        SetUpValue();
    }

    public void getMusic(){
        ContentResolver contentResolver = getContentResolver();
        Uri uri  = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(uri,null,null,null,null);

        if(songCursor != null && songCursor.moveToFirst()){
            int title = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int location = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do{
                String currentTitle = songCursor.getString(title);
                String currentArtist = songCursor.getString(artist);
                String currentLocation= songCursor.getString(location);
                songArrayList.add(new Song(currentTitle,currentArtist,currentLocation));
            }while(songCursor.moveToNext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MusicActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED){
                        Log.d("JJJ","permission granted");
                        doStuff();
                    }
                }else {
                    Log.d("JJJ","no permission granted");
                    finish();
                }
                return;
            }
        }
    }

    private void SetUpValue() {
        lv = (ListView)findViewById(R.id.lv_music);
        songArrayList = new ArrayList<>();
        getMusic();
        songAdapter = new SongAdapter(MusicActivity.this,songArrayList,R.layout.row_music);
        lv.setAdapter(songAdapter);
        back = (ImageView)findViewById(R.id.list_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
