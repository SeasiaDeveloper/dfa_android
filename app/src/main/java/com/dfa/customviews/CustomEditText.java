package com.dfa.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomEditText extends androidx.appcompat.widget.AppCompatEditText {
    private Context context;
    private AttributeSet attrs;
    private int defStyle;

    public CustomEditText(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.attrs = attrs;
        this.defStyle = defStyle;
        init();
    }

    private void init() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Montserrat-Regular_0.ttf");
        this.setTypeface(font);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Montserrat-Regular_0.ttf");
        super.setTypeface(tf, style);
    }

    @Override
    public void setTypeface(Typeface tf) {
        tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Montserrat-Regular_0.ttf");
        super.setTypeface(tf);
    }
}
