package com.cloud.cms.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cloud.cms.R;
import com.cloud.cms.manager.DeviceManager;
import com.cloud.cms.manager.ProgramManager;
import com.cloud.cms.model.LoadItem;
import com.cloud.cms.model.ProgramItem;
import com.cloud.cms.util.FileUtil;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DownloadResource extends BaseActivity {

    static int timerTaskCount;
    public static String TAG = "DownloadResource";
    long restimestamp;
    public static String MessageId;
    public static ProgressBar progressBar;
    public static TextView textView_percent1;
    public static TextView textView_percent2;
    public int progress;
    public static int taskTotal;
    public static int taskCount;
    private static int tasknumber;
    Thread downloadThread;
    Runnable downloadRunnable;
    public static int taskListnumber=0;
    public static String lastTaskMessageId;
    public static List<ProgramItem> programItemList;
    public static MyDownload myDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_resource);
        init();
    }

    private void init(){
        Log.i("cycle", "DownloadResource.onCreate");
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        textView_percent1 = (TextView) findViewById(R.id.textView_percent1);
        textView_percent2 = (TextView) findViewById(R.id.textView_percent2);

        String publishMessageId = getIntent().getStringExtra("publishMessageId");
        String templatesId = getIntent().getStringExtra("templatesId");
        programItemList=ProgramManager.getProgramItemList();
        if (myDownload == null) {
            myDownload = new MyDownload();
            myDownload.start(programItemList, publishMessageId, templatesId);
        } else {
            myDownload.start(programItemList, publishMessageId, templatesId);
        }
    }

    public void startActivity() {
        taskListnumber--;
        Log.i(TAG, "startActivity taskListnumber="+taskListnumber);
        if (taskListnumber<1) {
            Intent intent = new Intent(this, CMSActivity.class);
            startActivity(intent);
            finish();
        }

    }

    Handler handler_ = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                textView_percent1.setText("资源更新正在下载,Loading..." + taskCount + "/" + taskTotal);
                textView_percent2.setText("当前下载进度：" + progress + "%");
                progressBar.setProgress(progress);
                preferenceManager.setResId(taskCount + "/" + taskTotal);
            }
            super.handleMessage(msg);
        };
    };


    public class MyDownload {

        public void start(final List<ProgramItem> programItemList, final String publishMessageId, final String templatesId) {
            preferenceManager.setPublishMessageId(templatesId,publishMessageId);
            new Thread(new Runnable() {
                public void run() {
                    taskListnumber++;
                    Log.i("mytest_hl03", "MyDownload publishMessageId="+publishMessageId+",taskListnumber="+taskListnumber);
                    downLoadRes(programItemList, publishMessageId, templatesId);
                }
            }).start();

        }

        public synchronized void downLoadRes(List<ProgramItem> programItemList, String publishMessageId,
                                             String templatesId) {
            MessageId = publishMessageId;
            taskTotal = 0;
            for (int i = 0; i < programItemList.size(); i++) {
                taskTotal += programItemList.get(i).getLoadItemList().size();
            }
            if (taskTotal == 0) {
                ProgramManager.sendfinishTask("0", "下载完成", publishMessageId,"0/0");
                preferenceManager.setIsProgramListCompleteDownload(templatesId + "", true);
                preferenceManager.setTemplatesId(templatesId);
                startActivity();
                return;
            }

            if (programItemList != null && programItemList.size() > 0) {
                boolean isDownload = startDownload(programItemList, publishMessageId, templatesId);
                onPostExecute(isDownload, templatesId);
            }
        }

        public boolean startDownload(List<ProgramItem> programItemList, String publishMessageId, String templatesId) {
            Log.i("connect", "urls.length=" + taskTotal);

            tasknumber = taskTotal;
            taskCount = 0;
            ProgramManager.sendfinishTask("99", "下载中", publishMessageId,taskCount+"/"+taskTotal);
            for (int i = 0; i < programItemList.size(); i++) {
                List<LoadItem> loadItemList = programItemList.get(i).getLoadItemList();
                for (int j = 0; j < loadItemList.size(); j++) {
                    taskCount++;
                    Log.i("connect", "doInBackground taskCount=" + taskCount);

                    try {
                        String dirName = loadItemList.get(j).path;
                        String urlStr = loadItemList.get(j).url;
                        Log.i("connect", "dirName=" + dirName);
                        Log.i("connect", "urlStr=" + urlStr);

                        String fileName = dirName.substring(dirName.lastIndexOf("/") + 1);

                        File file = new File(dirName);

                        if (dirName.endsWith(".html")) {
                            if (file.exists()) {
                                tasknumber--;
                            } else {
                                if (FileUtil.createFile(file)) {
                                    tasknumber--;
                                } else {
                                    return false;
                                }
                            }

                            continue;
                        }

                        if (file.exists()) {
                            if (file.isDirectory()) {
                                FileUtil.createFile(file);
                            }

                        }

                        URL url = new URL(urlStr);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");

                        long fileLength = 0;

                        long total = file.length();

                        Log.i("connect", "fileLength=" + fileLength + ",total=" + total);
                        if (fileLength == 0) {

                            int code1 = con.getResponseCode();
                            if (code1 == HttpURLConnection.HTTP_OK) {
                                fileLength = con.getContentLength();
                                total = 0;
                            } else {
                                return false;
                            }
                            Log.i("connect", "(fileLength=0)fileLength=" + fileLength);

                            Log.i("connect", "c file.exists()=" + file.exists());
                        } else {
                            con.setRequestProperty("Range", "bytes=" + total + "-" + fileLength);
                            Log.i("connect", "Range=" + "bytes=" + total + "-" + fileLength);
                        }
                        if (total >= fileLength) {
                            tasknumber--;
                            continue;
                        }

                        long availableInternalMemorySize=DeviceManager.getAvailableInternalMemorySize();

                        double fileMemory=fileLength/(1024*1024.0);
                        Log.i("mytest_hl05", "DownloadResource.availableInternalMemorySize=" + availableInternalMemorySize+"M,fileLength="+fileMemory+"M");
                        if(availableInternalMemorySize<50||availableInternalMemorySize<fileMemory){

                            Log.i("mytest_hl05", "availableInternalMemorySize<50||availableInternalMemorySize<fileLength)");
                            AlertDialog dialog_Memory =	new AlertDialog.Builder(DownloadResource.this).setMessage("可用存储空间不足，下载失败："+dirName)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    }).show();

                            Thread.sleep(5000);
                            if(dialog_Memory.isShowing()){
                                dialog_Memory.dismiss();
                            }


                            return false;

                        }

                        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                        raf.seek(total);
                        Log.i("connect", "total=" + total);
                        int code = con.getResponseCode();
                        Log.i("connect", "code=" + code);
                        if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_PARTIAL) {

                            Log.i("connect", "hhh");

                            InputStream is = con.getInputStream();

                            raf.seek(file.length());
                            byte[] bs = new byte[1024];
                            int len;
                            while ((len = is.read(bs)) != -1) {

                                total += len;
                                progress = (int) ((total * 100) / fileLength);
                                raf.write(bs, 0, len);

                                Message message = new Message();
                                message.what = 1;
                                handler_.sendMessage(message);
                            }
                            tasknumber--;
                            con.disconnect();
                            raf.close();
                            is.close();
                        } else {
                            Log.i("connect", "链接失败");
                            return false;
                        }

                    } catch (Exception e) {
                        ProgramManager.sendfinishTask("-1", e.toString(), publishMessageId,taskCount+"/"+taskTotal);
                        e.printStackTrace();
                        return false;
                    }finally {

                    }

                }

                if (tasknumber == 0) {
                    ProgramManager.sendfinishTask("0", "下载完成", publishMessageId,taskCount+"/"+taskTotal);
                }
                preferenceManager.setIsProgramCompleteDownload(templatesId + "",
                        programItemList.get(i).getProgramId() + "", true);

            }
            return true;
        }

        protected void onPostExecute(Boolean isComplete, String templatesId) {
            if (isComplete) {
                preferenceManager.setIsProgramListCompleteDownload(templatesId + "", isComplete);
                startActivity();
            } else {
                Log.i(TAG, "下载失败");
                taskListnumber--;
                finish();
                return;
            }

        }
    }



}
