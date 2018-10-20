package com.example.ananth.recyclekiosk;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
        ViewPager pager = findViewById(R.id.mainViewPager);
        ScreenSlidePagerAdapter adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);


    }
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        List<ScoreItem> tempData;
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            tempData = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                tempData.add(new ScoreItem("Score "+i,i));
            }
        }

        @Override
        public Fragment getItem(int position) {
            PersonalScoreFragment fragment = new PersonalScoreFragment();
            fragment.setDataset(tempData);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
