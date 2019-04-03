package com.example.dbreader_ver1;

import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    Button listenButton, stopButton;
    TextView dbText;
    MediaRecorder mRecorder;
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;
    Thread runner;

    final Runnable updater = new Runnable(){

        public void run(){
            updateTv();
        };
    };
    final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    public void onResume()
    {
        super.onResume();
        startRecording();
    }

    public void onPause()
    {
        super.onPause();
        stopRecorder();
    }

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

    // stops the media recorder. This iss linked to the stop button.
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
        //mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return amp;
    }
}