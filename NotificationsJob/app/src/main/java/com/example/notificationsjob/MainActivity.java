package com.example.notificationsjob;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int JOB_ID = 0;
    private Button btnScheduleJob, btnCancelJob;
    private RadioGroup networkOpts;
    private JobScheduler jobScheduler;
    private Switch swIdle, swCharge;
    private SeekBar sbDead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView seekBarProgress = findViewById(R.id.tvProgress);

        btnScheduleJob = findViewById(R.id.btnScheduleJob);
        btnCancelJob = findViewById(R.id.btnCancelJob);
        networkOpts = findViewById(R.id.networkOptions);
        swIdle = findViewById(R.id.swIdle);
        swCharge = findViewById(R.id.swCharging);
        sbDead = findViewById(R.id.sbDeadline);

        btnScheduleJob.setOnClickListener(this);
        btnCancelJob.setOnClickListener(this);

        sbDead.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 0){
                    seekBarProgress.setText(progress + " s");
                }else {
                    seekBarProgress.setText("No Seteado");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnScheduleJob:
                int selectedNetworkID = networkOpts.getCheckedRadioButtonId();
                int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                int seekBarInteger = sbDead.getProgress();
                boolean seekBarSet = seekBarInteger > 0;

                switch (selectedNetworkID) {
                    case R.id.noNetwork:
                        selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                        break;
                    case R.id.anyNetwork:
                        selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                        break;
                    case R.id.wifiNetwork:
                        selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
                        break;
                }

                ComponentName serviceName = new ComponentName(getPackageName(),
                        NotificationJobService.class.getName());
                JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName)
                        .setRequiredNetworkType(selectedNetworkOption)
                        .setRequiresDeviceIdle(swIdle.isChecked())
                        .setRequiresCharging(swCharge.isChecked());

                if (seekBarSet) {
                    builder.setOverrideDeadline(seekBarInteger * 1000);
                }

                boolean constraintSet = (selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE)
                        || swCharge.isChecked() || swIdle.isChecked() || seekBarSet;

                if (constraintSet) {
                    JobInfo myJobInfo = builder.build();
                    jobScheduler.schedule(myJobInfo);
                    Toast.makeText(this, "Job Scheduled, job will run when the constraints are met.", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "Please set at least one constraint", Toast.LENGTH_SHORT).show();
                break;


            case R.id.btnCancelJob:
                if (jobScheduler!=null){
                    jobScheduler.cancelAll();
                    jobScheduler = null;
                    Toast.makeText(this, "Jobs cancelled", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
