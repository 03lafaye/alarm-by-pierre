package com.pierre;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;

import java.io.IOException;

public class WakeUpActivity extends Activity implements MediaPlayer.OnCompletionListener {
    private MediaPlayer mMediaPlayer;
    private boolean mWokeUp;
    private static final String LOGTAG = "WakeUp";
    private int mAudioVolume;
    private AudioManager mAudioManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mWokeUp)
            return;

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.wake_up);

        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

//        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
//                mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(alert == null){
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if(alert == null)
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(this, alert);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mWokeUp = true;
    }

    @Override
    protected void onPause() {
        onCompletion(mMediaPlayer);
        super.onPause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mMediaPlayer == null) {
            finish();
            return true;
        }

        mMediaPlayer.stop();
        mMediaPlayer.reset();

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            Log.v(LOGTAG, "onTouchEvent playing sleep mp3");

            mAudioVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

            Uri mp3 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sleep);
            mMediaPlayer.setDataSource(this, mp3);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(this);
        } catch (IOException e) {
            Log.v(LOGTAG, "onTouchEvent didn't work");
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.v(LOGTAG, "onCompletion shutting down");
        if (mMediaPlayer != null) {
            mediaPlayer.stop();
            mMediaPlayer = null;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioVolume, 0);
        }
        finish();
    }
}