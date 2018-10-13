package com.example.smart.emotionanalyzer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class ActivityManager {

    private Activity context;

    public ActivityManager(Activity context) {
        this.context = context;
    }

    public void changeActivty(Class newActivity, Bundle bundle) {
        Intent intent = new Intent(context, newActivity);
        if (bundle != null)
            intent.putExtras(bundle);
        context.startActivity(intent);
        context.finish();
    }

    public void changeActivityWithDelay(final Class newActivity, final Bundle bundle, final int millis) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, newActivity);
                if (bundle != null)
                    intent.putExtras(bundle);
                context.startActivity(intent);
                context.finish();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(r, millis);
    }

}
