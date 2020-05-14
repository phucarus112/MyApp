package com.myapp.phucarussocialapp.Activity.Notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.myapp.phucarussocialapp.Activity.Activity.MusicActivity;
import com.myapp.phucarussocialapp.Activity.Activity.SplashActivity;
import com.myapp.phucarussocialapp.Activity.Object.Song;
import com.myapp.phucarussocialapp.R;

public class MusicNotification {

    public static final String CHANNEL_ID = "channel_id";
    public static final String ACTION_PRE = "action_pre";
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_NEXT = "action_next";
    public static Notification notification;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void createNotification(Context context, Song song, int pos, int size){

        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(context);
        MediaSessionCompat mediaSession = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mediaSession = new MediaSessionCompat(context,"tag");
        }

        Bitmap icon= BitmapFactory.decodeResource(context.getResources(),R.drawable.disk);
        PendingIntent pendingIntentPre, pendingIntentPlay, pendingIntentNext;

        Intent intentPre = new Intent(context, NotificationAction.class).setAction(ACTION_PRE);
        pendingIntentPre = PendingIntent.getBroadcast(context, 0, intentPre, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentPlay = new Intent(context, NotificationAction.class).setAction(ACTION_PLAY);
        pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentNext = new Intent(context, NotificationAction.class).setAction(ACTION_NEXT);
        pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

        notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                    .setSmallIcon(R.drawable.music)
                    .setContentText(song.getSinger())
                    .setContentTitle(song.getName())
                    .setLargeIcon(icon)
                    .setShowWhen(false)
                    .addAction( android.R.drawable.ic_media_previous,"previous",pendingIntentPre)
                    .addAction(android.R.drawable.ic_media_pause,"play",pendingIntentPlay)
                    .addAction(android.R.drawable.ic_media_next,"next",pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0,1,2).setMediaSession(mediaSession.getSessionToken()))
                    .build();
            notificationCompat.notify(1,notification);
    }
}
