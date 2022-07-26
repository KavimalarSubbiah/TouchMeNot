package com.example.touchmenot_deltatask2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class MainActivity2 extends MainActivity {

    private Object view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void Proceed(View view) {
        Intent i = new Intent(MainActivity2.this, MainActivity.class);
        startActivity(i);

    }
    public void HighScoreViewer(View view) {
       Intent i = new Intent(MainActivity2.this,HighScore.class);
        startActivity(i);

    }

    //SharedPreferences sharedPreferences1 = getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
    //final int storedpreference = sharedPreferences1.getInt("key", highscore);
}
