package com.ajdev.aroundme.model;

import android.content.Context;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.internal.RealmCore;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by Akshay.Jayakumar on 10/16/2017.
 *
 * Getters and setters are provided by Lambok.
 * If getter/ setters appears to be un-resolvable, install Lambok plugin for studio
 */

@RunWith(MockitoJUnitRunner.class)
public class ResultTest {

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testObjectConstruction() {
        String name = "randomName", placeID = "ABCDEFGHIJKLMONPQRST", vicinity = "place vicinity";
        float rating = 5.0f;
        Photo photo1 = new Photo("photoReferenceIdForPhoto1");
        Photo photo2 = new Photo("photoReferenceIdForPhoto2");
        List<Photo> photoList = Arrays.asList(photo1,photo2);
        Location location = new Location(0.1, 0.2);
        Geometry geometry = new Geometry(location);
        OpeningTime openingTime = new OpeningTime(false);

        RealmList<Photo> photoRealmList = new RealmList<Photo>((Photo[]) photoList.toArray());
        Result result = new Result(name,placeID,vicinity,rating,geometry,photoRealmList,openingTime);

        /**
         * Assert that expected is the actual. Test passes if data passed along while object
         * creation is same as that's retrieved.
         **/
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getGeometry());
        Assert.assertNotNull(result.getGeometry().getLocation());
        Assert.assertNotNull(result.getOpeningTime());
        Assert.assertNotNull(result.getPhotos());
    }

    @Test
    public void testData(){
        String name = "randomName", placeID = "ABCDEFGHIJKLMONPQRST", vicinity = "place vicinity";
        float rating = 5.0f;
        Photo photo1 = new Photo("photoReferenceIdForPhoto1");
        Photo photo2 = new Photo("photoReferenceIdForPhoto2");
        List<Photo> photoList = Arrays.asList(photo1,photo2);
        Location location = new Location(0.1, 0.2);
        Geometry geometry = new Geometry(location);
        OpeningTime openingTime = new OpeningTime(false);
        RealmList<Photo> photoRealmList = new RealmList<Photo>((Photo[]) photoList.toArray());
        Result result = new Result(name,placeID,vicinity,rating,geometry,photoRealmList,openingTime);

        // Test for data integrity
        Assert.assertEquals(name,result.getName());
        Assert.assertEquals(placeID,result.getPlaceID());
        Assert.assertEquals(vicinity,result.getVicinity());
        Assert.assertEquals(0.1,result.getGeometry().getLocation().getLatitude());
        Assert.assertEquals(0.2,result.getGeometry().getLocation().getLongitude());
        Assert.assertEquals(vicinity,result.getVicinity());

        // Test that realm list of photos contains photo 1 and 2
        Assert.assertEquals(photoList.size(),result.getPhotos().size());
        Assert.assertTrue(result.getPhotos().contains(photo1));
        Assert.assertTrue(result.getPhotos().contains(photo2));
        Assert.assertEquals(photo1.getPhotoReference(),result.getPhotos().get(0).getPhotoReference());
        Assert.assertEquals(photo2.getPhotoReference(),result.getPhotos().get(1).getPhotoReference());
    }
}