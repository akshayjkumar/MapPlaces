package com.ajdev.aroundme.datasource;

import com.ajdev.aroundme.BaseApplicationTest;
import com.ajdev.aroundme.BuildConfig;
import com.ajdev.aroundme.dao.PlaceDetailsDAO;
import com.ajdev.aroundme.model.NearByPlaces;
import com.ajdev.aroundme.model.PlaceDetails;
import com.ajdev.aroundme.model.PlaceResult;
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

import rx.Observable;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;

/**
 * Created by Akshay.Jayakumar on 10/16/2017.
 *
 * This is a Robolectric unit test for testing PlaceDetails data source.
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

public class PlaceDetailsDSTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    ConnectionAPIInterface connectionAPIInterface;
    PlaceDetailsDAO placeDetailsDAO;
    PlaceDetailsDS placeDetailsDS;
    PlaceDetails testPlaceDetails;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        // Create mock objects of the dependent class
        connectionAPIInterface = Mockito.mock(ConnectionAPIInterface.class);
        placeDetailsDAO= Mockito.mock(PlaceDetailsDAO.class);

        // Create place details data source instance with mock objects
        placeDetailsDS = new PlaceDetailsDS(connectionAPIInterface, placeDetailsDAO);

        /**
         * Test getPlaceDetails APIs defined in ConnectionAPI interface.
         * Mock the server interaction for the place details data source, fetching
         * information from the API
         */
        testPlaceDetails = returnTestData(); // Get the test data
        Observable<PlaceDetails> observable = Observable.just(testPlaceDetails);
        // Mock the response from server to test the retrofit implementation in the data source
        Mockito.when(connectionAPIInterface.getPlaceDetails(Mockito.anyString(),Mockito.anyString()))
                .thenReturn(observable);
    }

    @Test
    public void testFetchPlaceDetails() throws Exception {
        /**
         * Perform the test with observer and subscriber execution on the same thread
         * hence Scheduler.immediate() which executes in the current thread.
         */
        TestSubscriber<PlaceDetails> subscriber = new TestSubscriber<>();
        placeDetailsDS.fetchPlaceDetailsFromServer("placeID")
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(subscriber);

        // Assert that data is returned from the mock service call has one PlaceDetails item
        Assert.assertEquals(1,subscriber.getOnNextEvents().size());
        // Assert that Place details returns, Result object
        Assert.assertNotNull(subscriber.getOnNextEvents().get(0).getResult());
        // Check for data integrity
        Assert.assertEquals(testPlaceDetails.getResult().getFormattedAddress(),
                subscriber.getOnNextEvents().get(0).getResult().getFormattedAddress());
        Assert.assertEquals(testPlaceDetails.getResult().getPhoneNumber(),
                subscriber.getOnNextEvents().get(0).getResult().getPhoneNumber());
        Assert.assertEquals(testPlaceDetails.getResult().getPhoneNumberInternational(),
                subscriber.getOnNextEvents().get(0).getResult().getPhoneNumberInternational());
        Assert.assertEquals(testPlaceDetails.getResult().getRating(),
                subscriber.getOnNextEvents().get(0).getResult().getRating());
        Assert.assertEquals(testPlaceDetails.getResult().getVicinity(),
                subscriber.getOnNextEvents().get(0).getResult().getVicinity());
    }


    @After
    public void tearDown(){
        connectionAPIInterface = null;
        placeDetailsDAO = null;
        placeDetailsDS = null;
        testPlaceDetails = null;
    }

    @Test
    public void testFetchPlaceDetailsFromRealm(){
        /**
         * Since data base persistence in not in scope of the implemented app.
         * However the data source provides mechanism, should Realm be implemented
         * in the future. Until then, this would provide nothing.
         *
         * Data base operations are ideally asynchronous. However, test will be performed
         * with observer and subscriber to be executed on the same thread using
         * Schedulers.immediate()
         */
        TestSubscriber<PlaceDetails> subscriber = new TestSubscriber<>();
        placeDetailsDS.fetchPlaceDetailsFromRealm()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(subscriber);
        // Assert that this observable emits nothing
        subscriber.assertNoValues();
        subscriber.assertValueCount(0);
        Assert.assertTrue(subscriber.getOnNextEvents().isEmpty());
    }

    public static PlaceDetails returnTestData(){
        /**
         * Create dummy data for unit test
         */
        float rating = 5.0f;
        String formattedAddress = "formattedAddress", phoneNumber = "phoneNumber",
                intPhoneNumber = "internationalPhoneNumber", vicinity = "place vicinity";
        PlaceResult placeResult = new PlaceResult(formattedAddress,phoneNumber,
                intPhoneNumber,rating,vicinity);
        return new PlaceDetails(placeResult);
    }
}