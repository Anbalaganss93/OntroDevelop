package com.ontro.customui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ontro.R;


/**
 * Created by umm
 */
public class BoldTextView extends TextView {

    public BoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public BoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public BoldTextView(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (attrs!=null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyTextView);

            String fontName = a.getString(R.styleable.MyTextView_bold);
            if (fontName!=null) {
                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto_bold.ttf");
                setTypeface(myTypeface);
            }
            a.recycle();
        }
    }

}
