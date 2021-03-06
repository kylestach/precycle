package com.example.ananth.recyclekiosk;

import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.observers.DisposableObserver;

public class MainActivity extends AppCompatActivity {
    private ProgressBar xpProgressBar;
    private ValueAnimator animator;
    private boolean rotatedFab;
    private TextView xpTextView, levelTextView, levelUpTextView;
    private ScoreAdapter leaderboard, breakdown;
    private View blueView;
    private ValueAnimator cameraUpAnimator,
            cameraDownAnimator,
            helpUpAnimator,
            helpDownAnimator,
            signUpAnimator,
            signDownAnimator;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;

    @Override
    protected void onResume() {
        super.onResume();
        levelTextView.setText(DataManager.user.getLevel() + "");
        xpTextView.setText(NumberFormat.getNumberInstance(Locale.US).format(DataManager.user.getHasPoints()) + "/" + NumberFormat.getNumberInstance(Locale.US).format(DataManager.user.getLevelPoints()));
        animator = ValueAnimator.ofInt(0, (int) (100 * ((double) DataManager.user.getHasPoints()) / DataManager.user.getLevelPoints()));
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                xpProgressBar.setProgress((int) animation.getAnimatedValue());
            }
        });
        //xpProgressBar.setProgress(50);
        breakdown.updateData(DataManager.user.getScoreItems());
        NetworkManager.getLeaderboard();
        xpProgressBar.setProgress(0);
        animator.setStartDelay(1000);
        animator.start();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        if(DataManager.levelUp){
            animateLevelUp();
            DataManager.levelUp = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        //NetworkManager.getLeaderboard();
        TextView name = findViewById(R.id.nameTextView);
        name.setText(DataManager.user.getName());
        getSupportActionBar().hide();
        levelTextView = findViewById(R.id.levelTextView);
        levelTextView.setText(DataManager.user.getLevel() + "");
        levelUpTextView = findViewById(R.id.levelUpTextView);
        levelUpTextView.setVisibility(View.GONE);
        xpTextView = findViewById(R.id.xpTextView);
        xpTextView.setText(NumberFormat.getNumberInstance(Locale.US).format(DataManager.user.getHasPoints()) + "/" + NumberFormat.getNumberInstance(Locale.US).format(DataManager.user.getLevelPoints()));
        xpProgressBar = findViewById(R.id.xpProgressBar);
        xpProgressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.buzzYellow), PorterDuff.Mode.SRC_IN);
        blueView = findViewById(R.id.blueView);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        intentHandler(getIntent());

        RecyclerView recyclerView = findViewById(R.id.mainViewPager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        IndefinitePagerIndicator indefinitePagerIndicator = findViewById(R.id.recyclerviewPagerIndicator);
        indefinitePagerIndicator.attachToRecyclerView(recyclerView);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        //pager.setAdapter(adapter);
        List<ScoreItem> tempData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            tempData.add(new ScoreItem("Score " + i, i));
        }
        final List<ScorePageModel> modelList = new ArrayList<>();
        breakdown = new ScoreAdapter(DataManager.user.getScoreItems(), MainActivity.this);
        modelList.add(new ScorePageModel(breakdown, "Breakdown"));
        leaderboard = new ScoreAdapter(new ArrayList<ScoreItem>(), MainActivity.this);
        modelList.add(new ScorePageModel(leaderboard, "Leaderboard"));
        NetworkManager.leaderboardInfoSubject.subscribe(new DisposableObserver<List<ScoreItem>>() {
            @Override
            public void onNext(final List<ScoreItem> scoreItems) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        leaderboard.setData(scoreItems);
                    }
                });
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        NetworkManager.leaderboardErrorSubject.subscribe(new DisposableObserver<List<ScoreItem>>() {
            @Override
            public void onNext(List<ScoreItem> scoreItems) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        recyclerView.setAdapter(new ScoreListAdapter(modelList, MainActivity.this));
        //recyclerView.setOverScrollEnabled(true);
        rotatedFab = false;
        final FloatingActionButton menuFAB = findViewById(R.id.fabMain);
        menuFAB.setSize(FloatingActionButton.SIZE_NORMAL);
        final View grayView = findViewById(R.id.grayView);
        grayView.setAlpha(0f);
        grayView.setVisibility(View.GONE);
        final FloatingActionButton cameraFab = findViewById(R.id.fabCamera);
        final FloatingActionButton helpFab = findViewById(R.id.fabHelp);
        final FloatingActionButton signOutFab = findViewById(R.id.fabSignOut);
        final TextView cameraLabel = findViewById(R.id.scanLabel);
        final TextView helpLabel = findViewById(R.id.chatLabel);
        final TextView signOutLabel = findViewById(R.id.signOutLabel);
        cameraFab.setAlpha(0f);
        helpFab.setAlpha(0f);
        signOutFab.setAlpha(0f);
        cameraLabel.setAlpha(0f);
        helpLabel.setAlpha(0f);
        signOutLabel.setAlpha(0f);
        final ValueAnimator alphaVisibleAnimator = ValueAnimator.ofFloat(0f, 1f);
        alphaVisibleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                cameraFab.setAlpha((float) animation.getAnimatedValue());
                helpFab.setAlpha((float) animation.getAnimatedValue());
                signOutFab.setAlpha((float) animation.getAnimatedValue());
                cameraLabel.setAlpha((float) animation.getAnimatedValue());
                helpLabel.setAlpha((float) animation.getAnimatedValue());
                signOutLabel.setAlpha((float) animation.getAnimatedValue());
                if ((float) animation.getAnimatedValue() == 0) {
                    grayView.setVisibility(View.VISIBLE);
                }
                grayView.setAlpha(((float) animation.getAnimatedValue()) / 2);
            }
        });
        final ValueAnimator alphaInvisibleAnimator = ValueAnimator.ofFloat(1f, 0f);
        alphaInvisibleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                cameraFab.setAlpha((float) animation.getAnimatedValue());
                helpFab.setAlpha((float) animation.getAnimatedValue());
                signOutFab.setAlpha((float) animation.getAnimatedValue());
                cameraLabel.setAlpha((float) animation.getAnimatedValue());
                helpLabel.setAlpha((float) animation.getAnimatedValue());
                signOutLabel.setAlpha((float) animation.getAnimatedValue());
                grayView.setAlpha(((float) animation.getAnimatedValue()) / 2);
                if ((float) animation.getAnimatedValue() == 0) {
                    grayView.setVisibility(View.GONE);
                }
            }
        });
        grayView.setOnClickListener(new View.OnClickListener() {
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
                    signUpAnimator.cancel();
                    signDownAnimator.start();
                }
            }
        });
        menuFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rotatedFab) {
                    //animateLevelUp();
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
                    signUpAnimator.cancel();
                    signDownAnimator.start();
                } else {
                    if (cameraUpAnimator == null) {
                        float cameraYOffset = menuFAB.getY() - cameraFab.getY();
                        cameraFab.setTranslationY(cameraYOffset);
                        float helpYOffset = menuFAB.getY() - helpFab.getY();
                        helpFab.setTranslationY(helpYOffset);
                        float signYOffset = menuFAB.getY() - signOutFab.getY();
                        signOutFab.setTranslationY(signYOffset);
                        cameraFab.setAlpha(0f);
                        helpFab.setAlpha(0f);
                        signOutFab.setAlpha(0f);
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
                        signUpAnimator = ValueAnimator.ofFloat(signYOffset, 0f);
                        signUpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                signOutFab.setTranslationY((float) animation.getAnimatedValue());
                                signOutLabel.setTranslationY((float) animation.getAnimatedValue());
                            }
                        });
                        signDownAnimator = ValueAnimator.ofFloat(0f, signYOffset);
                        signDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                signOutFab.setTranslationY((float) animation.getAnimatedValue());
                                signOutLabel.setTranslationY((float) animation.getAnimatedValue());
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
                    signDownAnimator.cancel();
                    signUpAnimator.start();
                }
            }
        });
        cameraFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                signUpAnimator.cancel();
                signDownAnimator.start();
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });
        helpFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                signUpAnimator.cancel();
                signDownAnimator.start();
                startActivity(new Intent(Intent.ACTION_VOICE_COMMAND)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        signOutFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("userID");
                editor.commit();
                DataManager.user = null;
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        intentHandler(intent);
    }

    private void intentHandler(Intent data) {

        //Get the tag from the given intent
        Tag tag = data.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            Ndef ndef = Ndef.get(tag);

            Uri uri = null;

            try {
                ndef.connect();
                NdefMessage message = ndef.getNdefMessage();
                for (int i = 0; i < message.getRecords().length; i++) {
                    if (message.getRecords()[i] != null) {
                        String msg = message.getRecords()[i].toString();
                        String payload = new String(message.getRecords()[i].getPayload());
                        int kioskID = Integer.parseInt(payload.substring(payload.indexOf("=") + 1));
                        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                        intent.putExtra("kiosk", kioskID);
                        startActivity(intent);
                        Log.v("tag", "toString: " + msg + "; payload:" + payload);
                    }
                }
                ndef.close();
            } catch (FormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (uri != null) {


                Log.v("tag", "tag: " + uri.toString());
            }
            //Toast.makeText(this, "nfc enabled: "+tag.toString(),Toast.LENGTH_LONG).show();
            Log.v("tag", "tag: " + tag.toString());
        } else {
            Log.v("tag", "no tag");
        }
    }

    private void animateLevelUp() {
        int mWidth = this.getResources().getDisplayMetrics().widthPixels/2;
        int mHeight = this.getResources().getDisplayMetrics().heightPixels/2;
        float xOffset = mWidth - levelTextView.getX()-levelTextView.getWidth()/2;
        float yOffset = mHeight - levelTextView.getY()-2*levelTextView.getHeight();
        blueView.setVisibility(View.VISIBLE);
        blueView.setAlpha(0f);
        levelUpTextView.setVisibility(View.VISIBLE);
        levelUpTextView.setAlpha(0f);
        final ValueAnimator blueIn = ValueAnimator.ofFloat(0f,1f);
        blueIn.setDuration(1000);
        blueIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                blueView.setAlpha((float)animation.getAnimatedValue());
                levelUpTextView.setAlpha((float)animation.getAnimatedValue());
            }
        });
        final ValueAnimator blueOut = ValueAnimator.ofFloat(1f,0f);
        blueOut.setDuration(1000);
        blueOut.setStartDelay(2000);
        blueOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                blueView.setAlpha((float)animation.getAnimatedValue());
                levelUpTextView.setAlpha((float)animation.getAnimatedValue());
            }
        });
        ValueAnimator xAnimate = ValueAnimator.ofFloat(0, xOffset);
        ValueAnimator yAnimate = ValueAnimator.ofFloat(0, yOffset);
        xAnimate.setDuration(1000);
        yAnimate.setDuration(1000);
        xAnimate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                levelTextView.setTranslationX((float) animation.getAnimatedValue());
            }
        });
        yAnimate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                levelTextView.setTranslationY((float) animation.getAnimatedValue());
            }
        });
        ValueAnimator xAnimate2 = ValueAnimator.ofFloat(xOffset, 0);
        ValueAnimator yAnimate2 = ValueAnimator.ofFloat(yOffset, 0);
        xAnimate2.setDuration(1000);
        yAnimate2.setDuration(1000);
        xAnimate2.setStartDelay(2000);
        yAnimate2.setStartDelay(2000);
        xAnimate2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                levelTextView.setTranslationX((float) animation.getAnimatedValue());
            }
        });
        yAnimate2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                levelTextView.setTranslationY((float) animation.getAnimatedValue());
            }
        });
        xAnimate.start();
        yAnimate.start();
        xAnimate2.start();
        yAnimate2.start();
        blueIn.start();
        blueOut.start();
    }
}
