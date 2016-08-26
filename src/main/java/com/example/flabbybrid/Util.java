package com.example.flabbybrid;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by liuxian on 2016/8/24.
 */

public class Util {

    public static int dp2px(Context context , float dp){
        int px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP , dp , context.getResources().getDisplayMetrics()));
        return px;
    }
}
