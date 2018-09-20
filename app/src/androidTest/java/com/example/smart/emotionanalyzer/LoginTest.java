package com.example.smart.emotionanalyzer;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LoginTest {

    /*
    Test Account Info
    Email : test@gmail.com
    Password : Tester123
     */
    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);
    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);

    @Test
    public void successLogin() {
        onView(withId(R.id.email)).perform(click()).perform(replaceText("test@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("Tester123"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 3000);
        mainActivity.finish();

    }
}
