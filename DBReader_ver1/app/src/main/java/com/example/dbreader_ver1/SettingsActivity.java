package com.example.dbreader_ver1;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    EditText thresholdValue;
    TextView currentValue;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Threshold = "thresholdKey";

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        currentValue=findViewById(R.id.currentThresh);
        thresholdValue=findViewById(R.id.thresholdText);
        Button save = findViewById(R.id.saveButton);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveThresholdPreference();
            }
        });



        back();
    }

    private void saveThresholdPreference() {
        Float t  = Float.valueOf(thresholdValue.getText().toString());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(Threshold, t);
        editor.apply();

        currentValue.setText(t.toString());

    }

    private void back() {
        Button ibList = findViewById(R.id.backButton);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
