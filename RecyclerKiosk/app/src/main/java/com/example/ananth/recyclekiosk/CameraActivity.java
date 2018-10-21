package com.example.ananth.recyclekiosk;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    private CameraView cameraView;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private ValueAnimator animator;
    private boolean shouldScan;
    private int kioskID = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        getSupportActionBar().hide();
        cameraView = findViewById(R.id.camera);
        cameraView.setLifecycleOwner(this);
        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
        cameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER);
        cameraView.mapGesture(Gesture.LONG_TAP, GestureAction.CAPTURE);
        shouldScan = true;
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        intentHandler(getIntent());
        ImageView takePictureImage = findViewById(R.id.cameraButton);
        takePictureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.capturePicture();
            }
        });
        takePictureImage.setClickable(true);

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);
                final ConstraintLayout layout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.image_dialog_layout, null);
                cameraView.stop();
                //Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                String s = "";
                for (int i = 0; i < 10; i++) {
                    s+=jpeg[i]+" ";
                }
                //Toast.makeText(CameraActivity.this,s,Toast.LENGTH_LONG).show();
                Log.v("data",s);
                ((ImageView)layout.findViewById(R.id.classifiedImage)).setVisibility(View.GONE);
                //((ImageView)layout.findViewById(R.id.classifiedImage)).setImageBitmap(bitmap);

                layout.findViewById(R.id.classificationText).setVisibility(View.GONE);
                layout.findViewById(R.id.classificationProgressBar).setVisibility(View.VISIBLE);

                new MaterialDialog.Builder(getSelf()).title("Classification").cancelable(false).canceledOnTouchOutside(false).positiveText("Finish").negativeText("Take Another").customView(layout.getRootView(), false).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.cancel();
                        dialog.dismiss();
                        cameraView.start();
                    }
                }).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                }).show();

                //byte[] stuff = Base64.encode(jpeg, Base64.NO_PADDING);
                NetworkManager.getClassification(jpeg, kioskID, new ClassificationCallback() {
                    @Override
                    public void onError(Exception e) {

                    }

                    @Override
                    public void onFinished(final String classification) {
                        getSelf().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                layout.findViewById(R.id.classificationText).setVisibility(View.VISIBLE);
                                if(kioskID!=-1) {
                                    layout.findViewById(R.id.pointsText).setVisibility(View.VISIBLE);
                                    layout.findViewById(R.id.pointsText).setAlpha(0f);
                                    ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                                    animator.setStartDelay(250);
                                    animator.setDuration(1000);
                                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            layout.findViewById(R.id.pointsText).setAlpha((float) animation.getAnimatedValue());
                                        }
                                    });
                                    animator.start();
                                }
                                else{
                                    layout.findViewById(R.id.pointsText).setVisibility(View.GONE);
                                }
                                ((TextView)layout.findViewById(R.id.classificationText)).setText(classification);
                                layout.findViewById(R.id.classificationProgressBar).setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        });
        final ConstraintLayout cameraLayout = findViewById(R.id.cameraLayout);
        final AppCompatButton skipButton = findViewById(R.id.skipButton);
        animator = ValueAnimator.ofFloat(0f,1f);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                cameraLayout.setVisibility(View.VISIBLE);
                cameraLayout.setAlpha(0f);
            }

            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                skipButton.setVisibility(View.GONE);
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                cameraLayout.setAlpha((float)animation.getAnimatedValue());
                skipButton.setAlpha(1f-(float)animation.getAnimatedValue());
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouldScan = false;
                animator.start();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
        mAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }

    public CameraActivity getSelf() {
        return this;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        intentHandler(intent);
    }

    private void intentHandler(Intent data) {
        if(!shouldScan){
            return;
        }
        //Get the tag from the given intent
        Tag tag = data.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            Ndef ndef = Ndef.get(tag);

            Uri uri = null;

            try {
                ndef.connect();
                NdefMessage message = ndef.getNdefMessage();
                for (int i = 0; i < message.getRecords().length; i++) {
                    if(message.getRecords()[i]!=null) {
                        String msg = message.getRecords()[i].toString();
                        String payload = new String(message.getRecords()[i].getPayload());
                        kioskID = Integer.parseInt(payload.substring(payload.indexOf("=")+1));
                        shouldScan = false;
                        animator.start();
                        Log.v("tag","toString: "+msg+"; payload:"+payload);
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
}
