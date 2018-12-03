package com.example.smart.emotionanalyzer;

import android.test.ActivityInstrumentationTestCase2;

import org.junit.Test;

import java.io.IOException;

public class EmotionTest extends ActivityInstrumentationTestCase2 {
private LoginActivity mTestActivity;
private EmotionClassifier classifier;
    public EmotionTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mTestActivity = (LoginActivity) getActivity();



        try {
            classifier = new EmotionClassifier(getActivity());
        }
        catch (IOException e) {
            classifier = null;
        }

    }

    @Test
    public void testAngry() {
        assertEquals(classifier.ANGRY,classifier.predict("I hate you."));
        assertEquals(classifier.ANGRY,classifier.predict("This is one of the dumbest ideas I have ever heard."));
        assertEquals(classifier.ANGRY,classifier.predict("This is ridiculous, I demand you to be fired."));
        assertEquals(classifier.ANGRY,classifier.predict("Do not talk to me like that"));
    }
    @Test
    public void testSad() {
        assertEquals(classifier.SAD,classifier.predict("I want to cry."));
        assertEquals(classifier.SAD,classifier.predict("I've never felt more sad in my life."));
        assertEquals(classifier.SAD,classifier.predict("Seeing people act like this makes me depressed."));
        assertEquals(classifier.SAD,classifier.predict("I am so overwhelmed."));
    }

    @Test
    public void testHappy() {
        assertEquals(classifier.HAPPY,classifier.predict("This actor will do great in this role."));
        assertEquals(classifier.HAPPY,classifier.predict("I am very optimistic for the future"));
        assertEquals(classifier.HAPPY,classifier.predict("Wow. I can't wait to try that out!"));
        assertEquals(classifier.HAPPY,classifier.predict("This looks awesome!`"));
    }
}
