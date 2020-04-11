package com.ngo.ui.imagevideo

import android.net.Uri
import android.view.View
import android.widget.MediaController
import com.bumptech.glide.Glide
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.utils.Constants
import kotlinx.android.synthetic.main.image_video_layout.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class ImageVideoScreen : BaseActivity() {
    var mediaType: String? = null
    var imageUrl: String? = null
    override fun getLayout(): Int {
        return R.layout.image_video_layout
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        mediaType = intent.getStringExtra("fromWhere")
        imageUrl = intent.getStringExtra(Constants.IMAGE_URL)
        if (mediaType.equals("VIDEOS")) {
            showProgress()
            videoView.visibility = View.VISIBLE
            val ctrl = MediaController(this@ImageVideoScreen)
            ctrl.visibility = View.VISIBLE
            videoView.setMediaController(ctrl)
            showVideo(imageUrl!!)
        } else {
            videoView.visibility = View.GONE
            imageView.visibility = View.VISIBLE
            try{Glide.with(this).load(imageUrl).into(imageView)}
            catch (e:Exception){
                e.printStackTrace()
            }
        }


    }

    fun showVideo(videoUri: String) {
        val video =
            Uri.parse(videoUri)
        videoView.setVideoURI(video)
        videoView.setOnPreparedListener { mp ->
            dismissProgress()
            mp.isLooping = true
            videoView.start()
        }
    }

    override fun handleKeyboard(): View {
        return imageVideoLayout
    }
}