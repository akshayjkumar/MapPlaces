package com.ajdev.aroundme.presenter;

import android.location.Location;

import com.ajdev.aroundme.BaseApplicationTest;
import com.ajdev.aroundme.BuildConfig;
import com.ajdev.aroundme.datasource.NearByPlacesDS;
import com.ajdev.aroundme.datasource.NearByPlacesDSTest;
import com.ajdev.aroundme.datasource.PlaceDetailsDS;
import com.ajdev.aroundme.datasource.PlaceDetailsDSTest;
import com.ajdev.aroundme.model.NearByPlaces;
import com.ajdev.aroundme.model.PlaceDetails;
import com.ajdev.aroundme.model.PlaceResult;
import com.ajdev.aroundme.view.activity.NearbyPlacesActivity;
import com.ajdev.aroundme.view.fragment.PlaceDetailsBSFragment;

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
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.any;
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
public class PlaceDetailsPresenterTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    PlaceDetails placeDetails;
    PlaceDetailsPresenter testPlaceDetailsPresenter;
    PlaceDetailsDS testPlaceDetailsDS;
    PlaceDetailsBSFragment placeDetailsBSFragment;


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
        // Mock of place details data source
        testPlaceDetailsDS = Mockito.mock(PlaceDetailsDS.class);
        // Mock the view used by the presenter. PlaceDetailsBSFragment
        placeDetailsBSFragment = Mockito.mock(PlaceDetailsBSFragment.class);
        // Set the dependencies for the presenter
        testPlaceDetailsPresenter = new PlaceDetailsPresenter(testPlaceDetailsDS);
        // Bind the view to the presenter
        testPlaceDetailsPresenter.bindView(placeDetailsBSFragment);

        placeDetails = PlaceDetailsDSTest.returnTestData();
    }

    /** Test search invocation and verify presenter view interaction**/
    @Test
    public void testAPresenterViewInteraction(){
        Observable<PlaceDetails> observable = Observable.just(placeDetails);
        Mockito.when(testPlaceDetailsDS.getPlaceDetailsSubject()).thenReturn(observable);
        testPlaceDetailsPresenter.search("");// Search any String for testing since response is mocked
        verify(placeDetailsBSFragment).updateData((PlaceResult) any());
    }

    /** Test view interaction during error **/
    @Test
    public void testBPresenterViewInteractionOnError(){
        Mockito.when(testPlaceDetailsDS.getPlaceDetailsSubject()).thenReturn(Observable.<PlaceDetails>error(new Throwable("error")));
        testPlaceDetailsPresenter.search(""); // Search any String for testing since response is mocked
        verify(placeDetailsBSFragment,never()).updateData((PlaceResult) any());
    }

    /** Clean up after test **/
    @After
    public void tearDown(){
        placeDetails = null;
        testPlaceDetailsPresenter = null;
        testPlaceDetailsDS = null;
        placeDetailsBSFragment = null;
    }


}
