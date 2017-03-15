package com.cycleanimation;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Utils {

//    public static float dp2px(final Context context, final float px) {
//        return px / context.getResources().getDisplayMetrics().density;
//    }
//
//    public static float px2dp(final Context context, final float dp) {
//        return dp * context.getResources().getDisplayMetrics().density;
//    }
//
//    public static float px2sp(Context context, float px) {
//        return px / context.getResources().getDisplayMetrics().scaledDensity;
//    }
//
//    public static float sp2px(Context context, float sp) {
//        return sp * context.getResources().getDisplayMetrics().scaledDensity;
//    }

    public static void drawTextCenterVertically(Canvas canvas, Paint paint, int x, int y, String... strings) {
        if (strings.length == 0)
            return;

        Rect rect = new Rect();
        paint.getTextBounds(strings[0], 0, strings[0].length(), rect);
        int lineHeight = rect.height() * 3 / 2;
        y -= lineHeight * strings.length / 2 + (lineHeight - rect.height()) / 2;
        for (int i = 0; i < strings.length; i ++) {
            y += lineHeight;
            canvas.drawText(strings[i], x, y, paint);
        }
    }
}
