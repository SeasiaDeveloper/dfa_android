package com.ngo.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class CustomtextView extends AppCompatTextView {
    private final Context context;

    public CustomtextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (!isInEditMode())
            init();
    }

    private void init() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Montserrat-Regular_0.ttf");
        this.setTypeface(font,Typeface.BOLD);
    }

}

