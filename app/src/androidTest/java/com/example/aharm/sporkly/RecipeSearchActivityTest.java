package com.example.aharm.sporkly;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.*;

/**
 * Created by David on 11/16/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class RecipeSearchActivityTest {
    private String testQuery;

    @Rule
    public ActivityTestRule<RecipeSearchActivity> mActivityRule = new ActivityTestRule<>(
            RecipeSearchActivity.class);


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
        onView(withId(R.id.recipeSearchText))
                .perform(typeText(testQuery), closeSoftKeyboard());

        // Send Query
        onView(withId(R.id.recipeSearchButton)).perform(click());

        // Check the search result size
        onView(withId(R.id.recipeSearchList))
                .check(ViewAssertions.matches(Matchers.withListSize(5)));
    }
}
