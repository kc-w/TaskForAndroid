package com.example.taskforandroid.KeepLive.backgroundMusic;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.example.taskforandroid.R;

public class MusicService extends Service {

    private final String TAG = "MusicService";

    private MediaPlayer mMediaPlayer;

    private AudioManager mAudioManager;

    private AudioManager.OnAudioFocusChangeListener mAudioFocusChange = new
            AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            Log.e(TAG, "获得了Audio Focus");
                            try {
                                startPlayMusic();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            Log.e(TAG, "失去了Audio Focus");
//                            startPlayMusic();
//                            mAudioManager.abandonAudioFocus(mAudioFocusChange);
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            Log.e(TAG, "暂时失去Audio Focus，停止播放并会很快再次获得");
                            startPlayMusic();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            Log.e(TAG, "暂时失去AudioFocus，但是可以继续播放，不过要在降低音量");
                            startPlayMusic();
                            break;
                    }
                }
            };

    public MusicService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //音标处理
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (mAudioManager != null)
            mAudioManager.requestAudioFocus(mAudioFocusChange, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw);
        mMediaPlayer.setLooping(true);

        startPlayMusic();

        return START_STICKY;
    }


    private void startPlayMusic() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            Log.e(TAG, "启动后台播放音乐");
            mMediaPlayer.start();
        }
    }

    private void stopPlayMusic() {
        if (mMediaPlayer != null) {
            Log.e(TAG, "关闭后台播放音乐");
            mMediaPlayer.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayMusic();
        Log.e(TAG, "停止服务");
    }


}