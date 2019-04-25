package com.example.dbreader_ver1;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID = "Service Channel";

@Override
    public void onCreate(){
    super.onCreate();

    startService(new Intent(this, serviceClass.class));
    createNotificationChannel();
}

//Creates the notifciation channel for background service
private void createNotificationChannel(){
    //only create channel on android oreo or higher
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);


    }


}
}


