package com.example.dbreader_ver1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.os.Handler;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button listenButton, stopButton;
    TextView dbText;
    MediaRecorder mRecorder;
    Thread runner;

    final Runnable updater = new Runnable(){

        public void run(){
            updateTv();
        };
    };
    final Handler mHandler = new Handler();
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        requestAudioPermissions();

        //listenButton = (Button) findViewById(R.id.listenButton);
        //stopButton = (Button) findViewById(R.id.stopButton);
        dbText = (TextView) findViewById(R.id.dbText);

        /*Set OnClickListener for Listen Button.
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResume();
                updateTv();
            }
        }); */

        /* Set onClickListener for Stop button.
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
            }
        }); */

        if (runner == null)
        {
            runner = new Thread(){
                public void run()
                {
                    while (runner != null)
                    {
                        try
                        {
                            Thread.sleep(1000);
                            Log.i("Noise", "Tock");
                        } catch (InterruptedException e) { };
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
            Log.d("Noise", "start runner()");
        }
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
            startRecording();
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
                    startRecording();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    // These methods are used to pause and resume the recorder which is a WIP. Currently they cause the app to crash so they are commented.
    /*public void onResume()
    {
        super.onResume();
        startRecording();
    }

    public void onPause()
    {
        super.onPause();
        stopRecorder();
    }
    */



    // Initializes media recorder and starts recording.
    private void startRecording() {

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
        dbText.setText(Double.toString((soundDb())) + " dB");
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

        double pressure = getAmplitudeEMA()/51805.5336;
        double amp1 = 0.00002;
        return  20 * Math.log10(pressure / amp1);

    }

    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude());
        else
            return 0;
    }

    // I commented out the sound filter that was originally part fo the calculation. Not sure if we need it, but it didn't change the data as much with or without it.
    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        return amp;
    }
}