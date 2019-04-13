package com.example.dbreader_ver1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Callibration extends AppCompatActivity {
    int minteger = 0;
    TextView textView2;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callibration);
        textView2 = findViewById(R.id.textView2);
        saveButton = (Button) findViewById(R.id.saveButton);
    }

    public void increaseInteger(View view) {
        minteger = minteger + 1;
        display(minteger);
    }

    public void decreaseInteger(View view) {
        minteger = minteger - 1;
        display(minteger);
    }

    private void display(int number) {
        TextView displayInteger = (TextView) findViewById(
                R.id.integer_number);
        displayInteger.setText("" + number);
    }

    private void saveButtonMethod(View view) {
        Button ibList = (Button) findViewById(R.id.saveButton);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.calvar = minteger;
                textView2.setText(minteger);
            }
        });
    }
}
