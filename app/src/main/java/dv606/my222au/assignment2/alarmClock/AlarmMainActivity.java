package dv606.my222au.assignment2.alarmClock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import dv606.my222au.assignment2.R;

public class AlarmMainActivity extends AppCompatActivity {
    private static final String TAG = AlarmMainActivity.class.getSimpleName();
    private TextView mCurrentTime;
    public static final String ALARM_BORADCAST = "project.my222au.alarmclock.ALARM_BROADCAST";
    private TextView mAlarmTime;
    private LinearLayout mLayout;
    private FloatingActionButton mFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mCurrentTime = (TextView) findViewById(R.id.timeLabel);
        mAlarmTime = (TextView) findViewById(R.id.alamTime);
        mLayout = (LinearLayout) findViewById(R.id.timelayout);
        mAlarmTime.setOnClickListener(new OnAlarmTimeClickListner());
        mLayout.setVisibility(View.INVISIBLE);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime();

            }


        });

        // uppdates the textview every  5 seconds
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                time();
                handler.postDelayed(this, 5000);
            }
        }, 10);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private Long setTime() {
        Calendar calender = Calendar.getInstance();
        final int hour = calender.get(Calendar.HOUR_OF_DAY);
        int minute = calender.get(Calendar.MINUTE);
        TimePickerDialog timepicker = new TimePickerDialog(AlarmMainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calender = Calendar.getInstance();
                calender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calender.set(Calendar.MINUTE, minute);
                calender.set(Calendar.SECOND, 00);
                mAlarmTime.setText(formatdiget(hourOfDay) + ":" + formatdiget(minute));
                mLayout.setVisibility(View.VISIBLE);
                setAlarm(calender.getTimeInMillis());
            }
        }, hour, minute, true);

        timepicker.show();


        return calender.getTimeInMillis();
    }
    /***
     * Time for the textview uppdates every 5 seconds
     */
    private void time() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        StringBuilder currentTime = new StringBuilder().append(formatdiget(hour)).append(":").
                append(formatdiget(minute)).append(":").
                append(formatdiget(seconds));

        mCurrentTime.setText(currentTime);


    }
    /**
     *  adds zero to the time. if it is 13:3 ---> 13:(0)3
     * @param time
     * @return
     */
    private String formatdiget(long time) {
        if (time > 9) {
            return String.valueOf(time);
        } else return "0" + String.valueOf(time);
    }

 // setting the alarm using  pendingintent and  AlarmManger
    private void setAlarm(long time) {
        Intent intent = new Intent(ALARM_BORADCAST);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(AlarmMainActivity.this, 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, time, alarmIntent);
        mFab.setVisibility(View.INVISIBLE);
        Log.d(TAG, "setAlarm: Alarm is set");


    }

    /*
    canceling the alarm using same boradcast intent
     */
    private void cancelAlarm() {
        Intent intent = new Intent(ALARM_BORADCAST);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(AlarmMainActivity.this, 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(alarmIntent);
        Log.d(TAG, "CancelAlarm: Alarm is Canceled");


    }

     // alertdialog if the user wants to change or cancel the alarm.
    private android.app.AlertDialog setup_alert(int title, int message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setNegativeButton(R.string.modify, new OnModifyClickListner());
        builder.setPositiveButton(R.string.cancel, new OnCancelClickListner());
        builder.setNeutralButton(R.string.abort, new OnAbortClickListner());
        return builder.create();
    }


    private class OnAlarmTimeClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            setup_alert(R.string.aler_title2, R.string.alert_msg2).show();

        }

    }

    private class OnModifyClickListner implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            cancelAlarm();
            setTime();
            mFab.setVisibility(View.INVISIBLE);
            dialog.dismiss();
        }
    }

    private class OnCancelClickListner implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            cancelAlarm();
            mLayout.setVisibility(View.INVISIBLE);
            mFab.setVisibility(View.VISIBLE);
            dialog.dismiss();

        }
    }

    private class OnAbortClickListner implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }
}
