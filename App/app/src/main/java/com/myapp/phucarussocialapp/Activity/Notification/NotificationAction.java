package com.myapp.phucarussocialapp.Activity.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationAction extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent transfer = new Intent("SONG_PLAYING");
        transfer.putExtra("action_name",intent.getAction());
        context.sendBroadcast(transfer);
    }
}
