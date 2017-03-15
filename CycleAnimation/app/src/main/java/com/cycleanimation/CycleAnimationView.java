package com.cycleanimation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CycleAnimationView extends View {

    // c1 : red
    private static final int[] HEX_COLORS_PERIOD = {0xffea1d4c, 0xfff38a77, 0xffff9467, 0xfff67173, 0xffe97a95, 0xffac85c6, 0xffc0514c, 0xfff85464};
    // c2: dark green
    private static final int[] HEX_COLORS_OVU = {0xff63a11e, 0xff6f7a24, 0xff34a874, 0xff24a969, 0xff388798, 0xff0b9098, 0xff5f7426, 0xff7f9949};
    // c3: light green
    private static final int[] HEX_COLORS_FERTILE = {0xff8fd437, 0xff989d48, 0xff89c05a, 0xff99bf65, 0xff40b8ae, 0xff58b9b2, 0xff83941b, 0xff9bd52};
    // c4: light grey
    private static final int HEX_COLOR_BASE = 0xffc5c5c5;
    // c5: dark_grey
    private static final int TEXT_COLOR = 0xff404040;
    // c6
    private static final int COLOR_PERIOD_LATE = TEXT_COLOR;
    // background color
    private static final int SELFDEFINED_COLOR_BACKGROUND = Color.WHITE;
    // text color
    private static final int SELFDEFINED_COLOR_TEXT = Color.WHITE;

    private static Paint paint = null;
    static {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    // should be loaded from resources or made by any rules.
    private String string1 = "Top Label String";
    private String string2 = "Comment - Top Label String";
    private String string3 = "Period";
    private String string4 = "in 24 days";
    private String string5 = "Ovulation";
    private String string6 = "5 days later";
    private String string7 = "PREGNANCY PROBABILITY - HIGH";
    private String string8 = "PERIOD LATE: 15 DAYS";




    private boolean isStarted = false;

    private long mStartTime;
    private long mSelectTime;
    private int mSelectedDay;

    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private CycleActivity mActivity = null;

    public CycleAnimationView(Context context) {
        super(context);
    }

    public CycleAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CycleAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    public void setRelativeActivity(CycleActivity relativeActivity) {
        this.mActivity = relativeActivity;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mSurfaceWidth = w;
        mSurfaceHeight = h;
    }

    private void startAnimation() {
        mStartTime = System.currentTimeMillis();
        mSelectTime = mStartTime - 3000;
        isStarted = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mActivity == null)
            return;

        if (!isStarted) {
            startAnimation();
        }


        paint.setColor(SELFDEFINED_COLOR_BACKGROUND);
        canvas.drawPaint(paint);

        // top label
        drawTopLabel(canvas);

        // cycle wheel
        drawCycleWheel(canvas);

        // bottom label
        drawBottomLabels(canvas);
    }


    private Rect mTopLabelBoundRect = null;
    private Rect getTopLabelBoundRect() {
        if (mTopLabelBoundRect == null)
            mTopLabelBoundRect = new Rect(0, 0, mSurfaceWidth, mSurfaceHeight / 10);

        return mTopLabelBoundRect;
    }

    private Rect mWheelBoundRect = null;
    private Rect getWheelBoundRect() {
        if (mWheelBoundRect == null)
            mWheelBoundRect = new Rect(0, getTopLabelBoundRect().bottom, mSurfaceWidth, mSurfaceHeight - mSurfaceHeight / 4);

        return mWheelBoundRect;
    }

    private Rect mBottomLabelsBoundRect = null;
    private Rect getBottomLabelsBoundRect() {
        if (mBottomLabelsBoundRect == null)
            mBottomLabelsBoundRect = new Rect(0, getWheelBoundRect().bottom, mSurfaceWidth, mSurfaceHeight);

        return mBottomLabelsBoundRect;
    }

    private void drawTopLabel(Canvas canvas) {
        Rect rect =  getTopLabelBoundRect();

        int textY = calcTopLabelPos();

        paint.setColor(TEXT_COLOR);

        boolean isReverse = (((System.currentTimeMillis() - mStartTime) / 4000) % 2) == 1;
        paint.setTextSize(rect.height() * 2 / 5);
        Utils.drawTextCenterVertically(canvas, paint, rect.centerX(), textY, isReverse ? string2 : string1);
        Utils.drawTextCenterVertically(canvas, paint, rect.centerX(), textY - rect.height(), isReverse ? string1 : string2);
    }

    private void drawCycleWheel(Canvas canvas) {
        paint.setColor(SELFDEFINED_COLOR_BACKGROUND);
        Rect rect = getWheelBoundRect();
        canvas.drawRect(rect, paint);

        float startAngle = calcStartAngle();
        float endAngle = calcEndAngle();
        int radius1 = calcRadius1();
        int radius2 = calcRadius2();
        int centerX = rect.centerX();
        int centerY = calcCenterY();

        // duration (light grey)
        drawRing(canvas, centerX, centerY, radius1, radius2, startAngle, calcDayAngle(startAngle, endAngle, mActivity.mCycleDuration), HEX_COLOR_BASE);

        // period_length (red)
        drawRing(canvas, centerX, centerY, radius1, radius2, startAngle, calcDayAngle(startAngle, endAngle, mActivity.mPeriodLength), HEX_COLORS_PERIOD[mActivity.mThemeId]);

        if (mActivity.mCycleDay <= mActivity.mCycleDuration) {
            // ovulation before (light green)
            drawRing(canvas, centerX, centerY, radius1, radius2, startAngle + calcDayAngle(startAngle, endAngle, mActivity.mOvulation - 4), calcDayAngle(startAngle, endAngle, 3), HEX_COLORS_FERTILE[mActivity.mThemeId]);

            // ovulation (dark green)
            drawRing(canvas, centerX, centerY, radius1, radius2, startAngle + calcDayAngle(startAngle, endAngle, mActivity.mOvulation - 1), calcDayAngle(startAngle, endAngle, 1), HEX_COLORS_OVU[mActivity.mThemeId]);

        } else {
            // late (dark grey)
            drawRing(canvas, centerX, centerY, radius1, radius2, startAngle + calcDayAngle(startAngle, endAngle, mActivity.mCycleDuration), calcDayAngle(startAngle, endAngle, mActivity.mCycleDay - mActivity.mCycleDuration), COLOR_PERIOD_LATE);
        }

        paint.setColor(SELFDEFINED_COLOR_BACKGROUND);
        canvas.drawCircle(centerX, centerY, radius2, paint);

        // draw arrow
        int innerColor = calcDayColor(mActivity.mCycleDay);

        float angle = calcArrowAngle(startAngle, endAngle);
        int x1 = (int) (centerX + Math.cos(angle) * radius2 * 3 / 4);
        int y1 = (int) (centerY + Math.sin(angle) * radius2 * 3 / 4);
        int x2 = (int) (centerX + Math.cos(angle) * radius2);
        int y2 = (int) (centerY + Math.sin(angle) * radius2);
        int radiusOffset = radius2 / 40;

        paint.setColor(innerColor);
        Path arrowPath = new Path();
        arrowPath.reset();
        arrowPath.moveTo(centerX, centerY);

        arrowPath.lineTo((float) centerX + (float) Math.cos(angle - Math.PI / 2) * radiusOffset / 2, (float) centerY + (float) Math.sin(angle - Math.PI / 2) * radiusOffset / 2);
        arrowPath.lineTo((float) x1 + (float) Math.cos(angle - Math.PI / 2) * radiusOffset / 2, (float) y1 + (float) Math.sin(angle - Math.PI / 2) * radiusOffset / 2);
        arrowPath.lineTo((float) x1 + (float) Math.cos(angle - Math.PI / 2) * radiusOffset * 4, (float) y1 + (float) Math.sin(angle - Math.PI / 2) * radiusOffset * 4);
        arrowPath.lineTo(x2, y2);
        arrowPath.lineTo((float) x1 + (float) Math.cos(angle + Math.PI / 2) * radiusOffset * 4, (float) y1 + (float) Math.sin(angle + Math.PI / 2) * radiusOffset * 4);
        arrowPath.lineTo((float) x1 + (float) Math.cos(angle + Math.PI / 2) * radiusOffset / 2, (float) y1 + (float) Math.sin(angle + Math.PI / 2) * radiusOffset / 2);;
        arrowPath.lineTo((float) centerX + (float) Math.cos(angle + Math.PI / 2) * radiusOffset / 2, (float) centerY + (float) Math.sin(angle + Math.PI / 2) * radiusOffset / 2);

        arrowPath.lineTo(centerX, centerY);
        canvas.drawPath(arrowPath, paint);

        // draw days
        int radiusDay = (radius1 + radius2) / 2;
        paint.setTextSize((radius1 - radius2) * 2 / 5);
        paint.setColor(SELFDEFINED_COLOR_TEXT);
        int range = Math.max(mActivity.mCycleDay, mActivity.mCycleDuration);
        int step = Math.max((range + 39) / 40, 1);
        for (int i = 1; i <= range; i += step) {
            angle = (float) Math.toRadians(startAngle + calcDayAngle(startAngle, endAngle, i) - calcDayAngle(startAngle, endAngle, 1) / 2);
            int x = (int) (centerX + Math.cos(angle) * radiusDay);
            int y = (int) (centerY + Math.sin(angle) * radiusDay);
            Utils.drawTextCenterVertically(canvas, paint, x, y, String.valueOf(i));
        }

        // inner circle
        paint.setColor(innerColor);
        canvas.drawCircle(centerX, centerY, radius2 / 2, paint);
        paint.setColor(SELFDEFINED_COLOR_BACKGROUND);
        canvas.drawCircle(centerX, centerY, radius2 / 2 - radiusOffset, paint);
        paint.setColor(innerColor);
        canvas.drawCircle(centerX, centerY, radius2 / 2 - radiusOffset * 3, paint);

        paint.setColor(SELFDEFINED_COLOR_TEXT);
        if (System.currentTimeMillis() - mSelectTime < 3000) {
            paint.setTextSize(radius2 / 6);
            Date date = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(mSelectedDay - mActivity.mCycleDay));
            DateFormat dateFormat_MMM_d = new SimpleDateFormat("MMM d");
            DateFormat dateFormat_yyyy = new SimpleDateFormat("yyyy");
            Utils.drawTextCenterVertically(canvas, paint, centerX, centerY, dateFormat_MMM_d.format(date).toUpperCase(), dateFormat_yyyy.format(date));

//            int highlightColor = calcDayColor(mSelectedDay);
//            highlightColor = Color.argb(0x80,
//                                        Math.max(Color.red(highlightColor), 0xb0 | Color.red(highlightColor)),
//                                        Math.max(Color.green(highlightColor), 0xb0 | Color.green(highlightColor)),
//                                        Math.max(Color.blue(highlightColor), 0xb0 | Color.blue(highlightColor)));

            angle = (float) Math.toRadians(startAngle + calcDayAngle(startAngle, endAngle, mSelectedDay) - calcDayAngle(startAngle, endAngle, 1) / 2);
            int x3 = (int) (centerX + Math.cos(angle) * (radius1 + radius2) / 2);
            int y3 = (int) (centerY + Math.sin(angle) * (radius1 + radius2) / 2);
            paint.setColor(calcDayColor(mSelectedDay));
            canvas.drawCircle(x3, y3, (radius1 - radius2) * 4 / 5, paint);

            paint.setColor(SELFDEFINED_COLOR_TEXT);
            paint.setTextSize((radius1 - radius2) * 4 / 5);
            Utils.drawTextCenterVertically(canvas, paint, x3, y3, String.valueOf(mSelectedDay));
        } else {
            paint.setTextSize(radius2 / 3);
            String centerText;
            if (mActivity.mCycleDay <= mActivity.mCycleDuration)
                centerText = String.valueOf(mActivity.mCycleDay);
            else
                centerText = "+" + (mActivity.mCycleDay - mActivity.mCycleDuration);
            Utils.drawTextCenterVertically(canvas, paint, centerX, centerY, centerText);
        }
    }

    private void drawBottomLabels(Canvas canvas) {
        Rect rect = getBottomLabelsBoundRect();
        if (mActivity.mCycleDay <= mActivity.mCycleDuration) {
            int middleSpaceSize = calcMiddleSpaceSize();

            Rect halfRect = new Rect(rect.centerX() - middleSpaceSize / 2 - rect.width() / 2, rect.top,
                    rect.centerX() - middleSpaceSize / 2,                    rect.centerY());
            paint.setColor(HEX_COLORS_PERIOD[mActivity.mThemeId]);
            canvas.drawRect(halfRect, paint);
            paint.setColor(SELFDEFINED_COLOR_TEXT);
            paint.setTextSize(halfRect.height() / 4);
            Utils.drawTextCenterVertically(canvas, paint, halfRect.centerX(), halfRect.centerY(), string3, string4);

            halfRect.offset(halfRect.width() + middleSpaceSize, 0);
            paint.setColor(HEX_COLORS_OVU[mActivity.mThemeId]);
            canvas.drawRect(halfRect, paint);
            paint.setColor(SELFDEFINED_COLOR_TEXT);
            Utils.drawTextCenterVertically(canvas, paint, halfRect.centerX(), halfRect.centerY(), string5, string6);
        }

        Rect bottomHalfRect = new Rect(rect);
        bottomHalfRect.top = rect.centerY();
        int bottomHalfRectTop = calcBottomHalfRectTop(rect.centerY());
        bottomHalfRect.offsetTo(bottomHalfRect.left, bottomHalfRectTop);
        paint.setColor((mActivity.mCycleDay <= mActivity.mCycleDuration ? HEX_COLOR_BASE : COLOR_PERIOD_LATE));
        canvas.drawRect(bottomHalfRect, paint);

        paint.setTextSize(bottomHalfRect.height() * 2 / 7);
        paint.setColor(calcBottomLabelAlpha(mActivity.mCycleDay <= mActivity.mCycleDuration ? TEXT_COLOR : HEX_COLOR_BASE));
        Utils.drawTextCenterVertically(canvas, paint, bottomHalfRect.centerX(), bottomHalfRect.centerY(), mActivity.mCycleDay <= mActivity.mCycleDuration ? string7 : string8);
    }


    private void drawRing(Canvas canvas, int centerX, int centerY, int radius1, int radius2, float startAngle, float sweepAngle, int color) {
        drawArc(canvas, centerX, centerY, radius1, startAngle, sweepAngle, color);
    }

    private float calcStartAngle() {
        return -90f + 2.5f;
    }

    private float calcEndAngle() {
        return 270f - 2.5f;
    }

    private int calcRadius1() {
        Rect rect = getWheelBoundRect();
        return Math.min(rect.width(), rect.height()) * 9 / 10 / 2;
    }

    private int calcRadius2() {
        return calcRadius1() * 4 / 5;
    }

    private int calcCenterY() {
        return getWheelBoundRect().top + calcRadius1() + calcRadius1()/ 10;
    }

    private float calcDayAngle(float startAngle, float endAngle, int day) {
        return (endAngle - startAngle) * day / Math.max(mActivity.mCycleDuration, mActivity.mCycleDay);
    }

    private int calcDayColor(int day) {
        if (day <= mActivity.mPeriodLength)
            return HEX_COLORS_PERIOD[mActivity.mThemeId];
        else if (day > mActivity.mCycleDuration)
            return COLOR_PERIOD_LATE;
        else if (mActivity.mCycleDay <= mActivity.mCycleDuration) {
            if (mActivity.mOvulation == day)
                return HEX_COLORS_OVU[mActivity.mThemeId];
            else if (mActivity.mOvulation - 4 < day && day < mActivity.mOvulation)
                return HEX_COLORS_FERTILE[mActivity.mThemeId];
        }

        return HEX_COLOR_BASE;
    }

    private void drawArc(Canvas canvas, int centerX, int centerY, int radius, float startAngle, float sweepAngle, int color) {
        paint.setColor(color);
        RectF rect = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        canvas.drawArc(rect, startAngle, sweepAngle, true, paint);
    }

