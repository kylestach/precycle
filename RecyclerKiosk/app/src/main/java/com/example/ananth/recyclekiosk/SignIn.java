package com.example.ananth.recyclekiosk;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import io.reactivex.observers.DisposableObserver;

public class SignIn extends AppCompatActivity {
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private ProgressBar signInProgress;
    private TextView signInTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_layout);
        final SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String id = preferences.getString("userID", null);
        signInTextView = findViewById(R.id.scanTextView);
        signInProgress = findViewById(R.id.scanProgress);
        if (id != null) {
            signInProgress.setVisibility(View.VISIBLE);
            signInTextView.setVisibility(View.GONE);
            NetworkManager.getUserInfo("https://stats.dev.hack.gt/api/user/"+id);
        }
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        Log.v("nfc", "nfc enabled: " + mAdapter.isEnabled());
        Toast.makeText(this, "nfc enabled: " + mAdapter.isEnabled(), Toast.LENGTH_SHORT).show();
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        intentHandler(getIntent());
        NetworkManager.userBasicInfoSubject.subscribe(new DisposableObserver<User>() {
            @Override
            public void onNext(final User user) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        signInProgress.setVisibility(View.GONE);
                        signInTextView.setVisibility(View.VISIBLE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("userID",user.getId());
                        editor.apply();
                        DataManager.user = user;
                        startActivity(new Intent(SignIn.this, MainActivity.class));
                        finish();
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
        NetworkManager.userBasicInfoErrorSubject.subscribe(new DisposableObserver<User>() {
            @Override
            public void onNext(User user) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        signInProgress.setVisibility(View.GONE);
                        signInTextView.setVisibility(View.VISIBLE);
                        signInTextView.setText("Please try again!");
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
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        intentHandler(intent);
    }

    private void intentHandler(Intent data) {
        if(NetworkManager.retrievingUser){
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
                    Uri uri2 = message.getRecords()[i].toUri();
                    if (uri2 != null) {
                        uri = uri2;
                    }
                }
                ndef.close();
            } catch (FormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (uri != null) {
                //Toast.makeText(this, "nfc enabled: " + uri.toString(), Toast.LENGTH_LONG).show();
                signInProgress.setVisibility(View.VISIBLE);
                signInTextView.setVisibility(View.GONE);
                NetworkManager.getUserInfo("https://stats.dev.hack.gt/api/user/"+uri.toString().substring(uri.toString()
                        .indexOf("user=")+5));
                Log.v("tag", "tag: " + uri.toString());
            }
            //Toast.makeText(this, "nfc enabled: "+tag.toString(),Toast.LENGTH_LONG).show();
            Log.v("tag", "tag: " + tag.toString());
        } else {
            Log.v("tag", "no tag");
        }
    }
}
