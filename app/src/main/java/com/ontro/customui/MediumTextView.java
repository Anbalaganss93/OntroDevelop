package com.ontro.customui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ontro.R;

/**
 * Created by IDEOMIND02 on 30-08-2017.
 */

public class MediumTextView extends TextView {

    public MediumTextView(Context context) {
        super(context);
        init(null);
    }

    public MediumTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MediumTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyTextView);

            String fontName = a.getString(R.styleable.MyTextView_semibold);
            if (fontName != null) {
                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto_medium.ttf");
                setTypeface(myTypeface);
            }
            a.recycle();
        }
    }
}
