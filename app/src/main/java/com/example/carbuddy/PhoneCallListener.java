package com.example.carbuddy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneCallListener extends PhoneStateListener {
    private boolean isPhoneCalling = false;
    private Context context;


    PhoneCallListener(Context context){
        this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
            isPhoneCalling = true;
        }

        if (TelephonyManager.CALL_STATE_IDLE == state) {
            // run when class initial and phone call ended, need detect flag
            if (isPhoneCalling) {
                // restart app
                Intent i = context.getPackageManager()
                        .getLaunchIntentForPackage(
                                context.getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
                isPhoneCalling = false;
            }
        }
    }
}
