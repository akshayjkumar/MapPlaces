package com.ajdev.aroundme.presenter;

import android.location.Location;

import com.ajdev.aroundme.BaseApplicationTest;
import com.ajdev.aroundme.BuildConfig;
import com.ajdev.aroundme.dao.NearByPlacesDAO;
import com.ajdev.aroundme.datasource.NearByPlacesDS;
import com.ajdev.aroundme.datasource.NearByPlacesDSTest;
import com.ajdev.aroundme.model.NearByPlaces;
import com.ajdev.aroundme.network.ConnectionAPIInterface;
import com.ajdev.aroundme.view.activity.NearbyPlacesActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by Akshay.Jayakumar on 10/17/2017.
 *
 * This is a Robolectric unit test for testing NearByPlace presenter.
 *
 * @@@@@@@@@@@@@@@@@@@@@@@@  MAKE SURE CONFIGURATION IS CORRECT FOR ROBOLECTRIC UNIT TESTS
 * @@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@ Important : If Robolectric fails to run due to issues
 * @@@@@@@@@@@@@@@@@@@@@@@@ related to Android Manifest, then edit the test configuration
 * @@@@@@@@@@@@@@@@@@@@@@@@ and point the working directory to $MODULE_DIR$
 *
 *
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, application = BaseApplicationTest.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({})
public class NearByPlacesPresenterTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    NearByPlaces nearByPlaces;
    NearByPlacesPresenter testNearByPlacesPresenter;
    NearByPlacesDS testNearByPlacesDS;
    NearbyPlacesActivity nearbyPlacesActivity;
    Location testLocation;


    @Before
    public void setup(){

        /**
         * AndroidScheduler thread is unavailable during test. So configure prior to
         * test execution with Scheduler.immediate() to execute in current thread.
         */
        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });


        MockitoAnnotations.initMocks(this);
        // Mock of near by places data source
        testNearByPlacesDS = Mockito.mock(NearByPlacesDS.class);
        // Mock the view used by the presenter. NearByPlacesActivity
        nearbyPlacesActivity = Mockito.mock(NearbyPlacesActivity.class);
        // Create a mock location since search API requires current location (Just mock ;-) !!)
        testLocation = Mockito.mock(Location.class);
        // Set the dependencies for the presenter
        testNearByPlacesPresenter = new NearByPlacesPresenter(testNearByPlacesDS);
        // Bind the view to the presenter
        testNearByPlacesPresenter.bindView(nearbyPlacesActivity);

        nearByPlaces = NearByPlacesDSTest.returnTestData();
    }

    /** Test search invocation **/
    @Test
    public void testAPresenter(){
        Observable<NearByPlaces> observable = Observable.just(nearByPlaces);
        Mockito.when(testNearByPlacesDS.getNearByPlaceSubject()).thenReturn(observable);
        testNearByPlacesPresenter.search(testLocation,"",true);
        verify(nearbyPlacesActivity).toggleProgressBarVisibility(true);
    }

    /** Test view interaction when API response is successful **/
    @Test
    public void testBPresenterViewInteraction(){
        Observable<NearByPlaces> observable = Observable.just(nearByPlaces);
        Mockito.when(testNearByPlacesDS.getNearByPlaceSubject()).thenReturn(observable);
        testNearByPlacesPresenter.search(testLocation,"",true);
        verify(nearbyPlacesActivity).setSearchResultAdapter(anyList());
    }

    /** Test view interaction during error **/
    @Test
    public void testCPresenterViewInteractionOnError(){
        Mockito.when(testNearByPlacesDS.getNearByPlaceSubject()).thenReturn(Observable.<NearByPlaces>error(new Throwable("error")));
        testNearByPlacesPresenter.search(testLocation,"",true);
        verify(nearbyPlacesActivity,never()).updateAdapterLocation();
    }

    /** Clean up after test **/
    @After
    public void tearDown(){
        nearbyPlacesActivity = null;
        testNearByPlacesPresenter = null;
        testLocation = null;
        testNearByPlacesDS = null;
    }


}
