package com.example.chronos;
import static android.app.Notification.EXTRA_PROGRESS;
import static android.content.Intent.getIntent;
import static android.content.Intent.getIntentOld;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.ProgressBar;

import androidx.core.app.NotificationCompat;

public class TimerService extends Service {

    private static final String CHANNEL_ID = "TimerChannel";
    public static final String BROADCAST_ACTION = "com.example.timerapp.PROGRESS_UPDATE";
    public static final String EXTRA_PROGRESS = "progress";
    ProgressBar p;
    private static final int NOTIFICATION_ID = 1;
    long last;
    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer,initial;

    private long totalTime; // 60 seconds

    private void sendProgressUpdate(int progress) {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(EXTRA_PROGRESS, progress);
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        if (intent != null && intent.hasExtra("duration")) {
            totalTime = intent.getIntExtra("duration", 0) * 1000L; // Convert to milliseconds
        } else {
            totalTime = 60000;
        }
        countDownTimer = new CountDownTimer(totalTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                initalsong();
                int progress = (int) ((totalTime - millisUntilFinished) * 100 / totalTime);
                sendProgressUpdate(progress);
                last=millisUntilFinished;
            }

            @Override
            public void onFinish() {
                initial.stop();
               // int progress = (int) ((totalTime - last) * 100 / totalTime);
                sendProgressUpdate(100);

                playRingtone();
            }
        };
        countDownTimer.start();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(NOTIFICATION_ID, createNotification());



        mediaPlayer = MediaPlayer.create(this, R.raw.timer_after); // Load the ringtone
        initial = MediaPlayer.create(this, R.raw.countdown);


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (initial != null) {
            initial.stop();
            initial.release();
        }
    }

    private Notification createNotification() {
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Timer Service")
                .setContentText("Timer is running...")
                .setContentIntent(pendingIntent)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Timer Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void playRingtone() {
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true); // Loop the ringtone until stopped
            mediaPlayer.start();
        }
    }
    private void initalsong() {
        if (initial != null) {
            initial.setLooping(true); // Loop the ringtone until stopped
            initial.start();
        }
    }
}
