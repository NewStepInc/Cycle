package com.cycleanimation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class CycleActivity extends AppCompatActivity {
    public static final String LAUNCH_PARAM_CYCLE_DURATION = "cycle_duration";	// the total number of days in cycle
    public static final String LAUNCH_PARAM_CYCLE_DAY = "cycle_day";	// the current day in cycle (always today), arrow points to that day
    public static final String LAUNCH_PARAM_PERIOD_LENGTH = "period_length";	// number of days from day 1 to mPeriodLength displayed in red color
    public static final String LAUNCH_PARAM_OVULATION = "ovulation";	// mOvulation day displayed in green color, the 3 days preceding mOvulation day displaying in light green color
    public static final String LAUNCH_PARAM_LANG = "lang";		// language
    public static final String LAUNCH_PARAM_THEME_ID = "theme_id";	// theme


    public int mCycleDuration;
    public int mCycleDay;
    public int mPeriodLength;
    public int mOvulation;
    public String mLang;
    public int mThemeId;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cycleview);

        getSupportActionBar().hide();

        getParams();

        final CycleAnimationView cycleAnimationView = (CycleAnimationView) findViewById(R.id.cycle_canvas);
        cycleAnimationView.setRelativeActivity(this);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                cycleAnimationView.postInvalidate();
            }
        }, 0, 10);
    }

    protected void getParams() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCycleDuration = extras.getInt(LAUNCH_PARAM_CYCLE_DURATION);
            mCycleDay = extras.getInt(LAUNCH_PARAM_CYCLE_DAY);
            mPeriodLength = extras.getInt(LAUNCH_PARAM_PERIOD_LENGTH);
            mOvulation = extras.getInt(LAUNCH_PARAM_OVULATION);
            mLang = extras.getString(LAUNCH_PARAM_LANG);
            mThemeId = extras.getInt(LAUNCH_PARAM_THEME_ID);
        }
    }

    public void tapBack(View view) {
        onBackPressed();
    }


    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }
}
