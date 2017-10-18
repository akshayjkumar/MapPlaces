package com.ajdev.aroundme.view.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ajdev.aroundme.BaseApplication;
import com.ajdev.aroundme.R;
import com.ajdev.aroundme.model.PlaceResult;
import com.ajdev.aroundme.presenter.PlaceDetailsPresenter;
import com.ajdev.aroundme.utils.FontManager;
import com.ajdev.aroundme.utils.Utilities;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 */
@Getter
@Setter
public class PlaceDetailsBSFragment extends BottomSheetDialogFragment {

    @Inject
    PlaceDetailsPresenter placeDetailsPresenter;

    @BindView(R.id.place_details_container_LL)
    LinearLayout placeDetailsContainerLL;

    @BindView(R.id.place_details_image_iv)
    ImageView placeDetailsImageIV;

    @BindView(R.id.map_direction_fab)
    FloatingActionButton directionFab;

    @BindView(R.id.place_name_tv)
    TextView placeNameTV;

    @BindView(R.id.place_rating)
    TextView placeRatingTV;

    @BindView(R.id.place_rating_visual)
    TextView placeStarRatingTV;

    @BindView(R.id.place_address_tv)
    TextView placeAddressTV;

    @BindView(R.id.place_phone_primary_tv)
    TextView placePhoneTV;

    @BindView(R.id.place_vicinity_tv)
    TextView placeVicinityTV;

    // Tag for the fragment
    public static final String TAG = PlaceDetailsBSFragment.class.getName();

    // String keys string literals in bundle extra
    public static final String KEY_PLACE_ID = "place_id";
    public static final String KEY_PLACE_NAME = "place_name";
    public static final String KEY_PLACE_RATING = "place_rating";
    public static final String KEY_PLACE_PHOTO_REF_ID = "place_photo_ref_id";
    public static final String KEY_PLACE_GEO = "place_latitude_longitude";

    // Bottoms sheet interaction behavior for the view
    private BottomSheetBehavior bottomSheetBehavior;

    // Callback for monitoring events about bottom sheets
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback;

    // Butter knife un-binder reference
    private Unbinder viewUnBinder;

    // Host hostActivity instance
    private Activity hostActivity;

    // Place id
    private String placeID = "";

    // Place geo coordinates
    private String placeGeo = "";

    // Place name
    private String placeName;

    // Photo reference id for the place
    private String photoReferenceId;

    // Rating given for the place
    private float placeRating;

    // Root view instance
    private View contentView;

    // Listener for custom events in bottom sheet. To be implemented by hosting hostActivity
    private BottomSheetListener bListener;

    //Click listeners for interactive actions
    private View.OnClickListener actionClickListener;

    // Click listeners for FAB direction using google map
    private View.OnClickListener fabClickListener;

    // Users current location
    private Location userLocation;

