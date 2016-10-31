package com.friendlyarm.serial.seaweather4shd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueenTextView extends TextView {

    public MarqueenTextView(Context context) {
        super(context);
    }

    public MarqueenTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueenTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused(){
        return true;
    }
}
