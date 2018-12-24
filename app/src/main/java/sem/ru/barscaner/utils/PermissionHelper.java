package sem.ru.barscaner.utils;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper {

    private static final String TAG = "PermissionHelper";
    private String snackMessage;
    private boolean shouldShowRationale;
    private View view;
    private Activity activity;
    private OnPermissionResultImpl onPermissionResult;
    private OnShowRationaleAlert onShowRationaleAlert;
    private Fragment fragmentSupport;

    public void setOnShowRationaleAlert(OnShowRationaleAlert onShowRationaleAlert) {
        this.onShowRationaleAlert = onShowRationaleAlert;
    }

    public interface OnPermissionResultImpl{
        void onPermissionsResult(int requestCode, boolean isGranted);
    }

    public interface OnShowRationaleAlert{
        void onShowRationalePermissionAlert(String message);
    }


    public PermissionHelper(Activity activity, OnShowRationaleAlert onShowRationaleAlert,
                            OnPermissionResultImpl onPermissionResult) {
        this.activity = activity;
        this.onShowRationaleAlert = onShowRationaleAlert;
        this.onPermissionResult = onPermissionResult;
    }

    public PermissionHelper(Activity activity, View view,
                            OnPermissionResultImpl onPermissionResult) {
        this.activity = activity;
        this.view = view;
        this.onPermissionResult = onPermissionResult;
    }

    public PermissionHelper(Fragment fragment, OnShowRationaleAlert onShowRationaleAlert,
                            OnPermissionResultImpl onPermissionResult) {
        this.onPermissionResult = onPermissionResult;
        this.onShowRationaleAlert = onShowRationaleAlert;
        this.fragmentSupport = fragment;
    }

    public PermissionHelper(Fragment fragment, View view,
                            OnPermissionResultImpl onPermissionResult) {
        this.fragmentSupport = fragment;
        this.view = view;
        this.onPermissionResult = onPermissionResult;
    }

    private String[] getNeededPermissions(String[] permissions){
        Context context = activity!=null ? activity : fragmentSupport.getActivity();
        List<String> permissionsNeeded = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            final String perm = permissions[i];
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                if(activity!=null) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)) {
                        this.shouldShowRationale = true;
                        permissionsNeeded.add(perm);
                    } else permissionsNeeded.add(perm);
                }else{
                    if (fragmentSupport.shouldShowRequestPermissionRationale(perm)) {
                        this.shouldShowRationale = true;
                        permissionsNeeded.add(perm);
                    } else permissionsNeeded.add(perm);
                }
            }
        }
        Log.d(TAG, "needed="+permissionsNeeded.toString());
        return permissionsNeeded.toArray(new String[permissionsNeeded.size()]);
    }

    public void againRequestPermission(@NonNull String[] permissions, final int requestCode){
        final String[] permissionsNeeded = getNeededPermissions(permissions);
        if (fragmentSupport == null) {
            requestFromActivity(activity, permissionsNeeded, requestCode);
        } else {
            requestFromFragment(fragmentSupport, permissionsNeeded, requestCode);
        }
    }

    private void requestFromActivity(Activity activity, String[] permissions,
                                     int requestCode){
        ActivityCompat.requestPermissions(activity,
                permissions, requestCode);
    }

    private void requestFromFragment(Fragment fragment, String[] permissions,
                                     int requestCode){
        fragment.requestPermissions(permissions, requestCode);
    }

    public void requestPermissions(@NonNull String[] permissions, final int requestCode,
                                   String snackMessage){
        shouldShowRationale=false;
        this.snackMessage = snackMessage;
        final String[] permissionsNeeded = getNeededPermissions(permissions);
        if(permissionsNeeded.length>0) {
            if (shouldShowRationale) {
                if(onShowRationaleAlert==null) {
                    /*
                    Если не определен интерфейс покавза уведомления о запросе
                    разрешений, то опказываем SnaсkBar
                     */
                    Snackbar snack = Snackbar.make(view, snackMessage, Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.YELLOW)
                            .setAction("OK", view1 -> {
                                if (fragmentSupport == null) {
                                    requestFromActivity(activity, permissionsNeeded, requestCode);
                                } else {
                                    requestFromFragment(fragmentSupport, permissionsNeeded, requestCode);
                                }
                            });
                    View view = snack.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snack.show();
                }else{
                    onShowRationaleAlert.onShowRationalePermissionAlert(snackMessage);
                }
            } else {
                if (fragmentSupport == null) {
                    requestFromActivity(activity, permissionsNeeded, requestCode);
                } else {
                    requestFromFragment(fragmentSupport, permissionsNeeded, requestCode);
                }
            }
        }else{//все права есть
            onPermissionResult.onPermissionsResult(requestCode, true);
        }
    }

    public void onPermissionsResult(int requestCode, @NonNull String[] permissions,
                                      @NonNull int[] grantResults){
        boolean isGranted=true;
        for(int grantR: grantResults){
            if(grantR!=PackageManager.PERMISSION_GRANTED){
                isGranted=false;
                break;
            }
        }
        onPermissionResult.onPermissionsResult(requestCode, isGranted);
    }

}
