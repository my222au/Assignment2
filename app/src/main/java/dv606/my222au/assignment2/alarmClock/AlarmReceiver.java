package dv606.my222au.assignment2.alarmClock;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName() ;

    @Override
    public void onReceive(Context ctx, Intent intent) {
       Intent intent1 = new Intent(ctx,AlarmActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

        Log.d(TAG, "onReceive:starting Alarm Activity");
        ctx.startActivity(intent1);
    }

}


