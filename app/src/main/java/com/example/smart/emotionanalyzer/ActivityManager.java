package com.example.smart.emotionanalyzer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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

}
