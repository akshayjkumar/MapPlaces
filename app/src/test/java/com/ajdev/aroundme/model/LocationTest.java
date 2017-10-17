package com.ajdev.aroundme.model;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Akshay.Jayakumar on 10/16/2017.
 *
 * Getters and setters are provided by Lambok.
 * If getter/ setters appears to be un-resolvable, install Lambok plugin for studio
 */

@RunWith(MockitoJUnitRunner.class)
public class LocationTest {

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testObjectConstruction() {
        Double testLatitude = 25.0;
        Double testLongitude = 35.0;
        Location location = new Location(testLatitude, testLongitude);
        /**
         * Assert that expected is the actual. Test passes if data passed along while object
         * creation is same as that's retrieved.
         **/
        Assert.assertEquals(testLatitude, location.getLatitude());
        Assert.assertEquals(testLongitude, location.getLongitude());
    }

    @Test
    public void testNoGetterConflicts(){
        Double testLatitude = 25.0;
        Double testLongitude = 35.0;
        Location location = new Location(testLatitude, testLongitude);
        /**
         * Assert that expected is the actual.
         * Test that there are no data conflicts in between latitude and longitude getters.
         **/
        Assert.assertTrue(location.getLatitude() != location.getLongitude());
    }
}