//    private String makeBottomLabelString() {
//        if (mActivity.mCycleDay > mActivity.mCycleDuration) {
//            int x = mActivity.mCycleDay - mActivity.mCycleDuration;
//            return String.format("PERIOD LATE: %d DAY" + (x > 1 ? "S" : ""), x);
//        }
//
//        return "PREGNANCY PROBABILITY: " + getPregnancyProbabilityString();
//    }
//
//    private String getPregnancyProbabilityString() {
//        return "HIGH";                                                      // LOW, MEDIUM ... should be calculated
//    }
//
//    private String getOvulationDayString() {
//        int x = mActivity.mCycleDay - mActivity.mOvulation;
//        if (x == 0)
//            return "today";
//
//        return String.format("%d day" + (Math.abs(x) > 1 ? "s" : "") + (x < 0 ? " later" : " earlier"), Math.abs(x));
//    }


    private static final long ANIM_TIME_LEN = 1000;
    private float calcArrowAngle(float startAngle, float endAngle) {
        long diffTime = System.currentTimeMillis() - mStartTime - ANIM_TIME_LEN / 2;
        if (diffTime < 0)
            return (float) (-Math.PI / 2);
        if (diffTime > ANIM_TIME_LEN)
            return (float) Math.toRadians(startAngle + calcDayAngle(startAngle, endAngle, mActivity.mCycleDay) - calcDayAngle(startAngle, endAngle, 1) / 2);

        return (float) Math.toRadians(startAngle + (360.0f + calcDayAngle(startAngle, endAngle, mActivity.mCycleDay) - calcDayAngle(startAngle, endAngle, 1) / 2) * diffTime / ANIM_TIME_LEN);
    }

    private int calcTopLabelPos() {
        Rect rect = getTopLabelBoundRect();
        long diffTime = (System.currentTimeMillis() - mStartTime) % 4000;
        if (diffTime < 3 * ANIM_TIME_LEN)
            return rect.centerY();

        diffTime -= 3 * ANIM_TIME_LEN;
        double radian = Math.PI * 2 * diffTime / ANIM_TIME_LEN;
        double h = rect.height() * (1 - Math.sin(Math.PI / 2 * diffTime / ANIM_TIME_LEN));
        return (int) (rect.centerY() + rect.height() - Math.cos(radian) * h);
    }

    private int calcMiddleSpaceSize() {
        long diffTime = System.currentTimeMillis() - mStartTime;
        if (diffTime < ANIM_TIME_LEN * 2)
            return mSurfaceWidth;

        if (ANIM_TIME_LEN * 3 < diffTime)
            return 0;

        diffTime -= 2 * ANIM_TIME_LEN;
        double radian = Math.PI * 2 * diffTime / ANIM_TIME_LEN;
        double h = (double) mSurfaceWidth * (1 - Math.sin(Math.PI / 2 * diffTime / ANIM_TIME_LEN));
        return (int) (Math.cos(radian) * h);
    }

    private int calcBottomHalfRectTop(int centerY) {
        long diffTime = System.currentTimeMillis() - mStartTime;
        if (diffTime < ANIM_TIME_LEN)
            return mSurfaceHeight;

        if (ANIM_TIME_LEN * 3 < diffTime)
            return centerY;

        diffTime -= ANIM_TIME_LEN;
        double radian = Math.PI * diffTime * 3 / ANIM_TIME_LEN;
        double h = (mSurfaceHeight - centerY) * (1 - Math.sin(Math.PI / 4 * diffTime / ANIM_TIME_LEN));
        return (int) (centerY + Math.cos(radian) * h);
    }

    private int calcBottomLabelAlpha(int color) {
        long diffTime = System.currentTimeMillis() - mStartTime;
        if (diffTime < ANIM_TIME_LEN)
            return Color.TRANSPARENT;

        if (ANIM_TIME_LEN * 3 < diffTime)
            return color;

        diffTime -= ANIM_TIME_LEN;
        diffTime /= 2;
        return Color.argb((int) (0xff * diffTime / ANIM_TIME_LEN), Color.red(color), Color.green(color), Color.blue(color));
    }


    boolean isSelecting = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_MOVE:
                if (!isSelecting)
                    break;
                selectDay(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_DOWN:
                isSelecting = selectDay(event.getX(), event.getY());
                break;
            default:
                isSelecting = false;
        }
        return true;
    }

    private boolean selectDay(float x, float y) {
        int radius1 = calcRadius1();
        int radius2 = calcRadius2();
        Rect rect = getWheelBoundRect();
        int centerX = rect.centerX();
        int centerY = calcCenterY();
        int offX = (int) (x - centerX);
        int offY = (int) (y - centerY);
        int dist = (int) Math.sqrt(offX * offX + offY * offY);
        if (dist < radius2 || radius1 < dist)
            return false;

        float startAngle = calcStartAngle();
        float endAngle = calcEndAngle();
        float angle = (float) Math.toDegrees(Math.atan2(offY, offX));
        if (angle < startAngle) angle += 360f;
        if (angle > endAngle) angle -= 360f;
        if (angle < startAngle) return false;

        mSelectedDay = (int) Math.ceil((angle - startAngle) / calcDayAngle(startAngle, endAngle, 1));
        mSelectTime = System.currentTimeMillis();

        return true;
    }
}