    /**
     * Create a new instance of this bottom sheet fragment and returns it to the host
     * @return PlaceDetailsBSFragment
     */
    public static PlaceDetailsBSFragment newInstance() {
        PlaceDetailsBSFragment placeDetailsBSFragment = new PlaceDetailsBSFragment();
        return placeDetailsBSFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if(savedInstanceState != null){
            Bundle stateInstance = savedInstanceState.getBundle(TAG);
            setPlaceName(stateInstance.getString(KEY_PLACE_NAME));
            setPlaceID(stateInstance.getString(KEY_PLACE_ID));
            setPhotoReferenceId(stateInstance.getString(KEY_PLACE_PHOTO_REF_ID));
            setPlaceRating(stateInstance.getFloat(KEY_PLACE_RATING));
            setPlaceGeo(stateInstance.getString(KEY_PLACE_GEO));
        }
        // Get hosting hostActivity
        hostActivity = getActivity();
        // Inject this fragment for dependencies
        ((BaseApplication) hostActivity.getApplication()).getDependencyComponents().inject(this);
        // Bind this view with the presenter
        placeDetailsPresenter.bindView(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
       // Specify entry exit animations for the bottom sheet dialog
        dialog.getWindow().setWindowAnimations(R.style.bottomsheet_entry_exit_slide_anim);
        // Set translucent status which otherwise will turn dark
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // Remove window background dim since bottom sheet is transparent in nature
        dialog.getWindow().setDimAmount(0);
        super.setupDialog(dialog, style);
        // Set dialog content view
        contentView = View.inflate(getContext(), R.layout.fragment_place_details_bs, null);
        dialog.setContentView(contentView);
        // Bind the hostActivity for view injection. Unbind this butter knife instance after use.
        viewUnBinder =  ButterKnife.bind(this,contentView);
        // Specify the bottom sheet behavior for the dialog
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        // Set bottom sheet background transparent for aesthetics
        ((View) contentView.getParent()).setBackgroundColor(
                ResourcesCompat.getColor(dialog.getContext().getResources(), android.R.color.transparent, null));
        /**
         * Check if the layout behaviour has that of bottom sheet and configure
         * bottom sheet appearance. Peek height will make the bottom sheet to peek out
         * during entry. Bind listeners to the bottom sheet to listen to the events.
         */
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            bottomSheetBehavior = (BottomSheetBehavior) behavior;
            initializeBottomSheetCallback();
            bottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
            bottomSheetBehavior.setPeekHeight(
                    Utilities.convertDPToPixels(contentView.getContext(),110));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Bundle stateInstance = new Bundle();
        stateInstance.putString(KEY_PLACE_ID,getPlaceID());
        stateInstance.putString(KEY_PLACE_GEO,getPlaceGeo());
        stateInstance.putString(KEY_PLACE_NAME,getPlaceName());
        stateInstance.putString(KEY_PLACE_PHOTO_REF_ID,getPhotoReferenceId());
        stateInstance.putFloat(KEY_PLACE_RATING,getPlaceRating());
        // Save the state
        outState.putBundle(TAG,stateInstance);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BottomSheetListener) {
            bListener = (BottomSheetListener) context;
        }
    }

    @Override
    public void onDetach() {
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
        // Perform clean up so as to prevent memory leaks
        actionClickListener = null;
        fabClickListener = null;
        mBottomSheetBehaviorCallback = null;
        FontManager.clearFontCache();
        hostActivity = null;
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Initialize all ui views by setting data or by binding listeners.
        initializeViews();
        /**
         * Perform API search to fetch more details about the place based on the place_id
         * from Google Places API
         **/
        if(getPlaceID() != null && !getPlaceID().isEmpty())
            placeDetailsPresenter.search(getPlaceID());
    }

    /**
     * Initialize views, poplate views with data, bind listeners etc.
     * Also initialize Font manager for star ratings.
     */
    private void initializeViews() {
        // Click listener for interactive actions.
        if(actionClickListener == null)
            initInteractiveActions();
        // Click listener for Floating action button (FAB) for map routes.
        if(fabClickListener == null)
            initiDirecitonFABListener();
        // Bind the FAB with above initialized click listener
        if(directionFab != null)
            directionFab.setOnClickListener(fabClickListener);
        // Initialize font manager for Font Awesome
        Typeface iconFont = FontManager.getTypeface(FontManager.FONTAWESOME, hostActivity.getApplicationContext());
        // Apply typeface for font awesome to the view
        FontManager.convertToFontAwesome(placeStarRatingTV, iconFont);
        /**
         * Populate basic place information carried forward from previous search hostActivity.
         * Show name of the place. Display available rating
         */
        // Set place name
        if (placeNameTV != null && getPlaceName() != null && !getPlaceName().isEmpty()) {
            placeNameTV.setText(getPlaceName());
        }
        // Populate ratings and star ratings
        try {
            if (placeRatingTV != null && getPlaceRating() >= 0) {
                float ratings = getPlaceRating();
                placeRatingTV.setText(String.format("%.1f", ratings));
                placeStarRatingTV.setText(FontManager.createVisualRatings(ratings));
            }
        }catch (Exception e){
            placeRatingTV.setText(String.format("%d", 0));
            placeStarRatingTV.setText(FontManager.createVisualRatings(0));

        }// Implicit locale can at time result in exception
        // Display place image from Google place APIs.
        if(placeDetailsImageIV != null && getPhotoReferenceId()!= null && !getPhotoReferenceId().isEmpty()){
            Picasso.with(placeDetailsImageIV.getContext())
                    .load(Utilities.makePhotoUrl(getPhotoReferenceId()))
                    .error(R.drawable.drawable_image_not_available)
                    .into(placeDetailsImageIV);
        }else{

        }
    }

