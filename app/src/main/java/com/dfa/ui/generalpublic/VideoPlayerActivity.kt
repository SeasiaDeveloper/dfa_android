package com.dfa.ui.generalpublic

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dfa.R
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.vaibhavlakhera.circularprogressview.CircularProgressView
import kotlinx.android.synthetic.main.activity_video_player.*
import java.io.File

class VideoPlayerActivity : BaseActivity() {
    var mediaController: MediaController? = null
    var thumbNail = ""
    var documentId = ""
    var dialog1: Dialog? = null
    var percent = 0
    var task: DownloadTask? = null
    var mPath = ""
    var videoView: VideoView? = null
    var thumbImage: ImageView? = null
    var retry = 0


    override fun getLayout(): Int {
        return R.layout.activity_video_player
    }

    override fun setupUI() {

        (toolbarLayout as CenteredToolbar).title = getString(R.string.video_player)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_button)

        var btn = (toolbarLayout as CenteredToolbar)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }

        retry = 0
        askPermissionScanner()
    }


    fun downloadData() {
        videoView = findViewById(R.id.videoView)
        thumbImage = findViewById(R.id.image_view)
        val videoPath = intent.getStringExtra("videoPath")
        mPath = videoPath
        documentId = intent.getStringExtra("documentId")
        documentId = "VID_" + documentId
        if (intent.getStringExtra("thumbNail") != null) {
            // thumbNail = intent.getStringExtra("thumbNail")
        }
        try {
            val fileName =
                File(Environment.getExternalStorageDirectory(), "DFA/" + documentId)
            if (fileName.exists()) {
                playVideo(fileName.absolutePath)
            } else {
                DownloadTask(this, videoPath, documentId, this)
            }
        } catch (e: Exception) {
        }
        if (!videoPath.isEmpty()) {

            try {
                Glide.with(this)
                    .asBitmap()
                    .load(videoPath)
                    .into(thumbImage!!);
                thumbImage!!.visibility = View.VISIBLE
            } catch (e: Exception) {
                println("Exception>>>>>>>>>>>>>>" + e)
            }
        }
    }


    fun askPermissionScanner() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                requestPermission3()
                //   return false;

            } else {
                downloadData()
                //  return true;
            }


        } else {
            downloadData()
            // return true;
        }

    }


    fun requestPermission3() {
        /* requestPermissions(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ), 147
        );*/
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            456
        );
    }


    fun setLoader(circleProgress: CircularProgressView, percentage: Int, dialog: Dialog) {
        try {

            runOnUiThread {
                if (percentage != 0) {
                    circleProgress.setProgress(percentage, true);

                    // circleProgress.marginTop=4px
                }
            }
            dialog1 = dialog

        } catch (e: Exception) {
        }
    }

    fun play() {
        var fileName = File(Environment.getExternalStorageDirectory(), "DFA/" + documentId)
        playVideo(fileName.absolutePath)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (DownloadTask.task != null) {
            if (!DownloadTask.task.isCancelled) {
                DownloadTask.task.cancel(true)
            }
        }
    }

    fun playVideo(videoPath: String?) {
        try {
            val myUri = Uri.parse(videoPath)
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            mediaController = MediaController(this)
            //   mediaController.setAnchorView(videoView)General
            mediaController!!.setAnchorView(videoView);
            mediaController!!.setMediaPlayer(videoView);
            videoView!!.setMediaController(mediaController)
            videoView!!.setVideoURI(myUri)

//            try {
//                val trustStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
//                trustStore.load(null, null)
//                val sf = MySSLSocketFactory(trustStore)
//                sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
//                sf.fixHttpsURLConnection()
//                val hostnameVerifier: HostnameVerifier =
//                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
//                HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier)
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//            }

            videoView!!.requestFocus()
            videoView!!.start()
            videoView!!.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
                override fun onPrepared(mp: MediaPlayer?) {
                    mp!!.start()
                    thumbImage!!.visibility = View.GONE
                }
            })
            var firstPlay = true
            videoView!!.setOnErrorListener(object : MediaPlayer.OnErrorListener {
                override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
                    if (firstPlay) {
                        firstPlay = false
                        var fileName = File(
                            Environment.getExternalStorageDirectory(),
                            "DFA/" + documentId
                        )
                        fileName.delete()

                        if (retry > 1) {
                            retry = 0
                            Toast.makeText(
                                this@VideoPlayerActivity,
                                "Can't play this video",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            retry++
                            DownloadTask(
                                this@VideoPlayerActivity,
                                mPath,
                                documentId,
                                this@VideoPlayerActivity
                            )
                        }
                    }
                    return true
                }
            })

        } catch (e: Exception) {
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun handleKeyboard(): View {
        return parantLayout
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            456 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Toast.makeText(mContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                    downloadData()
                } else {
                    /* Toast.makeText(activity!!, "Permission Denied", Toast.LENGTH_SHORT).show()*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermission3()
                            }
                        }
                    }
                }
            }
        }
    }
}

