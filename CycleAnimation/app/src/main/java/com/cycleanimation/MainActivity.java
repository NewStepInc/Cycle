package com.cycleanimation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void tapTest(View view) {
        int cycle_duration = Integer.parseInt(((EditText) findViewById(R.id.test_duration)).getEditableText().toString());
        int cycle_day = Integer.parseInt(((EditText) findViewById(R.id.test_day)).getEditableText().toString());
        int period_length = Integer.parseInt(((EditText) findViewById(R.id.test_period_length)).getEditableText().toString());
        int ovulation = Integer.parseInt(((EditText) findViewById(R.id.test_ovulation)).getEditableText().toString());
        String lang = ((EditText) findViewById(R.id.test_lang)).getEditableText().toString();
        int theme_id = Integer.parseInt(((EditText) findViewById(R.id.test_theme_id)).getEditableText().toString());


        Intent intent = new Intent(getApplicationContext(), CycleActivity.class);
        intent.putExtra(CycleActivity.LAUNCH_PARAM_CYCLE_DURATION, cycle_duration);
        intent.putExtra(CycleActivity.LAUNCH_PARAM_CYCLE_DAY, cycle_day);
        intent.putExtra(CycleActivity.LAUNCH_PARAM_PERIOD_LENGTH, period_length);
        intent.putExtra(CycleActivity.LAUNCH_PARAM_OVULATION, ovulation);
        intent.putExtra(CycleActivity.LAUNCH_PARAM_LANG, lang);
        intent.putExtra(CycleActivity.LAUNCH_PARAM_THEME_ID, theme_id);

        startActivity(intent);
    }
}
