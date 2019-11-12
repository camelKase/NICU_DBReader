package com.example.dbreader_ver1;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

import static com.example.dbreader_ver1.App.CHANNEL_ID;

public class serviceClass extends Service{
    Float userThresh;
    float userCalibrate;
    private static final String NOTIF_CHANNEL_ID = "Service Channel";
    public static final String CHANNEL_ID = "Service Channel";
    private static final int NOTIFICATION_ID = 1;

    private static DecimalFormat df = new DecimalFormat("0.00");

    Button listenButton, stopButton;
    TextView dbText;
    MediaRecorder mRecorder;
    Thread runner;
    public boolean alertActive = false;

    public void startRecording() {

        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
        }
        try
        {
            mRecorder.prepare();
        }catch (java.io.IOException ioe) {
            android.util.Log.e("[Monkey]", "IOException: " + android.util.Log.getStackTraceString(ioe));

        }catch (java.lang.SecurityException e) {
            android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
        }
        try
        {
            mRecorder.start();
        }catch (java.lang.SecurityException e) {
            android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
        }

    }

    public double soundDb(){

        double pressure = getAmplitude()/51805.5336;
        double amp1 = 0.00002;
        double Db = 20 * Math.log10(pressure / amp1) + userCalibrate;

        // trigger the alert.
        if (Db > userThresh && alertActive == false)
            tripAlarm(Db);
        return  Db;
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude());
        else
            return 0;
    }

    public void tripAlarm(double Db){

        alertActive = true;
        mRecorder.pause();
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        }else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(serviceClass.this);

        builder.setCancelable(false);
        builder.setTitle("Sound Levels Are Too High");
        builder.setMessage((df.format(Db)) + " dB");


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // alertTextView.setVisibility(View.VISIBLE);
                alertActive = false;
                mRecorder.resume();
            }
        });
        builder.show();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //startForeground();






    }

    public int onStartCommand(Intent intent, int flags, int startId){

        // do your jobs here
        startRecording();
        soundDb();



        return super.onStartCommand(intent, flags, startId);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public IBinder onBind(Intent intent){
        return null;
    }



}
