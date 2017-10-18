package com.ajdev.aroundme.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ajdev.aroundme.BaseApplication;
import com.ajdev.aroundme.R;
import com.ajdev.aroundme.adapters.SearchResultAdapter;
import com.ajdev.aroundme.model.Geometry;
import com.ajdev.aroundme.model.Result;
import com.ajdev.aroundme.utils.FusedLocationProvider;
import com.ajdev.aroundme.presenter.NearByPlacesPresenter;
import com.ajdev.aroundme.utils.Utilities;
import com.ajdev.aroundme.view.fragment.PlaceDetailsBSFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NearbyPlacesActivity extends RuntimePermissionsActivity
        implements FusedLocationProvider.LocationCallback,
        View.OnClickListener, PlaceDetailsBSFragment.BottomSheetListener{

    @Inject
    NearByPlacesPresenter nearByPlacesPresenter;

    @Inject
    SearchResultAdapter searchResultAdapter;

    @BindView(R.id.appbar)
    AppBarLayout appBar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.places_list_fl)
    FrameLayout placesListFL;

    @BindView(R.id.map_container_fl)
    FrameLayout mapContainerFL;

    @BindView(R.id.places_list_rv)
    RecyclerView placesListRV;

    @BindView(R.id.activity_root_view_cl)
    View rootView;

    @BindView(R.id.search_box_et)
    EditText searchBoxET;

    @BindView(R.id.quick_search_cinema_tv)
    TextView quickSearchCinema;

    @BindView(R.id.quick_search_atm_tv)
    TextView quickSearchATM;

    @BindView(R.id.quick_search_restaurant_tv)
    TextView quickSearchRestaurants;

    @BindView(R.id.quick_search_shopping_tv)
    TextView quickSearchShopping;

    @BindView(R.id.clear_search_ib)
    ImageButton clearSearchIB;

    @BindView(R.id.search_progress_bar)
    ProgressBar searchProgressBar;

    @BindView(R.id.quick_search_layout)
    View quickSearchView;

    // Google map instance
    private GoogleMap googleMap;

    // Google map fragment
    private MapFragment mapFragment;

    // FusedLocationProvider instance getting user location
    private FusedLocationProvider mLocationProvider;

    // Holds current location from location provider
    private Location location;

    // Callback to handle map ready event
    private OnMapReadyCallback mapReadyCallback;

    // SavedStateInstance for the map fragment
    private Bundle mapViewSavedInstanceState;

    // Butter knife un-binder reference
    private Unbinder viewUnBinder;

    // Flag to handle permission state for Location
    private boolean manualPermissionOverride = false;

    // Action listener for edit text view search ime action
    private TextView.OnEditorActionListener searchActionIME;

    // Bottom sheet behavior instance for handling bottom sheet frame layout
    private BottomSheetBehavior searchResultBottomSheetBehavior;

    // Bottom sheet callback to monitor events associated with bottom sheet
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;

    // Marker list to maintain all markers plotted in the map
    private HashMap<String, Result> markersList;

    // New bitmap descriptor for map pins
    private BitmapDescriptor defaultMarker;

    // Interpolator for action bar animation
    private AccelerateInterpolator accelerateInterpolator;

    // Map marker click listener
    private GoogleMap.OnMarkerClickListener markerClickListener;

    // Instance of network error alert dialog
    private AlertDialog networkErrorAlterDialog = null;

    // Fragment instance showing more details about the place
    private PlaceDetailsBSFragment placeDetailsBSFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places);

        // Bind the hostActivity for view injection. Unbind this butter knife instance after use.
        viewUnBinder =  ButterKnife.bind(this);

        // Inject this hostActivity for dependencies
        ((BaseApplication)getApplication()).getDependencyComponents().inject(this);

        // Bind the view to the presenter
        nearByPlacesPresenter.bindView(this);

        // Set a toolbar to act as an action bar
        setSupportActionBar(toolbar);

        // Set toolbar title
        setToolbarTitle();

        // Prepare search progress bar
        prepareSearchProgressBar();

        // Get bottom sheet behavior from associated container view
        searchResultBottomSheetBehavior = BottomSheetBehavior.from(placesListFL);
        bottomSheetCallback = getBottomSheetCallback(placesListFL);
        searchResultBottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);

        // Set location provider
        mLocationProvider = new FusedLocationProvider(getApplicationContext(),this);

        // Initialize marker list
        markersList = new HashMap<>();

        // New instance of Map Fragment
        if(mapFragment == null)
            mapFragment = MapFragment.newInstance();

        // Add map fragment to the hostActivity
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.map_container_fl,mapFragment).commit();

        // Get Map saved state instance
        mapViewSavedInstanceState = savedInstanceState != null ? savedInstanceState.getBundle(
                getString(R.string.google_map_state_instance)) : null;

        // Check if google play services are up-to-date in the user device
        checkGooglePlayServices(getApplicationContext());

        // Add search ime action listener for the Search box EditText view.
        setSearchIMEAction();
        searchBoxET.setOnEditorActionListener(searchActionIME);

        // Initialize click listeners on views.
        prepareViews();

        // Initialize and prepare search result listing recycler view
        prepareSearchResultAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        prepareSearchResultRecyclerView(llm,searchResultAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * Manual permission override is to continue location services
         * once user has manually set the location permissions on from
         * the settings.
         */
        if(manualPermissionOverride) {
            authorizeAndStart();
            manualPermissionOverride = false;
        }
    }

    @Override
    public void onPermissionGranted(int requestCode, @NonNull String[] permissions,
                                    @NonNull int[] grantResults) {
        /**
         * Permission response are handled here.
         * If requested permission is granted by the user, below condition is satisfied.
         */
        if (requestCode == REQUEST_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionCheck();
        }else{
            if(rootView != null)
                showPermissionsDeniedOrForeverSB(rootView);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        /**
                         * Check permission again, Turning on location API does not guaranty
                         * that location permission has been granted
                         */
                        permissionCheck();
                        enableMapLocations(true); // Enable Current locations on the map
                        break;
                    case Activity.RESULT_CANCELED:
                        /**
                         * If Location API is not turned on, show the SnackBar to prompt the user
                         * to turn Location API on.
                         */
                        showLocationAPISettingsSB(rootView);
                        break;
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Dismiss alert dialog is its showing. Not dismissing can result in window leak esception
        if(networkErrorAlterDialog != null && networkErrorAlterDialog.isShowing())
            networkErrorAlterDialog.dismiss();
        networkErrorAlterDialog = null;
        // Disconnect from the FLP when hostActivity is paused
        if (mLocationProvider != null)
            mLocationProvider.disconnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        /**
         * Unbind the butter knife instance after use.
         * Can hit illegal state exception if bindings are already cleared.
         */
        if(viewUnBinder != null) {
            try {
                viewUnBinder.unbind();
            } catch (Exception e) {
            }
        }
        // Perform clean up after use to prevent memory leaks.
        if(mLocationProvider != null)
            mLocationProvider.removelisteners();
        if (markersList != null) {
            markersList.clear();
            markersList = null;
        }
        mLocationProvider = null;
        googleMap = null;
        mapFragment = null;
        mapReadyCallback = null;
        searchActionIME = null;
        bottomSheetCallback = null;
        accelerateInterpolator = null;
        super.onDestroy();
    }

    /**
     * Location changes from the location provider will trigger this event when new location
     * is available.
     * @param location
     */
    @Override
    public void handleNewLocation(Location location) {
        onLocationChanged(location);
    }

    /**
     * Click listener for this hostActivity views
     * @param view
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.clear_search_ib:
                clearSearch();
                return;
            case R.id.quick_search_atm_tv:
                searchBoxET.setText(R.string.places_api_type_atm);
                break;
            case R.id.quick_search_restaurant_tv:
                searchBoxET.setText(R.string.places_api_type_restaurant);
                break;
            case R.id.quick_search_cinema_tv:
                searchBoxET.setText(R.string.places_api_type_movie_theater);
                break;
            case R.id.quick_search_shopping_tv:
                searchBoxET.setText(R.string.places_api_shopping_mall);
                break;
        }
        searchForPoints(true);
    }

    /**
     * Callback method for place details bottom sheet scroll events.
     */
    @Override
    public void toggleAppBar(boolean status) {
    }

    /**
     * Initialize google maps. Listener will provide callback when the map is
     * loaded completely.
     * @param savedInstanceState
     */
    public void initGoogleMaps(Bundle savedInstanceState) {
        // Initialize bitmap descriptor for map pins
        try {
            defaultMarker = BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.drawable_map_pin, 128, 128));
        }catch (Exception e){
            e.printStackTrace();
        }
        // Initialize google map
        if(googleMap == null) {
            mapFragment.onCreate(savedInstanceState);
            if(mapReadyCallback == null)
                mapReadyCallback = getMapReadyCallback();
            mapFragment.getMapAsync(mapReadyCallback);
        }
    }

    /**
     * Initialize listener to interact with map markers
     */
    private void initMarkerClickListener(){
        if(markerClickListener == null) {
            markerClickListener = new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String placeId = marker.getTag().toString();
                    marker.showInfoWindow();
                    if (markersList != null && markersList.containsKey(placeId))
                        morePlaceInformation(markersList.get(placeId));
                    return true;
                }
            };
        }
    }

    /** Method to set toolbar title **/
    private void setToolbarTitle(){
        if(toolbar != null)
            toolbar.setTitle(R.string.app_name);
    }

    /**
     * Callback to handle onMapReady event. This callback will be fired
     * when map has loaded completely.
     */
    private OnMapReadyCallback getMapReadyCallback(){
        return new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMapObj) {
                googleMap = googleMapObj;
                // Enable toolbar for direction and maps
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.getUiSettings().setAllGesturesEnabled(true);
                // Check if permissions are granted and start location services
                permissionCheck();
                // Enable Current locations on the map
                enableMapLocations(true);
                // Map marker click listener
                initMarkerClickListener();
                googleMap.setOnMarkerClickListener(markerClickListener);
            }
        };
    }

    /**
     * Method to enable user location on the google map.
     * Enabling user location requires permission and this is
     * handled by the base hostActivity.
     *
     * Suppressing the permission lint warning as explicit permission check is in place
     */
    @SuppressWarnings("MissingPermission")
    private void enableMapLocations(boolean enable) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(getPermissionStatus(this
                        ,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION})
                        == RuntimePermissionsActivity.PERMISSION_GRANTED) {
                    if (googleMap != null) googleMap.setMyLocationEnabled(enable);
                }
            }else {
                if (googleMap != null) googleMap.setMyLocationEnabled(enable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method updates the location. Current user location is used for
     * querying the NearByPlaces API
     * @param location
     */
    public void onLocationChanged(Location location) {
        this.location = location;
        repositionMap(location);
        updateLocation(location);
    }

    /**
     * When user location is available, update map position based on the location coordinates.
     * @param location
     */
    private void repositionMap(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    /**
     * This method checks if the device google play services are up-to-date
     * @param context
     * @return
     */
    private void checkGooglePlayServices(Context context) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int code = googleAPI.isGooglePlayServicesAvailable(context);
        switch (code){
            case ConnectionResult.SUCCESS: // Play services are up-to-date
                initGoogleMaps(mapViewSavedInstanceState); // Initialize Google maps
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                // Play services need to be updated before this app can proceed
                break;
        }
    }

    /**
     * Check if location setting is enabled and commence resolving for current user location
     * using the FLP. Locations changes will trigger handleNewLocation() event.
     */
    public void authorizeAndStart() {
        if (mLocationProvider != null) {
            mLocationProvider.connect();
            if (!mLocationProvider.isGPSLocationEnabled()) {
                mLocationProvider.promptLocationSettingsChange(this, REQUEST_CHECK_SETTINGS);
            }else{
                enableMapLocations(true);
            }
        }
    }

    /*
     * Check for permissions to access location using Google play services and connect to the
     * google play service
     */
    public void permissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /**
             * If permission is granted already, call locationSettings Request for
             * enabling location access.
             **/
            int response = requestPermissionFromActivity(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    new int[]{REQUEST_FINE_LOCATION},
                    new String[]{getString(R.string.permission_location_rationale)});
            switch (response) {
                case RuntimePermissionsActivity.PERMISSION_GRANTED:
                    /** Start listening for location **/
                    authorizeAndStart();
                    break;
                default:
                    showPermissionsDeniedOrForeverSB(rootView);
                    break;
            }
        } else {
            /** Start listening for location directly because explicit permissions not required **/
            authorizeAndStart();
        }
    }

    /**
     * SnackBar to notify user that location permission has been denied/ denied forever.
     * Denied scenario prompts the user to enable the location permission.
     * Denied Forever scenario requires the user to manually enable the permission from
     * the device settings.
     */
    public void showPermissionsDeniedOrForeverSB(View rootView) {
        final int code = getPermissionStatus(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION});
        int rationaleText = (code == RuntimePermissionsActivity.PERMISSION_DENIED_FOREVER) ?
                R.string.location_permission_denied_forever_rationale
                : R.string.location_permission_denied_rationale;
        int actionText = (code == RuntimePermissionsActivity.PERMISSION_DENIED_FOREVER) ?
                R.string.snackbar_action_open_settings
                : R.string.snackbar_action_enable;
        Snackbar.make(rootView, rationaleText, Snackbar.LENGTH_INDEFINITE)
                .setAction(actionText, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(code == RuntimePermissionsActivity.PERMISSION_DENIED_FOREVER) {
                            manualPermissionOverride = true;
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts(getString(R.string.res_package)
                                    , getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }else{
                            requestSinglePermission(NearbyPlacesActivity.this
                                    ,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}
                                    ,REQUEST_FINE_LOCATION);
                        }
                    }
                }).show();
    }

    /**
     * SnackBar to notify user that locations API hasn't been turned on.
     * Location API should be on to access users location.
     */
    public void showLocationAPISettingsSB(View rootView) {
        Snackbar.make(rootView, R.string.location_settings_rationale, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.snackbar_action_enable, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        authorizeAndStart();
                    }
                }).show();
    }

    /**
     * Fetch updated location
     * @return
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Update location that is received from FLP
     * @param location
     */
    public void updateLocation(Location location) {
        this.location = location;
    }

    /**
     * Search ime action for the search box EditText view
     */
    private void setSearchIMEAction(){
        searchActionIME = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchForPoints(false);
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * Search NearByPlaces API for points. List ths result in Search result list.
     * Near by places API provides searching by Type and by Name.
     * Type is usually attractions like ATm, Shopping, Bus etc.
     * Search from the search bar will use Name to search rather than Tag.
     */
    private void searchForPoints(boolean type){
        // Hide the keyboard
        Utilities.hideKeyboard(this);
        if(searchBoxET != null
                && Utilities.isConnected(this)){
            String search = searchBoxET.getText().toString();
            if(!search.isEmpty() && location != null){
                nearByPlacesPresenter.search(location,search.trim(),type);
            }else
                permissionCheck();
        }else{
            displayConnectivityErrorAlert();
        }
    }

    /**
     * Initialize and configure search recycler view. Set layout manager, underlying data adapter.
     * @param llm
     * @param adapter
     */
    private void prepareSearchResultRecyclerView(LinearLayoutManager llm, SearchResultAdapter adapter){
        // Search result recycler view
        if(placesListRV != null) {
            placesListRV.setLayoutManager(llm);
            placesListRV.setHasFixedSize(true);
            placesListRV.setAdapter(adapter);
        }
    }

    /**
     * Set new data for the search list recycler view adapter
     * @param resultList
     */
    public void setSearchResultAdapter(List<Result> resultList) {
        if(searchResultAdapter != null && resultList != null) {
            searchResultAdapter.setDataList(resultList);
            if(resultList.size() == 0) {
                Toast.makeText(getApplicationContext(), "No results found", Toast.LENGTH_LONG).show();
                toggleQuickSearchView(true);
            }else {
                toggleQuickSearchView(false);
                plotBranches(resultList);
            }
        }
    }

    /**
     * Update adapter location with current users location
     */
    public void updateAdapterLocation(){
        searchResultAdapter.setLocation(location);
    }

    /**
     * Clears search list recycler view adapter
     * and clears search text.
     */
    public void clearSearch() {
        if(searchBoxET != null)
            searchBoxET.setText("");
        if(searchResultAdapter != null)
            searchResultAdapter.reset();
        if(googleMap != null)
            googleMap.clear();
        if(location != null)
            repositionMap(location);
        toggleQuickSearchView(true);
    }

    /**
     * Bottom sheet callback events.
     * When the offset changes, adjust the background transparency of the hosting view
     * Stage changes also could be handled if required.
     * @param targetView
     * @return
     */
    public static BottomSheetBehavior.BottomSheetCallback getBottomSheetCallback(final View targetView){
        return new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                /**
                 * Not used. However retaining for future enhancements.
                 */
                switch (newState) {
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Change the bottom sheet background alpha based on the offset
                // RGB value 240,240,240 represents shade of light gray
                if (slideOffset >= 0 && slideOffset <= 1) {
                    targetView.setBackgroundColor(
                            Color.argb((int) (255 * slideOffset), 240, 240, 240));
                }
            }
        };
    }

    /**
     * Change the default colorAccent of the progress bar to match the theme.
     */
    private void prepareSearchProgressBar() {
        Drawable progressDrawable = searchProgressBar.getIndeterminateDrawable();
        progressDrawable.setColorFilter(ContextCompat.getColor(
                this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
    }

    /**
     * When search for near by places is in-flight, toggle visibility of the search clear button
     * to hidden and show the progress bar. This is determined by the status flag.
     * If status = true, show progress bar and hide search clear button. This will be
     * used by the presenter during search API invocation.
     * @param status
     */
    public void toggleProgressBarVisibility(boolean status){
        if(searchProgressBar != null && clearSearchIB != null){
            if(status){
                searchProgressBar.setVisibility(View.VISIBLE);
                clearSearchIB.setVisibility(View.INVISIBLE);
            }else{
                clearSearchIB.setVisibility(View.VISIBLE);
                searchProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * When search is complete as results are available, hide quick search
     * content. Clearing search will toggle the visibility of quick search content
     * back.
     * @param status
     */
    private void toggleQuickSearchView(boolean status){
        if(quickSearchView != null){
            if(status) {
                quickSearchView.setVisibility(View.VISIBLE);
                searchResultBottomSheetBehavior.setPeekHeight(
                        Utilities.convertDPToPixels(getApplicationContext(),110));
            }else {
                quickSearchView.setVisibility(View.GONE);
                searchResultBottomSheetBehavior.setPeekHeight(
                        Utilities.convertDPToPixels(getApplicationContext(),100));
            }
        }
    }

    /**
     * Set click listener for handling clicks on views such as
     * quick search and clear search
     */
    private void prepareViews() {
        if(clearSearchIB != null)
            clearSearchIB.setOnClickListener(this);
        if(quickSearchCinema != null)
            quickSearchCinema.setOnClickListener(this);
        if(quickSearchATM != null)
            quickSearchATM.setOnClickListener(this);
        if(quickSearchRestaurants != null)
            quickSearchRestaurants.setOnClickListener(this);
        if(quickSearchShopping != null)
            quickSearchShopping.setOnClickListener(this);
        if(accelerateInterpolator == null)
            accelerateInterpolator = new AccelerateInterpolator();
        toggleQuickSearchView(true);
    }

    /**
     * Bind search result list adapter with listeners to handle click events
     */
    private void prepareSearchResultAdapter() {
        searchResultAdapter.setOnItemClickListener(new SearchResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Result result) {
                morePlaceInformation(result);
            }
        });
    }

    private void morePlaceInformation(Result result){
        if(result != null) {
            // Create new instance of the bottom sheet fragment
            if (placeDetailsBSFragment == null)
                placeDetailsBSFragment = PlaceDetailsBSFragment.newInstance();
            // Set place name
            placeDetailsBSFragment.setPlaceName(result.getName());
            // Set place id
            placeDetailsBSFragment.setPlaceID(result.getPlaceID());
            // Set place rating
            placeDetailsBSFragment.setPlaceRating(result.getRating());
            // Set place photo reference id
            String photoReferenceId = null;
            if(result.getPhotos() != null && !result.getPhotos().isEmpty())
                photoReferenceId = (result.getPhotos().get(0) != null) ?
                        result.getPhotos().get(0).getPhotoReference() : null;
            placeDetailsBSFragment.setPhotoReferenceId(photoReferenceId);
            // Set place geo coordinates
            String geoCords = "0,0";
            if(result.getGeometry() != null && result.getGeometry().getLocation() != null)
                geoCords = result.getGeometry().getLocation().getLatitude() + "," +
                        result.getGeometry().getLocation().getLongitude();
            placeDetailsBSFragment.setPlaceGeo(geoCords);
            // Set user location (if any in the future use)
            placeDetailsBSFragment.setUserLocation(location);
            // Show the fragment
            //FragmentManager fragmentManager = getSupportFragmentManager();
            //fragmentManager.beginTransaction().add(placeDetailsBSFragment,PlaceDetailsBSFragment.TAG).commitAllowingStateLoss();
            placeDetailsBSFragment.show(getSupportFragmentManager(), placeDetailsBSFragment.TAG);
        }
    }

    // Plot and Display markers for respective branch locations.
    private void plotBranches(final List<Result> resultList) {
        try {
            googleMap.clear(); // Clear all markers previously loaded if any
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Clear all marker elements
                    if (markersList != null) {
                        markersList.clear();
                    }

                    for (final Result result : resultList) {
                        double lat = 0, lon = 0;
                        /**
                         * Get latitude and longitude values for a given location/ point/ place
                         * from the list
                         */
                        Geometry geometry = result.getGeometry();
                        if (geometry != null) {
                            com.ajdev.aroundme.model.Location location = geometry.getLocation();
                            if(location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                        /**
                         * Plot the marker onto the map canvas only if location data is available
                         * for a given location/ point/ place. When the marker is ready
                         * post a runnable to the main ui thread to plot it on the
                         * map canvas. This is to prevent blocking of the the UI since this whole
                         * task is cpu intensive.
                         */
                        if (lat > 0 && lon > 0) {
                            final MarkerOptions marker = new MarkerOptions().position(
                                    new LatLng(lat, lon));
                            // Adding marker title
                            marker.title(result.getName());
                            // Adding marker to the map canvas
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    Marker markerG = googleMap.addMarker(marker);
                                    // Changing marker icon
                                    markerG.setIcon(defaultMarker);
                                    markerG.setTag(result.getPlaceID());
                                    markersList.put(result.getPlaceID(), result);
                                }
                            };
                            runOnUiThread(runnable);
                        }
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Bitmap resize method. Returns a new scaled/ resized bitmap.
     * @param id
     * @param width
     * @param height
     * @return
     */
    public Bitmap resizeMapIcons(int id, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), id);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    /**
     * Display connectivity error to the user
     */
    public void displayConnectivityErrorAlert(){
        networkErrorAlterDialog = Utilities.showNetworkErrorDialog(this);
    }
}
