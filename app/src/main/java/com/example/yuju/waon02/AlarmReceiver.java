package com.example.yuju.waon02;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean v = intent.getBooleanExtra("valid", true);
        if(v) {
            String label = intent.getStringExtra("name");
            if (label == null)
                label = "none";
            int hour = intent.getIntExtra("hour", 0);
            int min = intent.getIntExtra("min", 0);
            boolean vb = intent.getBooleanExtra("vibrate", false);
            int _location = intent.getIntExtra("location", 0);
            int weather = intent.getIntExtra("weather", 0);
            int _rain = intent.getIntExtra("rain", 0);
            int _snow = intent.getIntExtra("snow", 0);
            int id = intent.getIntExtra("id", -1);
            Intent alarmIntent = new Intent(context, RingAlarmActivity.class);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            alarmIntent.putExtra("label", label);
            alarmIntent.putExtra("hour", hour);
            alarmIntent.putExtra("min", min);
            alarmIntent.putExtra("vibrate", vb);
            alarmIntent.putExtra("valid", v);
            alarmIntent.putExtra("location", _location);
            alarmIntent.putExtra("weather", weather);
            alarmIntent.putExtra("rain", _rain);
            alarmIntent.putExtra("snow", _snow);
            alarmIntent.putExtra("id", id);
            context.startActivity(alarmIntent);
        }
        else{
            Toast.makeText(context, "Invalid Alarm", Toast.LENGTH_SHORT).show();
        }


    }
}
