package com.example.aharm.sporkly;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;

/**
 * Created by David on 11/16/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class myPantryActivityTest {
    private String testQuery;

    @Rule
    public ActivityTestRule<myPantryActivity> mActivityRule = new ActivityTestRule<>(
            myPantryActivity.class);


    @Before
    public void init() {
        testQuery = "apple";
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.aharm.sporkly", appContext.getPackageName());
    }

    @Test
    public void query() {
        // Edit the text box
        onView(withId(R.id.pantryAddText))
                .perform(typeText(testQuery), closeSoftKeyboard());

        // Send Query
        onView(withId(R.id.pantryAddButton)).perform(click());

        // Check the search result size
        onView(withId(R.id.pantrySearchList))
                .check(ViewAssertions.matches(Matchers.withListSize(5)));
    }
}
