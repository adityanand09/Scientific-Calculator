package com.busi.adi.calc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CalcReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SECRET_CODE")){
            Intent new_intent = new Intent("android.intent.action.MAIN");
            if(intent.getData().getHost().equals("0153")){
                new_intent.setClass(context, Calculator.class);
                context.startActivity(new_intent);
            }
        }
    }
}
