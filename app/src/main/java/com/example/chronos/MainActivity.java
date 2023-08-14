package com.example.chronos;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ProgressBar p; private BroadcastReceiver receiver;
    private TimePicker timePicker;
    private NumberPicker secondsPicker;
    int totalSeconds=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));

        setContentView(R.layout.activity_main);

        p=findViewById(R.id.progressBar);



        timePicker = findViewById(R.id.timePicker);
        secondsPicker = findViewById(R.id.secondsPicker);
        secondsPicker.setMaxValue(59);
        timePicker.setIs24HourView(true);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(TimerService.BROADCAST_ACTION)) {
                    int progress = intent.getIntExtra(TimerService.EXTRA_PROGRESS, 0);

                    p.setProgress(progress);
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(TimerService.BROADCAST_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }



    public void startTimerService(View view) {
        int selectedHour = timePicker.getHour();
        int selectedMinute = timePicker.getMinute();
        int selectedSeconds = secondsPicker.getValue();

        totalSeconds = (selectedHour * 3600) + (selectedMinute * 60) + selectedSeconds;
        Intent serviceIntent = new Intent(this, TimerService.class);
        serviceIntent.putExtra("duration",totalSeconds);
        startService(serviceIntent);
        p.setProgress(0);
    }

    public void stopTimerService(View view) {
        Intent serviceIntent = new Intent(this, TimerService.class);
        stopService(serviceIntent);
    }
}
