package com.ajdev.aroundme.model;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by Akshay.Jayakumar on 10/16/2017.
 *
 * Getters and setters are provided by Lambok.
 * If getter/ setters appears to be un-resolvable, install Lambok plugin for studio
 */

@RunWith(MockitoJUnitRunner.class)
public class NearByPlacesTest {

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testObjectConstruction() {
        /**
         * Create dummy data for test
         */
        String name1 = "randomName", placeID1 = "ABCDEFGHIJKLMONPQRST", vicinity1 = "place vicinity";
        float rating1 = 5.0f;
        Photo photo11 = new Photo("photoReferenceIdForPhoto11");
        Photo photo12 = new Photo("photoReferenceIdForPhoto12");
        List<Photo> photoList1 = Arrays.asList(photo11,photo12);
        Location location1 = new Location(0.1, 0.2);
        Geometry geometry1 = new Geometry(location1);
        OpeningTime openingTime1 = new OpeningTime(false);
        RealmList<Photo> photoRealmList1 = new RealmList<Photo>((Photo[]) photoList1.toArray());
        Result result1 = new Result(name1,placeID1,vicinity1,rating1,geometry1,photoRealmList1,openingTime1);
        /**
         * Create dummy data for test
         */
        String name2 = "randomName2", placeID2 = "UVQXYZABCDEFPOIUYTRE", vicinity2 = "place vicinity 2";
        float rating2 = 1.0f;
        Photo photo21 = new Photo("photoReferenceIdForPhoto21");
        Photo photo22 = new Photo("photoReferenceIdForPhoto22");
        List<Photo> photoList2 = Arrays.asList(photo21,photo22);
        Location location2 = new Location(2.1, 2.2);
        Geometry geometry2 = new Geometry(location2);
        OpeningTime openingTime2 = new OpeningTime(false);
        RealmList<Photo> photoRealmList2 = new RealmList<Photo>((Photo[]) photoList2.toArray());
        Result result2 = new Result(name2,placeID2,vicinity2,rating2,geometry2,photoRealmList2,openingTime2);

        List<Result> resultList = Arrays.asList(result1,result2);
        RealmList<Result> resultRealmList = new RealmList<Result>((Result[]) resultList.toArray());
        NearByPlaces nearByPlaces = new NearByPlaces(resultRealmList);

        /**
         * Assert that expected is the actual. Test passes if data passed along while object
         * creation is same as that's retrieved.
         * Test realm list contains data
         **/
        Assert.assertNotNull(nearByPlaces);
        Assert.assertNotNull(nearByPlaces.getResults());
        Assert.assertNotNull(nearByPlaces.getResults().contains(result1));
        Assert.assertNotNull(nearByPlaces.getResults().contains(result2));
        Assert.assertEquals(result1,nearByPlaces.getResults().get(0));
        Assert.assertEquals(result2,nearByPlaces.getResults().get(1));
        Assert.assertNotSame(nearByPlaces.getResults().get(0),nearByPlaces.getResults().get(1));
    }
}