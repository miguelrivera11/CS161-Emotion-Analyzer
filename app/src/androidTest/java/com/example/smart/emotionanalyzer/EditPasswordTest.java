package com.example.smart.emotionanalyzer;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class EditPasswordTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);
    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
    Instrumentation.ActivityMonitor Accountmonitor = getInstrumentation().addMonitor(EditAccountActivity.class.getName(), null, false);
    Instrumentation.ActivityMonitor Loginmonitor = getInstrumentation().addMonitor(LoginActivity.class.getName(), null, false);

    @Test
    public void successEdit() {
        onView(withId(R.id.email)).perform(click()).perform(replaceText("albertyu_85@yahoo.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("Broncos71"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        onView(withId(R.id.navigation_Account)).perform(click());

        onView(withId(R.id.edit_account)).perform(click());
        onView(withId(R.id.email)).perform(click()).perform(replaceText("albertyu_85@yahoo.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("Broncos71"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        Activity editAccount = getInstrumentation().waitForMonitorWithTimeout(Accountmonitor, 6000);

        onView(withId(R.id.change_password)).perform(click());

        onView(withId(R.id.new_password_edit)).perform(click()).perform(replaceText("Broncos71"), closeSoftKeyboard());
        onView(withId(R.id.confirm_password_edit)).perform(click()).perform(replaceText("Broncos71"), closeSoftKeyboard());

        onView(withId(R.id.confirm_changes)).perform(click());


        Activity mainActivity2 = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        onView(withId(R.id.logout)).perform(click());
        Activity loginActivity = getInstrumentation().waitForMonitorWithTimeout(Loginmonitor, 6000);
        onView(withId(R.id.email)).perform(click()).perform(replaceText("albertyu_85@yahoo.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("Broncos71"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        Activity mainActivity3 = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        onView(withId(R.id.navigation_Account)).perform(click());
        onView(withId(R.id.logout)).perform(click());

    }

    @Test
    public void nonmatchingEdit() {
        /*onView(withId(R.id.email)).perform(click()).perform(replaceText("albertyu_85@yahoo.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("Broncos71"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);*/
        onView(withId(R.id.navigation_Account)).perform(click());

        onView(withId(R.id.edit_account)).perform(click());
        onView(withId(R.id.email)).perform(click()).perform(replaceText("albertyu_85@yahoo.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("Broncos71"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        Activity editAccount = getInstrumentation().waitForMonitorWithTimeout(Accountmonitor, 6000);

        onView(withId(R.id.change_password)).perform(click());

        onView(withId(R.id.new_password_edit)).perform(click()).perform(replaceText("wrong"), closeSoftKeyboard());
        onView(withId(R.id.confirm_password_edit)).perform(click()).perform(replaceText("Broncos71"), closeSoftKeyboard());

        onView(withId(R.id.confirm_changes)).perform(click());
        onView(withText("Passwords do not match")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

        /*Activity mainActivity2 = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        onView(withId(R.id.logout)).perform(click());
        Activity loginActivity = getInstrumentation().waitForMonitorWithTimeout(Loginmonitor, 6000);
        onView(withId(R.id.email)).perform(click()).perform(replaceText("albertyu_85@yahoo.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("Broncos71"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        Activity mainActivity3 = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        onView(withId(R.id.navigation_Account)).perform(click());
        onView(withId(R.id.logout)).perform(click());*/

    }

    @Test
    public void blankEdit() {
        onView(withId(R.id.email)).perform(click()).perform(replaceText("albertyu_85@yahoo.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("Broncos71"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        onView(withId(R.id.navigation_Account)).perform(click());

        onView(withId(R.id.edit_account)).perform(click());
        onView(withId(R.id.email)).perform(click()).perform(replaceText("albertyu_85@yahoo.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(click()).perform(replaceText("Broncos71"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        Activity editAccount = getInstrumentation().waitForMonitorWithTimeout(Accountmonitor, 6000);

        onView(withId(R.id.change_password)).perform(click());

        onView(withId(R.id.new_password_edit)).perform(click()).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.confirm_password_edit)).perform(click()).perform(replaceText(""), closeSoftKeyboard());

        onView(withId(R.id.confirm_changes)).perform(click());
        onView(withText("Password is too short")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

}