    /**
     * Initialize click listener for FAB used for invoking direction using google maps
     */
    private void initiDirecitonFABListener(){
        fabClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPlaceGeo() != null && !getPlaceGeo().isEmpty()) {
                    Uri geoIntent = Uri.parse("https://maps.google.com/maps?f=d&hl=en&daddr="
                            + getPlaceGeo());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoIntent);
                    if (mapIntent.resolveActivity(hostActivity.getPackageManager()) != null) {
                        startActivity(mapIntent);
                    } else {
                        Toast.makeText(hostActivity.getApplicationContext()
                                , R.string.error_msg_map_not_supported
                                , Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(hostActivity.getApplicationContext()
                            , getString(R.string.direction_opr_nt_sptd)
                            , Toast.LENGTH_SHORT).show();
            }
        };
    }

    /**
     * Initialize click listeners for interactive actions such as dial phone numbers,
     * copy address etc.
     */
    private void initInteractiveActions(){
        actionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                /**
                 * ACTION_DIAL : To route the intent to default dialer application
                 * ResolveIntent - is used to identify if the device has necessary application to handle
                 * this intent.
                 */
                if (id == R.id.place_phone_primary_tv) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + v.getTag()));
                    if (resolveIntent(getActivity().getApplicationContext(), intent)) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.error_msg_call_not_supported, Toast.LENGTH_SHORT).show();
                    }
                }


                /**
                 * When address linear layout is clicked, copy the post address + postbox no to clipboard
                 */
                if (id == R.id.place_address_tv) {
                    ClipboardManager cbManger = (ClipboardManager) getActivity()
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Address ", ((TextView) v).getText().toString());
                    cbManger.setPrimaryClip(clipData);
                    Toast.makeText(getActivity(), getActivity().getResources()
                            .getString(R.string.copy_to_clip_board), Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    /*
    * ResolveIntent - is used to identify if the device has necessary application to handle
    * this intent. QueryIntentActivities - returns all activities that can handle a particular intent
    * based on its attributes.
    */
    public static boolean resolveIntent(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * Initialize bottom sheet behavior callback. This callback handled events associated
     * with bottom sheet
     */
    private void initializeBottomSheetCallback(){
        final float newMax = Utilities.convertDPToPixels(hostActivity.getApplication(),210);
        mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    PlaceDetailsBSFragment.this.dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float y = convertRange(slideOffset, newMax);
                if(slideOffset >= 0) {
                    placeDetailsImageIV.setAlpha(slideOffset);
                    placeDetailsContainerLL.setTranslationY(y);
                }
            }
        };
    }

    /**
     * Converting range from one form to another
     * (((OldValue - OldMin) * (NewMax - NewMin)) / (OldMax - OldMin)) + NewMin
     * @param oldValue
     * @return
     */
    public float convertRange(float oldValue, final float newMax){
        float oldMin = 0; // As slideOffset has minimum value of 0 when collapsed
        float newMin = 0; // As view has its normal Y value as 0
        float oldMax = 1; // slideOffset has maximum value of 1 when expanded
        return (((oldValue - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin;
    }

    /**
     * This method is accessed by the associated presenter that will update the views
     * when place details are available
     * @param placeResult
     */
    public void updateData(PlaceResult placeResult){
        if(placeResult != null){
            if(placeAddressTV != null && placeResult.getFormattedAddress() != null) {
                placeAddressTV.setText(placeResult.getFormattedAddress());
                placeAddressTV.setOnClickListener(actionClickListener);
            }

            if(placePhoneTV != null && placeResult.getPhoneNumber() != null) {
                placePhoneTV.setText(placeResult.getPhoneNumber());
                placePhoneTV.setTag(placeResult.getPhoneNumberInternational() != null ?
                        placeResult.getPhoneNumberInternational() : placeResult.getPhoneNumber());
                placePhoneTV.setOnClickListener(actionClickListener);
            }

            if(placeVicinityTV != null && placeResult.getVicinity() != null)
                placeVicinityTV.setText(placeResult.getVicinity());
        }
    }
    public interface BottomSheetListener{
        void toggleAppBar(boolean status);
    }

    /**
     * Set users current location from the hosting view
     */
    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

}
