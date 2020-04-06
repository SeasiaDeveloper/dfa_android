package com.ngo.customviews

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.ngo.R


class CenteredToolbar : Toolbar {
    private var titleView: TextView? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
        val custom_font = Typeface.createFromAsset(context.assets, "fonts/ROBOTO-MEDIUM_0.TTF")
        titleView?.setTypeface(custom_font)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        if (!isInEditMode) {
            titleView = TextView(context)
            titleView!!.setSingleLine(true)

            titleView!!.ellipsize = TextUtils.TruncateAt.END
            addView(
                titleView,
                Toolbar.LayoutParams(
                    Toolbar.LayoutParams.WRAP_CONTENT,
                    Toolbar.LayoutParams.WRAP_CONTENT
                )
            )
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (titleView != null) titleView!!.x = (width - titleView!!.width shr 1).toFloat()
    }

    override fun setTitle(title: CharSequence) {
        if (titleView != null) {
            titleView!!.text = title
            titleView!!.textSize = 20f
        }
    }

    override fun setTitleTextColor(color: Int) {
        if (titleView != null) titleView!!.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.white
            )
        )
    }
}
