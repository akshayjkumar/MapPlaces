package com.ajdev.aroundme.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ajdev.aroundme.BuildConfig;
import com.ajdev.aroundme.model.Location;

/**
 * Created by Akshay.Jayakumar on 10/14/2017.
 */

public class Utilities {

    public static void hideKeyboard(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = context.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(context.getApplicationContext());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        context = null;
    }

    public static String makePhotoUrl(String photoReference){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("place")
                .appendPath("photo")
                .appendQueryParameter("photoreference", photoReference)
                .appendQueryParameter("maxwidth", "400")
                .appendQueryParameter("key", BuildConfig.MAPS_API_KEY);
        return builder.build().toString();
    }

    // Method for converting DP/DIP value to pixels
    public static int convertDPToPixels(Context context, int dps){
       Resources resources = context.getResources();
        int  px = (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps, resources.getDisplayMetrics()));
        return px;
    }
}
