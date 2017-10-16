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
public class GeometryTest {

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNotNull(){
        Double testLatitude = 25.0;
        Double testLongitude = 35.0;
        Location location = new Location(testLatitude, testLongitude);
        Geometry geometry = new Geometry(location);
        /**
         * Assert that expected is the actual. Test passes if location from geometry is not null
         **/
        Assert.assertNotNull(geometry.getLocation());
    }

    @Test
    public void testObjectConstruction() {
        Double testLatitude = 25.0;
        Double testLongitude = 35.0;
        Location location = new Location(testLatitude, testLongitude);
        Geometry geometry = new Geometry(location);
        /**
         * Assert that expected is the actual. Test passes if data passed along while object
         * creation is same as that's retrieved.
         **/
        Assert.assertEquals(testLatitude, geometry.getLocation().getLatitude());
        Assert.assertEquals(testLongitude, geometry.getLocation().getLongitude());
    }

    @Test
    public void testNoGetterConflicts(){
        Double testLatitude = 25.0;
        Double testLongitude = 35.0;
        Location location = new Location(testLatitude, testLongitude);
        Geometry geometry = new Geometry(location);
        /**
         * Assert that expected is the actual.
         * Test that there are no data conflicts in between latitude and longitude getters.
         **/
        Assert.assertTrue(geometry.getLocation().getLatitude() != geometry.getLocation().getLongitude());
    }
}