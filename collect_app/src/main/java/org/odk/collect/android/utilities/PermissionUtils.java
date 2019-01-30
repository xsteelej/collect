package org.odk.collect.android.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.odk.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.activities.FormChooserList;
import org.odk.collect.android.activities.FormDownloadList;
import org.odk.collect.android.activities.FormEntryActivity;
import org.odk.collect.android.activities.InstanceChooserList;
import org.odk.collect.android.activities.InstanceUploaderActivity;
import org.odk.collect.android.activities.InstanceUploaderList;
import org.odk.collect.android.activities.SplashScreenActivity;
import org.odk.collect.android.listeners.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * PermissionUtils allows all permission related messages and checks to be encapsulated in one
 * area so that classes don't have to deal with this responsibility; they just receive a callback
 * that tells them if they have been granted the permission they requested.
 */

public class PermissionUtils {

    /**
     * Required for context and spawning of Dexter's activity that handles
     * permission checking.
     */
    private final Activity activity;

    public PermissionUtils(@NonNull Activity activity) {
        this.activity = activity;
    }

    public static boolean areStoragePermissionsGranted(Context context) {
        return isPermissionGranted(context,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static boolean isCameraPermissionGranted(Context context) {
        return isPermissionGranted(context, Manifest.permission.CAMERA);
    }

    public static boolean areLocationPermissionsGranted(Context context) {
        return isPermissionGranted(context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public static boolean areCameraAndRecordAudioPermissionsGranted(Context context) {
        return isPermissionGranted(context,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO);
    }

    public static boolean isGetAccountsPermissionGranted(Context context) {
        return isPermissionGranted(context, Manifest.permission.GET_ACCOUNTS);
    }

    public static boolean isReadPhoneStatePermissionGranted(Context context) {
        return isPermissionGranted(context, Manifest.permission.READ_PHONE_STATE);
    }

    /**
     * Returns true only if all of the requested permissions are granted to Collect, otherwise false
     */
    private static boolean isPermissionGranted(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks to see if an activity is one of the entry points to the app i.e
     * an activity that has a view action that can launch the app.
     *
     * @param activity that has permission requesting code.
     * @return true if the activity is an entry point to the app.
     */
    public static boolean isEntryPointActivity(CollectAbstractActivity activity) {

        List<Class<?>> activities = new ArrayList<>();
        activities.add(FormEntryActivity.class);
        activities.add(InstanceChooserList.class);
        activities.add(FormChooserList.class);
        activities.add(InstanceUploaderList.class);
        activities.add(SplashScreenActivity.class);
        activities.add(FormDownloadList.class);
        activities.add(InstanceUploaderActivity.class);

        for (Class<?> act : activities) {
            if (activity.getClass().equals(act)) {
                return true;
            }
        }

        return false;
    }

    public static void finishAllActivities(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.finishAndRemoveTask();
        } else {
            activity.finishAffinity();
        }
    }

    /**
     * Checks to see if the user granted Collect the permissions necessary for reading
     * and writing to storage and if not utilizes the permissions API to request them.
     *
     * @param action is a listener that provides the calling component with the permission result.
     */
    public void requestStoragePermissions(@NonNull PermissionListener action) {
        requestPermissions(new PermissionListener() {
            @Override
            public void granted() {
                action.granted();
            }

            @Override
            public void denied() {
                showAdditionalExplanation(R.string.storage_runtime_permission_denied_title,
                        R.string.storage_runtime_permission_denied_desc, R.drawable.sd, action);
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void requestCameraPermission(@NonNull PermissionListener action) {
        requestPermissions(new PermissionListener() {
            @Override
            public void granted() {
                action.granted();
            }

            @Override
            public void denied() {
                showAdditionalExplanation(R.string.camera_runtime_permission_denied_title,
                        R.string.camera_runtime_permission_denied_desc, R.drawable.ic_photo_camera, action);
            }
        }, Manifest.permission.CAMERA);
    }

    public void requestLocationPermissions(@NonNull PermissionListener action) {
        requestPermissions(new PermissionListener() {
            @Override
            public void granted() {
                action.granted();
            }

            @Override
            public void denied() {
                showAdditionalExplanation(R.string.location_runtime_permissions_denied_title,
                        R.string.location_runtime_permissions_denied_desc, R.drawable.ic_place_black, action);
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public void requestRecordAudioPermission(@NonNull PermissionListener action) {
        requestPermissions(new PermissionListener() {
            @Override
            public void granted() {
                action.granted();
            }

            @Override
            public void denied() {
                showAdditionalExplanation(R.string.record_audio_runtime_permission_denied_title,
                        R.string.record_audio_runtime_permission_denied_desc, R.drawable.ic_mic, action);
            }
        }, Manifest.permission.RECORD_AUDIO);
    }

    public void requestCameraAndRecordAudioPermissions(@NonNull PermissionListener action) {
        requestPermissions(new PermissionListener() {
            @Override
            public void granted() {
                action.granted();
            }

            @Override
            public void denied() {
                showAdditionalExplanation(R.string.camera_runtime_permission_denied_title,
                        R.string.camera_runtime_permission_denied_desc, R.drawable.ic_photo_camera, action);
            }
        }, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
    }

    public void requestGetAccountsPermission(@NonNull PermissionListener action) {
        requestPermissions(new PermissionListener() {
            @Override
            public void granted() {
                action.granted();
            }

            @Override
            public void denied() {
                showAdditionalExplanation(R.string.get_accounts_runtime_permission_denied_title,
                        R.string.get_accounts_runtime_permission_denied_desc, R.drawable.ic_get_accounts, action);
            }
        }, Manifest.permission.GET_ACCOUNTS);
    }

    public void requestSendSMSPermission(@NonNull PermissionListener action) {
        requestPermissions(new PermissionListener() {
            @Override
            public void granted() {
                action.granted();
            }

            @Override
            public void denied() {
                showAdditionalExplanation(R.string.send_sms_runtime_permission_denied_title,
                        R.string.send_sms_runtime_permission_denied_desc, R.drawable.ic_sms, action);
            }
        }, Manifest.permission.SEND_SMS);
    }

    public void requestReadPhoneStatePermission(@NonNull PermissionListener action, boolean displayPermissionDeniedDialog) {
        requestPermissions(new PermissionListener() {
            @Override
            public void granted() {
                action.granted();
            }

            @Override
            public void denied() {
                if (displayPermissionDeniedDialog) {
                    showAdditionalExplanation(R.string.read_phone_state_runtime_permission_denied_title,
                            R.string.read_phone_state_runtime_permission_denied_desc, R.drawable.ic_phone, action);
                } else {
                    action.denied();
                }
            }
        }, Manifest.permission.READ_PHONE_STATE);
    }

    public void requestSendSMSAndReadPhoneStatePermissions(@NonNull PermissionListener action) {
        requestPermissions(new PermissionListener() {
            @Override
            public void granted() {
                action.granted();
            }

            @Override
            public void denied() {
                showAdditionalExplanation(R.string.send_sms_runtime_permission_denied_title,
                        R.string.send_sms_runtime_permission_denied_desc, R.drawable.ic_sms, action);
            }
        }, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE);
    }

    private void requestPermissions(@NonNull PermissionListener listener, String... permissions) {
        DexterBuilder builder = null;

        if (permissions.length == 1) {
            builder = createSinglePermissionRequest(listener, permissions[0]);
        } else if (permissions.length > 1) {
            builder = createMultiplePermissionsRequest(listener, permissions);
        }

        if (builder != null) {
            builder.withErrorListener(error -> Timber.i(error.name())).check();
        }
    }

    private DexterBuilder createSinglePermissionRequest(PermissionListener listener, String permission) {
        return Dexter.withActivity(activity)
                .withPermission(permission)
                .withListener(new com.karumi.dexter.listener.single.PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        listener.granted();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        listener.denied();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                });
    }

    private DexterBuilder createMultiplePermissionsRequest(PermissionListener listener, String[] permissions) {
        return Dexter.withActivity(activity)
                .withPermissions(permissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            listener.granted();
                        } else {
                            listener.denied();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                });
    }

    private void showAdditionalExplanation(int title, int message, int drawable, @NonNull PermissionListener action) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.Theme_AppCompat_Light_Dialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> action.denied())
                .setCancelable(false)
                .setIcon(drawable)
                .create();

        DialogUtils.showDialog(alertDialog, activity);
    }
}