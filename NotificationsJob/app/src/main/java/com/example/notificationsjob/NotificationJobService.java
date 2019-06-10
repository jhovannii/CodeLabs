package com.example.notificationsjob;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

public class NotificationJobService extends JobService {

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    NotificationManager mNotifyManager;

    @Override
    public boolean onStartJob(JobParameters params) {
        createNotificationChannel();
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyJob = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("Job Service")
                .setContentText("Job Running")
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_job_running)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        mNotifyManager.notify(0, notifyJob.build());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    "Job Service notification",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notifications from Job Service");

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }
}
