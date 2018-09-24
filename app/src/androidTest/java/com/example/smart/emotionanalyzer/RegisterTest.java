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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class RegisterTest {

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityTestRule = new ActivityTestRule<>(RegisterActivity.class);
    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);

    @Test
    public void successRegister() {
        onView(withId(R.id.name)).perform(click()).perform(replaceText("Test Register"), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(click()).perform(replaceText("testregister@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("Register123"), closeSoftKeyboard());
        onView(withId(R.id.confirm_password)).perform(click()).perform(replaceText("Register123"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(click());
        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        onView(withId(R.id.logout)).perform(click());
        mainActivity.finish();
    }

    @Test
    public void incorrectEmailFormat() {
        onView(withId(R.id.name)).perform(click()).perform(replaceText("Test Register"), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(click()).perform(replaceText("testregister"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("Register123"), closeSoftKeyboard());
        onView(withId(R.id.confirm_password)).perform(click()).perform(replaceText("Register123"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(click());
        onView(withText("This email address is invalid")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
    @Test
    public void noUppercasePassword() {
        onView(withId(R.id.name)).perform(click()).perform(replaceText("Test Register"), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(click()).perform(replaceText("testregister@yahoo.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("register123"), closeSoftKeyboard());
        onView(withId(R.id.confirm_password)).perform(click()).perform(replaceText("register123"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(click());
        onView(withText("This password is too short")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
    @Test
    public void blankPassword() {
        onView(withId(R.id.name)).perform(click()).perform(replaceText("Test Register"), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(click()).perform(replaceText("testregister@yahoo.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.confirm_password)).perform(click()).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(click());
        onView(withText("This password is too short")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
    //
    @Test
    public void noNumeralPassword() {
        onView(withId(R.id.name)).perform(click()).perform(replaceText("Test Register"), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(click()).perform(replaceText("testregister@yahoo.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("Register"), closeSoftKeyboard());
        onView(withId(R.id.confirm_password)).perform(click()).perform(replaceText("Register"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(click());
        onView(withText("This password is too short")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void shortPassword() {
        onView(withId(R.id.name)).perform(click()).perform(replaceText("Test Register"), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(click()).perform(replaceText("testregister@yahoo.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("reg"), closeSoftKeyboard());
        onView(withId(R.id.confirm_password)).perform(click()).perform(replaceText("reg"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(click());
        onView(withText("This password is too short")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void nonmatchingPasswords() {
        onView(withId(R.id.name)).perform(click()).perform(replaceText("Test Register"), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(click()).perform(replaceText("testregister@yahoo.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("Register123"), closeSoftKeyboard());
        onView(withId(R.id.confirm_password)).perform(click()).perform(replaceText("Register12345"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(click());
        onView(withText("Passwords do not match")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
}
