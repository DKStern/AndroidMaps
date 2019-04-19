package com.example.urldownload;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ForkJoinPool;

public class MainActivity extends AppCompatActivity {
    FileDownloadService fileDownloadService;
    TextView text;
    Button button;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            fileDownloadService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FileDownloadService.LocalBinder mLocalBinder = (FileDownloadService.LocalBinder) service;
            fileDownloadService = mLocalBinder.getServerInstance();
        }
    };

    private static int PERMISSION_REQUEST_CODE = 123;

    public void requestMultiplePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET
                },
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED  ) {
                addButtonListener();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestMultiplePermissions();

        text = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        createChannel();
        addButtonListener();
    }

    private void createChannel() {
        String CHANNEL_ID = "my_channel_01";
        NotificationChannel channel;
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_LOW;
        channel = new NotificationChannel(CHANNEL_ID, "UrlDownload", importance);
        mNotificationManager.createNotificationChannel(channel);
    }

    private void addButtonListener() {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fileDownloadService.Download(text.getText().toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent mIntent = new Intent(this, FileDownloadService.class);
        bindService(mIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(fileDownloadService != null) {
            unbindService(serviceConnection);
        }
    }
}
