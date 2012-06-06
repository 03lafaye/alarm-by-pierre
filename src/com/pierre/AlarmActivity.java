package com.pierre;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import java.util.Calendar;

public class AlarmActivity extends Activity
{
    private AnalogClockTimePicker mAnalogClockTimePicker;
    private boolean mEnableAlarm;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        mAnalogClockTimePicker = (AnalogClockTimePicker) findViewById(R.id.timePicker);
    }

    public void saveAndQuit(View v) {
        mEnableAlarm = true;
        setupAlarms();
        moveTaskToBack(true);
    }

    private void setupAlarms() {
        if (!mEnableAlarm)
            return;

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(this, AlarmNotificationReceiver.class), 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, mAnalogClockTimePicker.getHour());
        calendar.set(Calendar.MINUTE, mAnalogClockTimePicker.getMinute());

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
