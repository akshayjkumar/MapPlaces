package com.ajdev.aroundme.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import com.ajdev.aroundme.R;

public abstract class RuntimePermissionsActivity extends AppCompatActivity {

    /**
     * Request codes for location settings and
     * fine location permission
     */
    public static final int REQUEST_CHECK_SETTINGS = 100;
    public static final int REQUEST_FINE_LOCATION = 101;
    /**
     * Status codes for given permissions.
     */
    public static final int PERMISSION_GRANTED = 0;
    public static final int PERMISSION_DENIED = 1;
    public static final int PERMISSION_DENIED_FOREVER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onPermissionGranted(requestCode, permissions, grantResults);
    }

    /**
     * Requests a single permission. This method should be called when requiesting for permissions
     * from inside a fragment.
     * @param permissionRequests
     * @param requestCodes
     * @param messages
     * @return Status code
     */
    public int requestPermissionFromActivity(final String[] permissionRequests, final int[] requestCodes, final String[] messages){
        for(int i=0; i< permissionRequests.length; i++){
            final String permission = permissionRequests[i];

            if(ContextCompat.checkSelfPermission(getApplicationContext(),permission) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(RuntimePermissionsActivity.this,permission)){
                    displayRationale(RuntimePermissionsActivity.this,permissionRequests,requestCodes,messages);
                }else{
                    // This section is reached if user has previously denied the
                    // permission checking "Never ask again"
                    return PERMISSION_DENIED_FOREVER;
                }
            }else
                return PERMISSION_GRANTED;
        }
        return PERMISSION_DENIED;
    }

    public abstract void onPermissionGranted(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    public void displayRationale(final Activity activity, final String[] permissionRequests,
                                 final int[] requestCodes, final String[] messages) {
        try {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme_AlertDialogCustom));
            View customView = getLayoutInflater().inflate(R.layout.dialog_custom_layout, null);
            TextView txtTitle = (TextView) customView.findViewById(R.id.txtTitle);
            txtTitle.setText(R.string.permission_request_dialog_title);
            TextView txtMessage = (TextView) customView.findViewById(R.id.txtMessage);
            txtMessage.setText(messages[0]);
            mBuilder.setView(customView);
            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton(R.string.permission_action_enable, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(activity,permissionRequests,requestCodes[0]);
                    dialogInterface.dismiss();
                }
            });
            mBuilder.setNegativeButton(R.string.permission_action_dismiss, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            mBuilder.show();
        } catch (Exception ex) {
            ex.getLocalizedMessage();
        }
    }

    public int getPermissionStatus(final Activity activity, final String[] permissionRequests){
        for(int i=0; i< permissionRequests.length; i++){
            final String permission = permissionRequests[i];
            if(ContextCompat.checkSelfPermission(getApplicationContext(),permission) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(activity,permission)){
                    return PERMISSION_DENIED;
                }else{
                    // This section is reached if user has previously denied the permission checking "Never ask again"
                    return PERMISSION_DENIED_FOREVER;
                }
            }else {
                return PERMISSION_GRANTED;
            }
        }
        return PERMISSION_DENIED;
    }


    public void requestSinglePermission(Activity activity, String[] permissions, int requestCodes){
        ActivityCompat.requestPermissions(activity,permissions,requestCodes);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }
}
