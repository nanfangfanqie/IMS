package ims.yang.com.ims.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;

import ims.yang.com.ims.R;
import ims.yang.com.ims.ui.MainActivity;
import ims.yang.com.ims.listener.DownLoadListener;
import ims.yang.com.ims.task.DownLoadTask;

public class DownLoadService extends Service {
    private DownLoadTask downLoadTask;
    private String downloadUrl;

    private DownLoadListener listener = new DownLoadListener() {

        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1, getNotification("下载中...", progress));
        }

        @Override
        public void onSuccess() {
            downLoadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("下载成功", -1));
            Toast.makeText(DownLoadService.this, "下载成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            downLoadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("下载失败", -1));
            Toast.makeText(DownLoadService.this, "下载失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downLoadTask = null;
            Toast.makeText(DownLoadService.this, "暂停", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downLoadTask = null;
            stopForeground(true);
            Toast.makeText(DownLoadService.this, "取消下载", Toast.LENGTH_SHORT).show();
        }
    };

    private DownloadBinder mBinder =  new DownloadBinder();

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.qipao);
        builder.setContentIntent(pi);
        if (progress > 0) {
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class DownloadBinder extends Binder {
        public void startDownload(String url) {
            if (downLoadTask == null) {
                downloadUrl = url;
                downLoadTask = new DownLoadTask(listener);
                downLoadTask.execute(downloadUrl);
                startForeground(1, getNotification("下载中...", 0));
                Toast.makeText(DownLoadService.this, "下载中...", Toast.LENGTH_SHORT).show();

            }
        }

        public void pauseDownload() {
            if (downLoadTask != null) {
                downLoadTask.pauseDownLoad();
            }
        }

        public void cancelDownLoad() {
            if (downLoadTask != null) {
                downLoadTask.cancelDownload();
            } else {
                if (downloadUrl != null) {
                    //取消下载需要删除文件，并关闭通知
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownLoadService.this, "Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
