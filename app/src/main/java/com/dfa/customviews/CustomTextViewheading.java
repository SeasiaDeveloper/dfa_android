package com.dfa.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class CustomTextViewheading extends AppCompatTextView {
    private final Context context;

    public CustomTextViewheading(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (!isInEditMode())
            init();
    }

    private void init() {
        Typeface font = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat-SemiBold_0.ttf");
        //Typeface font = FontCache.getTypeface(FontCache.HEADING_REGULAR_FONT, context);
        setTypeface(font, Typeface.BOLD);
    }

}

