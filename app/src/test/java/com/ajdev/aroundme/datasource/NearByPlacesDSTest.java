package com.ajdev.aroundme.datasource;

import android.content.Context;

import com.ajdev.aroundme.BaseApplicationTest;
import com.ajdev.aroundme.BuildConfig;
import com.ajdev.aroundme.dao.NearByPlacesDAO;
import com.ajdev.aroundme.model.Geometry;
import com.ajdev.aroundme.model.Location;
import com.ajdev.aroundme.model.NearByPlaces;
import com.ajdev.aroundme.model.OpeningTime;
import com.ajdev.aroundme.model.Photo;
import com.ajdev.aroundme.model.PlaceDetails;
import com.ajdev.aroundme.model.Result;
import com.ajdev.aroundme.network.ConnectionAPIInterface;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import io.realm.RealmList;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Akshay.Jayakumar on 10/16/2017.
 *
 * This is a Robolectric unit test for testing NearByPlace data source.
 * Tests implemented here will only focus on the Retrofit server API part
 * of the data source. Since data is not persisted in the app, Realm testing is omitted.
 *
 *
 * @@@@@@@@@@@@@@@@@@@@@@@@  MAKE SURE CONFIGURATION IS CORRECT FOR ROBOLECTRIC UNIT TESTS
 * @@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@ Important : If Robolectric fails to run due to issues
 * @@@@@@@@@@@@@@@@@@@@@@@@ related to Android Manifest, then edit the test configuration
 * @@@@@@@@@@@@@@@@@@@@@@@@ and point the working directory to $MODULE_DIR$
 *
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, application = BaseApplicationTest.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({})

public class NearByPlacesDSTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    NearByPlaces nearByPlaces;
    ConnectionAPIInterface connectionAPIInterface;
    NearByPlacesDAO nearByPlacesDAO;
    android.location.Location testLocation;
    private NearByPlacesDS testNearByPlacesDS;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        // Create mock objects of the dependent class
        connectionAPIInterface = Mockito.mock(ConnectionAPIInterface.class);
        nearByPlacesDAO = Mockito.mock(NearByPlacesDAO.class);
        testLocation = Mockito.mock(android.location.Location.class);

        // Create near by places data source instance with mock objects
        testNearByPlacesDS = new NearByPlacesDS(connectionAPIInterface, nearByPlacesDAO);

        /**
         * There are two types of APIs defined in ConnectionAPI interface.
         * 1) Search for near by location based on type
         * 2) Search for near by location based on name
         */
        nearByPlaces = returnTestData();
        Observable<NearByPlaces> observable = Observable.just(nearByPlaces);
        // Search by type - Mock the response from server to test the retrofit implementation in the data source
        Mockito.when(connectionAPIInterface.getNearByPlacesByType(
                Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString()))
                .thenReturn(observable);

        // Search by name - Mock the response from server (to test the retrofit implementation in the data source
        Mockito.when(connectionAPIInterface.getNearByPlaces(
                Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString()))
                .thenReturn(observable);
    }

    @Test
    public void testFetchNearByPlaceByName() throws Exception {
        /**
        * Perform the test with observer and subscriber execution on the same thread
        * hence Scheduler.immediate() which executes in the current thread.
        */
        TestSubscriber<NearByPlaces> subscriber = new TestSubscriber<>();
        testNearByPlacesDS.fetchNearByPlaceFromServer(testLocation,"search_name",false)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(subscriber);

        // Assert that data is returned from the mock service call has one NearByPlace item
        Assert.assertEquals(1,subscriber.getOnNextEvents().size());
        // Assert that NearByPlace has two results item
        Assert.assertEquals(2, subscriber.getOnNextEvents().get(0).getResults().size());
        // Check for data integrity
        Assert.assertEquals(nearByPlaces.getResults().get(0).getName(),
                subscriber.getOnNextEvents().get(0).getResults().get(0).getName());
        Assert.assertEquals(nearByPlaces.getResults().get(0).getGeometry(),
                subscriber.getOnNextEvents().get(0).getResults().get(0).getGeometry());
        Assert.assertEquals(nearByPlaces.getResults().get(0).getPhotos(),
                subscriber.getOnNextEvents().get(0).getResults().get(0).getPhotos());
    }

    @Test
    public void testFetchNearByPlaceByType() throws Exception {
        /**
        * Perform the test with observer and subscriber execution on the same thread
        * hence Scheduler.immediate() which executes in the current thread.
        */
        TestSubscriber<NearByPlaces> subscriber = new TestSubscriber<>();
        testNearByPlacesDS.fetchNearByPlaceFromServer(testLocation,"search_type",true)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(subscriber);

        // Assert that data is returned from the mock service call has one NearByPlace item
        Assert.assertEquals(1,subscriber.getOnNextEvents().size());
        // Assert that NearByPlace has two results item
        Assert.assertEquals(2, subscriber.getOnNextEvents().get(0).getResults().size());
        // Check for data integrity
        Assert.assertEquals(nearByPlaces.getResults().get(0).getName(),
                subscriber.getOnNextEvents().get(0).getResults().get(0).getName());
        Assert.assertEquals(nearByPlaces.getResults().get(0).getGeometry(),
                subscriber.getOnNextEvents().get(0).getResults().get(0).getGeometry());
        Assert.assertEquals(nearByPlaces.getResults().get(0).getPhotos(),
                subscriber.getOnNextEvents().get(0).getResults().get(0).getPhotos());
    }

    @Test
    public void testFetchNearByPlaceFromRealm(){
        /**
         * Since data base persistence in not in scope of the implemented app.
         * However the data source provides mechanism, should Realm be implemented
         * in the future. Until then, this would provide nothing.
         *
         * Data base operations are ideally asynchronous. However, test will be performed
         * with observer and subscriber to be executed on the same thread using
         * Schedulers.immediate()
         */
        TestSubscriber<NearByPlaces> subscriber = new TestSubscriber<>();
        testNearByPlacesDS.fetchNearByPlaceFromRealm()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(subscriber);
        // Assert that this observable emits nothing
        subscriber.assertNoValues();
        subscriber.assertValueCount(0);
        Assert.assertTrue(subscriber.getOnNextEvents().isEmpty());
    }

    @After
    public void tearDown(){
        connectionAPIInterface = null;
        nearByPlacesDAO = null;
        testNearByPlacesDS = null;
        testLocation = null;
        nearByPlaces = null;
    }

    public static NearByPlaces returnTestData(){
        /**
         * Create dummy data for unit testing
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
        return new NearByPlaces(resultRealmList);
    }
}