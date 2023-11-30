package es.MiHipotecaApp.TFG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(EuriborMens.class).build();
        WorkManager.getInstance(context).enqueue(workRequest);
    }
}
