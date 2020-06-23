package com.dfa.ui.generalpublic;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.dfa.R;
import com.vaibhavlakhera.circularprogressview.CircularProgressView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class DownloadTask {
    private static final String TAG = "Download Task";
    private Context context;

    private String downloadUrl = "", downloadFileName = "";
    private ProgressDialog progressDialog;
    private Dialog dialog;
    private CircularProgressView circleProgress;
    private VideoPlayerActivity playVideo;
    private int pStatus = 0;
    public static DownloadingTask task;

    public DownloadTask(Context context, String downloadUrl, String documentId, VideoPlayerActivity play) {
        this.context = context;
        this.playVideo = play;


        this.downloadUrl = downloadUrl;

        downloadFileName = downloadUrl.substring(downloadUrl.lastIndexOf('/'), downloadUrl.length());//Create file name by picking download file name from URL
        downloadFileName = documentId;
        Log.e(TAG, downloadFileName);

        //Start Downloading Task
        task= (DownloadingTask) new DownloadingTask().execute();
    }

    public class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = new ProgressDialog(context);
//            progressDialog.setMessage("Downloading...");
//            progressDialog.setCancelable(false);
//            progressDialog.show();

            try {
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.circle_download_progress);
                dialog.setCanceledOnTouchOutside(false);

                circleProgress = dialog.findViewById(R.id.progressView);
                circleProgress.setProgress(1,true);
                dialog.show();

                dialog.setOnKeyListener(new Dialog.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            playVideo.finish();
                        }
                        return false;
                    }

                });

            } catch (Exception e) {

            }


        }


        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    // progressDialog.dismiss();
                    dialog.dismiss();
                    playVideo.play();

                } else {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 3000);

                    Log.e(TAG, "Download Failed");

                }
            } catch (Exception e) {
                e.printStackTrace();

                //Change button text if exception occurs

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 3000);
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }


            super.onPostExecute(result);
        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection
                int lenghtOfFile = c.getContentLength();

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }


                //Get File if SD card is present
//                if (new CheckForSDCard().isSDCardPresent()) {
//
//                    apkStorage = new File(Environment.getExternalStorageDirectory() + "/" + "CodePlayon");
//                } else
//                    Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                //If File is not present create directory

                apkStorage = new File(Environment.getExternalStorageDirectory(), "DFA");

                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }

                outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection
                long total = 0;
                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    total += len1;
                    fos.write(buffer, 0, len1);//Write new file
                    Log.d(TAG, "checking the percentage " + (int) ((total * 100) / lenghtOfFile));
                    setPercentage((int) ((total * 100) / lenghtOfFile));
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {
                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }
            return null;
        }

        public void setPercentage(int percentage) {

            playVideo.setLoader(circleProgress, percentage,dialog);

        }
    }
}
