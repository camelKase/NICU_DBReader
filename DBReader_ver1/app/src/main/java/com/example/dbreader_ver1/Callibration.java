package com.example.dbreader_ver1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Callibration extends AppCompatActivity {
    int minteger = 0;
    TextView textView2, currentCal;
    Button saveButton;
    Float userCal;
    //TextView currentValue;

    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES1 = "MyPrefs" ;
    public static final String calibration = "CalibrationKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callibration);
        textView2 = findViewById(R.id.textView2);
        currentCal = findViewById(R.id.integer_number);

        saveButton = (Button) findViewById(R.id.saveButton);
        sharedPreferences = getSharedPreferences(MyPREFERENCES1, Context.MODE_PRIVATE);
        userCal = sharedPreferences.getFloat(calibration, 0);
        currentCal.setText(userCal.toString());
        minteger = userCal.intValue();


        back();
    }

    public void increaseInteger(View view) {
        minteger = minteger + 1;
        saveCalibrationPreference(minteger);
        display(minteger);
    }

    public void decreaseInteger(View view) {
        minteger = minteger - 1;
        saveCalibrationPreference(minteger);
        display(minteger);
    }

    private void display(int number) {
        TextView displayInteger = (TextView) findViewById(
                R.id.integer_number);
        displayInteger.setText("" + number);
    }

    // I'm passing an int through this method right now to try and connect the calibration level with the saved pref.
    // The value that is being used to calibrate is the minteger, so I'm passing minteger as int n in this method. See
    // methods decreaseInteger and increaseInteger for example.
    private void saveCalibrationPreference(int n) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(calibration, n);
        editor.apply();
        Toast.makeText(this,
                "Calibration Set to " + minteger, Toast.LENGTH_SHORT).show();
    }

    private void back() {
        Button ibList = findViewById(R.id.backButton);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Callibration.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
