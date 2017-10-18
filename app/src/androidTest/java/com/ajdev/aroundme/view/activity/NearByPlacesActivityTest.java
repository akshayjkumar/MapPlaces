package com.ajdev.aroundme.view.activity;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import com.ajdev.aroundme.R;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NearByPlacesActivityTest {

    @Rule
    public ActivityTestRule<NearbyPlacesActivity> mActivityTestRule = new ActivityTestRule<>(NearbyPlacesActivity.class);

    @Test
    public void nearByPlacesActivityTest() {


        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.search_box_et), isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.search_box_et), isDisplayed()));
        appCompatEditText2.perform(replaceText("restaurant"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.search_box_et), withText("restaurant"), isDisplayed()));
        appCompatEditText3.perform(pressImeActionButton());

        // Added a sleep statement to match the app's execution delay. Should be replaced by
        // Espresso idling resources:
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.places_list_rv), isDisplayed()));


        RecyclerView rv = (RecyclerView) mActivityTestRule.getActivity()
                .findViewById(R.id.places_list_rv);

        // Added a sleep statement to match the app's execution delay. Should be replaced by
        // Espresso idling resources:
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Assert that search recycler view displays data after search.
        Assert.assertTrue(rv.getAdapter().getItemCount() > 1);
    }

}
