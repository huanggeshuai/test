package com.example.huang.test.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.huang.test.R;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class AccountCardActivity extends AppCompatActivity {
    Button button;
    TextView info;
  Integer MY_SCAN_REQUEST_CODE=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_card);
        button=(Button) findViewById(R.id.button);
        info=(TextView) findViewById(R.id.info);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onScanPress();
            }
        });

    }

    public void onScanPress() {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false); // default: false不需要     是否需要失效日期等信息
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, false); // default: false               是否隐藏LOGO标记
        scanIntent.putExtra(CardIOActivity.EXTRA_USE_CARDIO_LOGO, true); // default: false               是否使用card.io LOGO
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false不需要     邮政编码
        scanIntent.putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, Color.GREEN); // default: Color.GREEN      扫描线的颜色
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false不需要              CVV信息

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.cardNumber + "\n";
                Log.i("aaa", "银行卡号:" + resultDisplayStr);
                info.setText(resultDisplayStr);
             //   EventBus.getDefault().post(new UpdateCardNumberEvent(resultDisplayStr));
                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );
                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                    Log.i("aaa", "银行卡号有效期:" + resultDisplayStr);

                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            } else {
                resultDisplayStr = "Scan was canceled.";
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        }
        // else handle other activity results
    }
}
