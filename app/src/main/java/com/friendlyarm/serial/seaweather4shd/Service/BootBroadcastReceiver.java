package com.friendlyarm.serial.seaweather4shd.Service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.friendlyarm.serial.seaweather4shd.FirstActivity;

public class BootBroadcastReceiver extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)){
            Intent autoStartIntent=new Intent(context,  FirstActivity.class);
            autoStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(autoStartIntent);
        }
    }
}
