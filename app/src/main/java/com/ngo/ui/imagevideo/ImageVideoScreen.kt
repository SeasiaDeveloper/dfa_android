package com.ngo.ui.imagevideo

import android.net.Uri
import android.view.View
import android.widget.MediaController
import com.bumptech.glide.Glide
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.utils.Constants
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.image_video_layout.*
import kotlinx.android.synthetic.main.image_video_layout.toolbarLayout


class ImageVideoScreen : BaseActivity() {
    var mediaType: String? = null
    var imageUrl: String? = null
    private lateinit var mediaControls: MediaController
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
            Utilities.showMessage(this, "Please wait...")
            videoView.visibility = View.VISIBLE
            showVideo(imageUrl!!)
        } else {
            videoView.visibility = View.GONE
            imageView.visibility = View.VISIBLE
            Glide.with(this).load(imageUrl).into(imageView)
        }
    }

    fun showVideo(videoUri: String) {
        val video =
            Uri.parse(videoUri)
        videoView.setVideoURI(video)
        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            videoView.start()
        }
    }

    override fun handleKeyboard(): View {
        return imageVideoLayout
    }
}