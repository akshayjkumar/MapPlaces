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
public class PhotoTest {

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testObjectConstruction() {
        String reference = "somePhotoReferenceId";
        Photo photo = new Photo(reference);
        /**
         * Assert that expected is the actual. Test passes if data passed along while object
         * creation is same as that's retrieved.
         **/
        Assert.assertNotNull(photo);
        Assert.assertEquals(reference, photo.getPhotoReference());
    }

}