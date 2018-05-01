package com.example.huang.test.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huang.test.R;
import com.example.huang.test.utils.PermissionUtils;


/**
 * Created by huang on 2018/3/25.
 */

public class Frag2 extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Context context;
    Button button;
    TextView textView;

    public Frag2(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.second, container, false);
        button = view.findViewById(R.id.drive);
        textView = view.findViewById(R.id.drive_num);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requeststate();
            }
        });

        return view;
    }

//    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionGrant() {
//        @Override
//        public void onPermissionGranted(int requestCode) {
//            switch (requestCode) {
//                case PermissionUtils.CODE_READ_PHONE_STATE:
//                    Toast.makeText(context, "Result Permission Grant CODE_READ_PHONE_STATE", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    };

    void getSystem() {
        String brand = Build.BRAND;
        String model = Build.MODEL;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        String sim = tm.getSimSerialNumber();
        textView.setText("品牌：" + brand + "\n" + "型号：" + model + "\n" + "imei:" + deviceId + "\n" + "sim:" + sim);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //   PermissionUtils.requestPermissionsResult(scanForActivity(context), requestCode, permissions, grantResults, mPermissionGrant);
        switch (requestCode) {
            case PermissionUtils.CODE_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSystem();
                } else {
                    Toast.makeText(context, "权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    void requeststate() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                showMessageOKCancel("You need to allow access to Contacts",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //  requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},PermissionUtils.CODE_READ_PHONE_STATE);
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    Intent intent = new Intent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                                    context.startActivity(intent);
                                }
                            }
                        });
                return;
            }
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PermissionUtils.CODE_READ_PHONE_STATE);
        } else {
            getSystem();
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void insertDummyContactWrapper() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                showMessageOKCancel("You need to allow access to Contacts",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        getSystem();
    }
}