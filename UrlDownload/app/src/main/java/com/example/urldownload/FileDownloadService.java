package com.example.urldownload;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileDownloadService extends Service {
    IBinder mBinder = new LocalBinder();
    Context context = this;
    int notificationId = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void Download(String url)
    {
        DownloadFileFromURL task = new DownloadFileFromURL();
        task.execute(url);
    }

    class LocalBinder extends Binder {
        FileDownloadService getServerInstance() {
            return FileDownloadService.this;
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, Integer, Boolean> {
        NotificationManager mNotifyManager;
        NotificationCompat.Builder mBuilder;
        String fileName;
        int notification;

        protected void onPreExecute(){
            super.onPreExecute();
            mNotifyManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(context, "my_channel_01")
                    .setContentTitle("File Download")
                    .setContentText("Download in progress")
                    .setSmallIcon(R.drawable.ic_launcher_foreground);
            notification = notificationId;
            mNotifyManager.notify(notificationId++, mBuilder.build());
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            int count;
            try {
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);
                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                fileName = urls[0].substring(urls[0].lastIndexOf('/') + 1);

                fileName = timestamp + "_" + fileName;

                mBuilder.setContentTitle("File Download: " + fileName);

                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName);

                byte data[] = new byte[1024];

                long total = 0;
                int lengthOfFile = connection.getContentLength();
                while ((count = input.read(data)) != -1) {
                    total += count;

                    publishProgress((int) ((total*100)/lengthOfFile));
                    output.write(data, 0, count);
                }

                output.flush();

                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                return false;
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            mBuilder.setProgress(100, progress[0], false);
            mNotifyManager.notify(notification, mBuilder.build());
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mBuilder.setContentText("Download complete");
            mBuilder.setProgress(0, 0,false);
            mNotifyManager.notify(notification, mBuilder.build());
        }
    }
}
