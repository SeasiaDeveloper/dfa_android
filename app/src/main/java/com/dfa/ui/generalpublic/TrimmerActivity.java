package com.dfa.ui.generalpublic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.dfa.R;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnK4LVideoListener;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

public class TrimmerActivity extends AppCompatActivity implements OnTrimVideoListener, OnK4LVideoListener {

    private K4LVideoTrimmer mVideoTrimmer;
    private ProgressDialog mProgressDialog1;
    TextView toolTitle;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);
//        toolTitle = findViewById(R.id.toolbat_title);
//        btnBack = findViewById(R.id.iv_back);
//        toolTitle.setText("Video Trimming");
//        btnBack.setVisibility(View.GONE);

        Intent extraIntent = getIntent();
        String path = "";

        if (extraIntent != null) {
            path = extraIntent.getStringExtra("path");
        }

        //setting progressbar
        mProgressDialog1 = new ProgressDialog(this);
        mProgressDialog1.setCancelable(false);
        mProgressDialog1.setMessage(getString(R.string.trimming_progress));

        mVideoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
        if (mVideoTrimmer != null && path != null) {
            mVideoTrimmer.setMaxDuration(59);
            mVideoTrimmer.setOnTrimVideoListener(this);
            mVideoTrimmer.setOnK4LVideoListener(this);
            mVideoTrimmer.setVideoURI(Uri.parse(path));
            mVideoTrimmer.setVideoInformationVisibility(true);
        }
    }

    @Override
    public void onTrimStarted() {
        mProgressDialog1.show();
    }

    @Override
    public void getResult(final Uri uri) {
        mProgressDialog1.cancel();

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(TrimmerActivity.this, getString(R.string.video_saved_at, uri.getPath()), Toast.LENGTH_SHORT).show();
//            }
//        });
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        intent.setDataAndType(uri, "video/mp4");
//        startActivity(intent);

        setResult(Activity.RESULT_OK, getIntent().putExtra("filePath", uri.toString()));

        finish();
    }

    @Override
    public void cancelAction() {
        mProgressDialog1.cancel();
        mVideoTrimmer.destroy();
        finish();
    }

    @Override
    public void onError(final String message) {
        mProgressDialog1.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TrimmerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVideoPrepared() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ///Toast.makeText(TrimmerActivity.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
