package com.example.aharm.sporkly;

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

/**
 * Created by David on 11/16/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MyIngredientsActivityTest {
    private String testQuery;

    @Rule
    public ActivityTestRule<MyIngredientsActivity> mActivityRule = new ActivityTestRule<>(
            MyIngredientsActivity.class);


    @Before
    public void init() {
        testQuery = "apple";
    }

    @Test
    public void query() {
        // Edit the text box
        onView(withId(R.id.recipeText))
                .perform(typeText(testQuery), closeSoftKeyboard());

        // Send Query
        onView(withId(R.id.recipeButton)).perform(click());

        // Check the search result size
        onView(withId(R.id.recipeList))
                .check(ViewAssertions.matches(Matchers.withListSize(5)));
    }
}
