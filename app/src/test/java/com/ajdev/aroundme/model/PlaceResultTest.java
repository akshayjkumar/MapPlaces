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
public class PlaceResultTest {

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testObjectConstruction() {
        String formattedAddress = "formattedAddress", phoneNumber = "phoneNumber"
                ,intPhoneNumber = "internationalPhoneNumber", vicinity = "place vicinity";
        float rating = 5.0f;
        PlaceResult placeResult = new PlaceResult(formattedAddress,phoneNumber
                ,intPhoneNumber,rating,vicinity);
        /**
         * Assert that expected is the actual. Test passes if data passed along while object
         * creation is same as that's retrieved.
         **/
        Assert.assertNotNull(placeResult);
    }

    @Test
    public void testData(){
        String formattedAddress = "formattedAddress", phoneNumber = "phoneNumber"
                ,intPhoneNumber = "internationalPhoneNumber", vicinity = "place vicinity";
        float rating = 5.0f;
        PlaceResult placeResult = new PlaceResult(formattedAddress,phoneNumber
                ,intPhoneNumber,rating,vicinity);
        /**
         * Test for data integrity
         **/
        Assert.assertEquals(formattedAddress,placeResult.getFormattedAddress());
        Assert.assertEquals(phoneNumber,placeResult.getPhoneNumber());
        Assert.assertEquals(intPhoneNumber,placeResult.getPhoneNumberInternational());
        Assert.assertEquals(rating,placeResult.getRating());
        Assert.assertEquals(vicinity,placeResult.getVicinity());
    }
}