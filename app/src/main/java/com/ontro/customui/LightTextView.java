package com.ontro.customui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ontro.R;

/**
 * Created by IDEOMIND02 on 05-09-2017.
 */

public class LightTextView extends TextView {

    public LightTextView(Context context) {
        super(context);
        init(null);
    }

    public LightTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LightTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }



    private void init(AttributeSet attrs) {
        if (attrs!=null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyTextView);

            String fontName = a.getString(R.styleable.MyTextView_bold);
            if (fontName!=null) {
                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto_light.ttf");
                setTypeface(myTypeface);
            }
            a.recycle();
        }
    }
}
