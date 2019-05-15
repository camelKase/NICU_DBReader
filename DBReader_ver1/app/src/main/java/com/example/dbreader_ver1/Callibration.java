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
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES1 = "MyPrefs" ;
    public static final String calibration = "CalibrationKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callibration);
        textView2 = findViewById(R.id.textView2);
        currentCal = findViewById(R.id.integer_number);

        saveButton = findViewById(R.id.saveButton);
        sharedPreferences = getSharedPreferences(MyPREFERENCES1, Context.MODE_PRIVATE);
        userCal = sharedPreferences.getFloat(calibration, 0);
        currentCal.setText(userCal.toString());
        minteger = userCal.intValue();


        back();
    }

    private void saveCalibrationPreference(int n) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(calibration, n);
        editor.apply();
    }

    public void increaseInteger(View view) {
        saveCalibrationPreference(++minteger);
        display(minteger);
    }

    public void decreaseInteger(View view) {
        saveCalibrationPreference(--minteger);
        display(minteger);
    }

    private void display(int number) {
        TextView displayInteger = findViewById(
                R.id.integer_number);
        displayInteger.setText("" + number);
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
