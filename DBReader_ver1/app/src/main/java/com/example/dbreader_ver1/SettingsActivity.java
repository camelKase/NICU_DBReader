package com.example.dbreader_ver1;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {
    Float userThresh;
    EditText thresholdValue;
    TextView currentValue;
    String pname = "";

    public static final String MyPREFERENCES1 = "MyPrefs" ;
    public static final String Threshold = "thresholdKey";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        currentValue=findViewById(R.id.currentThresh);
        thresholdValue=findViewById(R.id.thresholdText);
        Button save = findViewById(R.id.saveButton);
        sharedPreferences = getSharedPreferences(MyPREFERENCES1, Context.MODE_PRIVATE);
        userThresh = sharedPreferences.getFloat(Threshold, 0);
        currentValue.setText(userThresh.toString() + " dB");


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveThresholdPreference();
            }
        });
        back();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btnLogout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SettingsActivity.this, loginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void saveThresholdPreference() {
        Float t  = Float.valueOf(thresholdValue.getText().toString());
        if (t == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setCancelable(false);
            builder.setTitle("Invalid Threshold Value.");
            builder.setMessage("Please enter a number in the Threshold field.");
        } else {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat(Threshold, t);
            editor.apply();

            currentValue.setText(t.toString());
            String dblevel = t.toString();
            DBLevel dl = new DBLevel(pname, dblevel);
            FirebaseDatabase.getInstance().getReference("DBLevels")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(dl).addOnCompleteListener(SettingsActivity.this, new OnCompleteListener<Void>() {

                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }

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
