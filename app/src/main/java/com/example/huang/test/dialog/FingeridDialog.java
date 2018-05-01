package com.example.huang.test.dialog;

import android.Manifest;
import android.app.DialogFragment;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.huang.test.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by huang on 2018/4/22.
 */

public class FingeridDialog extends DialogFragment {
    Context context;
    FingerprintManager fingerprintManager;
    KeyguardManager keyguardManager;
    public FingeridDialog(Context context) {
        this.context = context;
    }
    private isTrue isTrue;
    ImageView finger;
    Button button;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fingeriddialog,container,false);
        finger=view.findViewById(R.id.finger);
        button=view.findViewById(R.id.cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if(isFinger()) {
              startListening(null);
                }
        return view;
    }
        public boolean isFinger() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return false;
        }
        if (!fingerprintManager.isHardwareDetected()) {
            Toast.makeText(context, "没有指纹识别模块", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!keyguardManager.isKeyguardSecure()) {
            Toast.makeText(context, "没有开启锁屏密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            Toast.makeText(context, "没有录入指纹", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    CancellationSignal mCancellationSignal = new CancellationSignal();
    //回调方法
    FingerprintManager.AuthenticationCallback mSelfCancelled = new FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            //但多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
            Toast.makeText(context, errString, Toast.LENGTH_SHORT).show();
            dismiss();
            //  showAuthenticationScreen();
        }
        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

            Toast.makeText(context, helpString, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
         //   textView.setText("指纹正确");
           // Toast.makeText(context, "指纹识别成功", Toast.LENGTH_SHORT).show();
            //dismiss();
            finger.setImageResource(R.drawable.success_finger);
            if(isTrue!=null){
                isTrue.istrue(true);
            }

        }
        @Override
        public void onAuthenticationFailed() {
          //  textView.setText("指纹错误");
            isTrue.istrue(false);
            finger.setImageResource(R.drawable.fail_finger);
           // Toast.makeText(context, "指纹识别失败", Toast.LENGTH_SHORT).show();
        }
    };
    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
        //android studio 上，没有这个会报错
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "没有指纹识别权限", Toast.LENGTH_SHORT).show();
            return;
        }
        fingerprintManager.authenticate(cryptoObject, mCancellationSignal, 0, mSelfCancelled, null);

    }

    private void showAuthenticationScreen() {
        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent("finger", "测试指纹识别");
        if (intent != null) {
            startActivityForResult(intent, 0);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            // Challenge completed, proceed with using cipher
            if (resultCode == RESULT_OK) {
                Toast.makeText(context, "识别成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "识别失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void Log(String tag, String msg) {
        Log.d(tag, msg);
    }
    public interface isTrue{
        void istrue(boolean flag);
    }
    public void istrue(isTrue isTrue){
        this.isTrue=isTrue;
    }

}
