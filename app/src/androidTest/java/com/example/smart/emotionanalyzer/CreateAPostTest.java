package com.example.smart.emotionanalyzer;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class CreateAPostTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);
    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);

    @Test
    public void createPost() {
        onView(withId(R.id.email)).perform(click()).perform(replaceText("albertyu_85@yahoo.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("Broncos81"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        onView(withId(R.id.add_topic)).perform(click());
        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Business"))).perform(click());
        onView(withId(R.id.editTextPost)).perform(click()).perform(replaceText("This is an example post"), closeSoftKeyboard());
        onView(withId(R.id.PostButton)).perform(click());
        onView(withId(R.id.navigation_Account)).perform(click());
        onView(withId(R.id.logout)).perform(click());

    }

    @Test
    public void deletePost() {
        onView(withId(R.id.email)).perform(click()).perform(replaceText("albertyu_85@yahoo.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("Broncos81"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        onView(withId(R.id.add_topic)).perform(click());
        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Business"))).perform(click());
        onView(withId(R.id.editTextPost)).perform(click()).perform(replaceText("This is an example post"), closeSoftKeyboard());
        onView(withId(R.id.DeleteButton)).perform(click());
        onView(withId(android.R.id.button2)).perform(click());


    }

}
