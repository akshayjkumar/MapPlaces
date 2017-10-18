package com.ajdev.aroundme.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.ajdev.aroundme.BuildConfig;
import com.ajdev.aroundme.R;
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

    public static boolean isConnected(Activity activity){
        boolean isConnected = false;
        ConnectivityManager cm =
                (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        Log.e("~~~~~~~~~~~~~~~~~","~~~~~~~~~~~~~~~~~~~~~~~ isConnected " + isConnected);
        activity = null;
        return isConnected;
    }

    public static AlertDialog showNetworkErrorDialog(Activity activity){{
            try {
                AlertDialog alertDialog;
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                        new ContextThemeWrapper(activity, R.style.AppTheme_AlertDialogCustom));
                LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.dialog_custom_layout, null);
                layoutInflater = null;
                TextView txtTitle = (TextView) customView.findViewById(R.id.txtTitle);
                txtTitle.setText(R.string.network_dialog_title_error);
                TextView txtMessage = (TextView) customView.findViewById(R.id.txtMessage);
                txtMessage.setText(R.string.network_dialog_error_msg);
                mBuilder.setView(customView);
                mBuilder.setCancelable(true);
                DialogInterface.OnClickListener actionDismiss = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                };
                mBuilder.setPositiveButton(R.string.network_dialog_action_dismiss_text, actionDismiss);
                alertDialog = mBuilder.show();
                activity = null;
                return alertDialog;
            } catch (Exception ex) {
                ex.getLocalizedMessage();
                return null;
            }
        }
    }
}
