package com.example.ananth.recyclekiosk;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        TextView name = findViewById(R.id.nameTextView);
        name.setText(DataManager.user.getName());
        getSupportActionBar().hide();
        ProgressBar xpProgressBar = findViewById(R.id.xpProgressBar);
        xpProgressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.buzzYellow), PorterDuff.Mode.SRC_IN);
    }

}
