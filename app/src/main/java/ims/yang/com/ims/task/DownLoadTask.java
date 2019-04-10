package ims.yang.com.ims.task;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import ims.yang.com.ims.listener.DownLoadListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author yangchen
 * on 2019/4/9 11:36
 */
public class DownLoadTask extends AsyncTask<String, Integer, Integer> {
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;

    private DownLoadListener listener;
    private boolean isCanceled = false;
    private boolean isPaused = false;
    private int lastProgress;

    public DownLoadTask(DownLoadListener downLoadListener) {
        this.listener = downLoadListener;

    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;
        long downloadLenth = 0;
        String downloadUrl = params[0];
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        file = new File(directory + fileName);
        if (file.exists()) {
            downloadLenth = file.length();
        }
        long contentLength = file.length();
        if (contentLength == 0) {
            return TYPE_FAILED;
        } else if (contentLength == downloadLenth) {
            //下载完成
            return TYPE_SUCCESS;
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("RANGE", "bytes=" + downloadLenth + "-")
                .url(downloadUrl)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (null != response) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadLenth);
                byte[] bytes = new byte[1024];
                int total = 0;
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    if (isCanceled) {
                        return TYPE_CANCELED;
                    } else if (isPaused) {
                        return TYPE_PAUSED;
                    } else {
                        total += len;
                        savedFile.write(bytes, 0, len);
                        //计算已下载的百分比
                        int progress = (int) ((total + downloadLenth) * 100 /
                                contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
                if (isCanceled && file != null) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progerss = values[0];
        if (progerss > lastProgress) {
            listener.onProgress(progerss);
            lastProgress = progerss;
        }
    }

    @Override
    protected void onPostExecute(Integer status) {
        switch (status) {
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            default:
                break;
        }
    }

    public void pauseDownLoad(){
        isPaused = true;
    }

    public void cancelDownload(){
        isCanceled = true;
    }

    private long getContentLength(String downloadUrl) throws  IOException{
        OkHttpClient client = new OkHttpClient();
        Request request =new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()){
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }
}

