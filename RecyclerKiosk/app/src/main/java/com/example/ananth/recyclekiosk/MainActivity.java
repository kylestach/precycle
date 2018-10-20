package com.example.ananth.recyclekiosk;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ProgressBar xpProgressBar;
    private ValueAnimator animator;
    private boolean rotatedFab;

    private ValueAnimator cameraUpAnimator,
            cameraDownAnimator,
            helpUpAnimator,
            helpDownAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        TextView name = findViewById(R.id.nameTextView);
        name.setText(DataManager.user.getName());
        getSupportActionBar().hide();
        xpProgressBar = findViewById(R.id.xpProgressBar);
        //xpProgressBar.setScaleY(2.0f);
        animator = ValueAnimator.ofInt(0, 50);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                xpProgressBar.setProgress((int) animation.getAnimatedValue());
            }
        });
        //xpProgressBar.setProgress(50);
        xpProgressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.buzzYellow), PorterDuff.Mode.SRC_IN);
        xpProgressBar.setProgress(0);
        animator.setStartDelay(1000);
        animator.start();
        //ViewPager pager = findViewById(R.id.mainViewPager);
        DiscreteScrollView pager = findViewById(R.id.mainViewPager);
        ScreenSlidePagerAdapter adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        //pager.setAdapter(adapter);
        List<ScoreItem> tempData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            tempData.add(new ScoreItem("Score " + i, i));
        }
        List<ScorePageModel> modelList = new ArrayList<>();
        modelList.add(new ScorePageModel(new ScoreAdapter(tempData, MainActivity.this),"Breakdown"));
        modelList.add(new ScorePageModel(new ScoreAdapter(tempData, MainActivity.this),"Leaderboard"));
        pager.setAdapter(new ScoreListAdapter(modelList, MainActivity.this));
        pager.setOverScrollEnabled(true);
        rotatedFab = false;
        final FloatingActionButton menuFAB = findViewById(R.id.fabMain);
        menuFAB.setSize(FloatingActionButton.SIZE_NORMAL);
        final View grayView = findViewById(R.id.grayView);
        grayView.setAlpha(0f);
        grayView.setVisibility(View.GONE);
        final FloatingActionButton cameraFab = findViewById(R.id.fabCamera);
        final FloatingActionButton helpFab = findViewById(R.id.fabHelp);
        final TextView cameraLabel = findViewById(R.id.scanLabel);
        final TextView helpLabel = findViewById(R.id.chatLabel);
        cameraFab.setAlpha(0f);
        helpFab.setAlpha(0f);
        cameraLabel.setAlpha(0f);
        helpLabel.setAlpha(0f);
        final ValueAnimator alphaVisibleAnimator = ValueAnimator.ofFloat(0f, 1f);
        alphaVisibleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                cameraFab.setAlpha((float) animation.getAnimatedValue());
                helpFab.setAlpha((float) animation.getAnimatedValue());
                cameraLabel.setAlpha((float) animation.getAnimatedValue());
                helpLabel.setAlpha((float) animation.getAnimatedValue());
                if((float)animation.getAnimatedValue()==0) {
                    grayView.setVisibility(View.VISIBLE);
                }
                grayView.setAlpha(((float) animation.getAnimatedValue())/2);
            }
        });
        final ValueAnimator alphaInvisibleAnimator = ValueAnimator.ofFloat(1f, 0f);
        alphaInvisibleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                cameraFab.setAlpha((float) animation.getAnimatedValue());
                helpFab.setAlpha((float) animation.getAnimatedValue());
                cameraLabel.setAlpha((float) animation.getAnimatedValue());
                helpLabel.setAlpha((float) animation.getAnimatedValue());
                grayView.setAlpha(((float) animation.getAnimatedValue())/2);
                if((float)animation.getAnimatedValue()==0) {
                    grayView.setVisibility(View.GONE);
                }
            }
        });
        grayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rotatedFab) {
                    final OvershootInterpolator interpolator = new OvershootInterpolator();
                    ViewCompat.animate(menuFAB).
                            rotation(0f).
                            withLayer().
                            setDuration(300).
                            setInterpolator(interpolator).
                            start();
                    rotatedFab = false;
                    alphaVisibleAnimator.cancel();
                    alphaInvisibleAnimator.start();
                    cameraUpAnimator.cancel();
                    cameraDownAnimator.start();
                    helpUpAnimator.cancel();
                    helpDownAnimator.start();
                }
            }
        });
        menuFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rotatedFab) {
                    final OvershootInterpolator interpolator = new OvershootInterpolator();
                    ViewCompat.animate(menuFAB).
                            rotation(0f).
                            withLayer().
                            setDuration(300).
                            setInterpolator(interpolator).
                            start();
                    rotatedFab = false;
                    alphaVisibleAnimator.cancel();
                    alphaInvisibleAnimator.start();
                    cameraUpAnimator.cancel();
                    cameraDownAnimator.start();
                    helpUpAnimator.cancel();
                    helpDownAnimator.start();
                } else {
                    if (cameraUpAnimator == null) {
                        float cameraYOffset = menuFAB.getY() - cameraFab.getY();
                        cameraFab.setTranslationY(cameraYOffset);
                        float helpYOffset = menuFAB.getY() - helpFab.getY();
                        helpFab.setTranslationY(helpYOffset);
                        cameraFab.setAlpha(0f);
                        helpFab.setAlpha(0f);

                        cameraUpAnimator = ValueAnimator.ofFloat(cameraYOffset, 0f);
                        cameraUpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                cameraFab.setTranslationY((float) animation.getAnimatedValue());
                                cameraLabel.setTranslationY((float) animation.getAnimatedValue());
                            }
                        });
                        cameraDownAnimator = ValueAnimator.ofFloat(0f, cameraYOffset);
                        cameraDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                cameraFab.setTranslationY((float) animation.getAnimatedValue());
                                cameraLabel.setTranslationY((float) animation.getAnimatedValue());
                            }
                        });
                        helpUpAnimator = ValueAnimator.ofFloat(helpYOffset, 0f);
                        helpUpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                helpFab.setTranslationY((float) animation.getAnimatedValue());
                                helpLabel.setTranslationY((float) animation.getAnimatedValue());
                            }
                        });
                        helpDownAnimator = ValueAnimator.ofFloat(0f, helpYOffset);
                        helpDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                helpFab.setTranslationY((float) animation.getAnimatedValue());
                                helpLabel.setTranslationY((float) animation.getAnimatedValue());
                            }
                        });
                    }
                    final OvershootInterpolator interpolator = new OvershootInterpolator();
                    ViewCompat.animate(menuFAB).
                            rotation(135f).
                            withLayer().
                            setDuration(300).
                            setInterpolator(interpolator).
                            start();
                    rotatedFab = true;
                    alphaInvisibleAnimator.cancel();
                    alphaVisibleAnimator.start();
                    cameraDownAnimator.cancel();
                    cameraUpAnimator.start();
                    helpDownAnimator.cancel();
                    helpUpAnimator.start();
                }
            }
        });
        cameraFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });
        helpFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VOICE_COMMAND)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        List<ScoreItem> tempData;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            tempData = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                tempData.add(new ScoreItem("Score " + i, i));
            }
        }

        @Override
        public Fragment getItem(int position) {
            PersonalScoreFragment fragment = new PersonalScoreFragment();
            fragment.setDataset(tempData);
            if (position == 0) {
                fragment.setTitle("My Items");
            }
            if (position == 1) {
                fragment.setTitle("Leaderboard");
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
