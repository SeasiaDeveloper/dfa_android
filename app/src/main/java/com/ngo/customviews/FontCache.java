package com.ngo.customviews;

/*
* This class is used to set the font and style to whole application
*/
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import java.util.HashMap;

public class FontCache {
    public static final String REGULAR_FONT ="Montserrat-Regular_0.ttf";
    public static final String HEADING_REGULAR_FONT="Montserrat-SemiBold_0.ttf";


    private static final HashMap<String, Typeface> fontCache = new HashMap<>();

    public static Typeface getTypeface(String fontname, Context context) {
        Typeface typeface = fontCache.get(fontname);
        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), fontname);
            } catch (Exception e) {
                return null;
            }
            fontCache.put(fontname, typeface);
        } else {
            Log.d("", "");
        }
        return typeface;
    }
}