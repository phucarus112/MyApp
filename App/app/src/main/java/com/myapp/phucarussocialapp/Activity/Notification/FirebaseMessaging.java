package com.myapp.phucarussocialapp.Activity.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.myapp.phucarussocialapp.Activity.Activity.SplashActivity;

public class FirebaseMessaging extends FirebaseMessagingService  {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        SharedPreferences sharedPreferences = getSharedPreferences("SP_USER",MODE_PRIVATE);
        String savedCurrentUser = sharedPreferences.getString("Current_USERID","");

        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");
        FirebaseUser firebaseUser  = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null && sent.equals(firebaseUser.getUid()) == true){
            if(!savedCurrentUser.equals(user)){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    sendOAndAboveNotification(remoteMessage);
                }else{
                    sendNormalAboveNotification(remoteMessage);
                }
            }
        }
    }

    private void sendNormalAboveNotification(RemoteMessage remoteMessage) {

        final String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String other = remoteMessage.getData().get("other");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        final int i= Integer.parseInt(user.replaceAll("[\\D]",""));
        final Intent intent = new Intent(this, SplashActivity.class);
        if(other.equals("") == true){ //chat type
            intent.putExtra("receive",user);
        }else{ //like or cmt type
            intent.putExtra("receive",other);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(uri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if(i>0){
            j=i;
        }
        notificationManager.notify(j,builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {
        final String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String other = remoteMessage.getData().get("other");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i= Integer.parseInt(user.replaceAll("[\\D]",""));
        final Intent intent = new Intent(this, SplashActivity.class);
        if(other.equals("") == true){ //chat type
            intent.putExtra("receive",user);
        }else{ //like or cmt type
            intent.putExtra("receive",other);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        OreoAndAboveNotification notification1 = new OreoAndAboveNotification(this);
        Notification.Builder builder = notification1.getONotification(title,body,pendingIntent,uri,icon);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if(i>0){
            j=i;
        }
        notification1.getManager().notify(j,builder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            updateToken(s);
        }
    }

    private void updateToken(String s) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("token");
        Token newToken  = new Token(s);
        databaseReference.child(user.getUid()).setValue(newToken);
    }
}
