package com.ngo.adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.ngo.utils.FontStyle

object BindingAdapters {

    @BindingAdapter("bind:font")
    @JvmStatic
    fun setFont(textView: TextView, fontName: String) {
        textView.typeface = FontStyle.instance.getFont(fontName)
    }


}

