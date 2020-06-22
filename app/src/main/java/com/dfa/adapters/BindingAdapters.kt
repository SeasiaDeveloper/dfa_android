package com.dfa.adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.dfa.utils.FontStyle

object BindingAdapters {

    @BindingAdapter("bind:font")
    @JvmStatic
    fun setFont(textView: TextView, fontName: String) {
        textView.typeface = FontStyle.instance.getFont(fontName)
    }


}

