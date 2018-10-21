package com.example.ananth.recyclekiosk;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    private CameraView cameraView;

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
                ConstraintLayout layout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.image_dialog_layout, null);
                cameraView.stop();
                //Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                String s = "";
                for (int i = 0; i < 10; i++) {
                    s+=jpeg[i]+" ";
                }
                Toast.makeText(CameraActivity.this,s,Toast.LENGTH_LONG).show();
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
                byte[] stuff = Base64.encode(jpeg, Base64.NO_PADDING | Base64.URL_SAFE);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }

    public CameraActivity getSelf() {
        return this;
    }
}
