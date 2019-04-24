package com.example.dbreader_ver1;

import android.Manifest;
import android.app.Notification;
<<<<<<< HEAD
import android.app.PendingIntent;
=======
>>>>>>> 5cd6f62c21b5f66cecc1366b9b147893b93e0ed6
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
<<<<<<< HEAD
import android.support.v4.app.TaskStackBuilder;
=======
>>>>>>> 5cd6f62c21b5f66cecc1366b9b147893b93e0ed6
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.os.Handler;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;

//import static com.example.dbreader_ver1.Callibration.MyPREFERENCES2;
import static com.example.dbreader_ver1.App.CHANNEL_ID;
import static com.example.dbreader_ver1.Callibration.calibration;
import static com.example.dbreader_ver1.SettingsActivity.MyPREFERENCES1;
import static com.example.dbreader_ver1.SettingsActivity.Threshold;


public class MainActivity extends AppCompatActivity {
//    public static final String MyPREFERENCES = "MyPrefs" ;
//    public static final String Threshold = "thresholdKey";

    private NotificationManagerCompat notificationManager;


    // Shared preferences for the threshold level and calibration value.
    Float userThresh;
    float userCalibrate;

    private static DecimalFormat df = new DecimalFormat("0.00");

    Button listenButton, stopButton;
    TextView dbText;
    MediaRecorder mRecorder;
    Thread runner;
    public boolean alertActive = false;
    final Runnable updater = new Runnable(){

        public void run(){
            updateTv();
            //checkDb(soundDb());

        };
    };
    final Handler mHandler = new Handler();
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        requestAudioPermissions();
        initListButton();

        SharedPreferences sharedPreferences1 = getSharedPreferences(MyPREFERENCES1, Context.MODE_PRIVATE);
        //SharedPreferences sharedPreferences2 = getSharedPreferences(MyPREFERENCES2, Context.MODE_PRIVATE);

        userThresh = sharedPreferences1.getFloat(Threshold, 0);
        userCalibrate = sharedPreferences1.getFloat(calibration,0);

        initCallibration();

        listenButton = (Button) findViewById(R.id.listenButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        dbText = (TextView) findViewById(R.id.dbText);


        if (runner == null)
        {
            runner = new Thread(){
                public void run()
                {
                    while (runner != null)
                    {
                        try
                        {
                            Thread.sleep(250);
                            Log.i("Noise", "Tock");
                        } catch (InterruptedException e) { };
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
            Log.d("Noise", "start runner()");
        }
        notificationManager = NotificationManagerCompat.from(this);


    }

    private void initListButton() {
        Button ibList = (Button) findViewById(R.id.settingsButton);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                // Calling the stopRecorder method to allow re-initialization of the mRecorder when you return to the main page and tap Listen.
                stopRecorder();
            }
        });
    }

    private void initCallibration() {
        Button ibList = (Button) findViewById(R.id.callibrateButton);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Callibration.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                // Calling the stopRecorder method to allow re-initialization of the mRecorder when you return to the main page and tap Listen.
                stopRecorder();
            }
        });
    }

    // Method to request permission for mic recording
    public void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            //startRecording();
        }
    }

    //Handling callback for permission request
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    //startRecording();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    //Attaches the start recording button to the listen
    public void listenButton(View v){
        if (mRecorder == null) {
            startRecording();
        } else {
            mRecorder.resume();
        }

    }
    // Initializes media recorder and starts recording.
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

    // display the DB level in the textView 'dbText'.
    public void updateTv(){

        double dbLevel = soundDb();

        if(mRecorder == null) {
            dbText.setText("- dB");
        } else {
            dbText.setText(df.format(dbLevel) + " dB");
            //Calibration: " + userCalibrate);
        }
    }

    public void stopButton(View v){
        stopRecorder();
    }

    // stops the media recorder. This is linked to the stop button that is currently not implemented.
    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    // This calculation was derived from this link: https://stackoverflow.com/questions/10655703/what-does-androids-getmaxamplitude-function-for-the-mediarecorder-actually-gi
    // We are reading sound from the device using the MediaRecorder's 'getMaxAmplitude' method. The return of this method is explain in the link above as well.
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

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

        //Show the notification if the noise levels get too loud in the foreground
        String title = "Sound Meter";
<<<<<<< HEAD
        String message = "Sound levels are too high";

        //Created an Intent for Main Activity
        Intent intent = new Intent(this, MainActivity.class);

        //Creates Taskbuilder and add the MainActivity intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        //Get the pending intent containing the entire backstack
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

=======
        String message = "Sound Levels Are too High";
>>>>>>> 5cd6f62c21b5f66cecc1366b9b147893b93e0ed6

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_android)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
<<<<<<< HEAD
                .setContentIntent(pendingIntent)
=======
>>>>>>> 5cd6f62c21b5f66cecc1366b9b147893b93e0ed6
                .build();

                notificationManager.notify(1,notification);



<<<<<<< HEAD



=======
>>>>>>> 5cd6f62c21b5f66cecc1366b9b147893b93e0ed6
    }

    public void startService(View v){
        double lb  = soundDb();

        Intent serviceIntent = new Intent(this, serviceClass.class);
       serviceIntent.putExtra("input" , lb);

        startService(serviceIntent);

    }
    public void stopService(){
        Intent serviceIntent = new Intent(this, serviceClass.class);
        stopService(serviceIntent);


    }
}