package com.wellsun.pushdoor.util;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wellsun.pushdoor.R;
import com.wellsun.pushdoor.base.App;

/**
 * date     : 2023-03-29
 * author   : ZhaoZheng
 * describe :
 */
public class ToastPrint {

    private static Toast toastText;
    private static Toast toastView;

    public static void showText(String mgs) {
        if (toastText == null) {
            toastText = Toast.makeText(App.mApp, mgs, Toast.LENGTH_LONG);
        } else {
            toastText.cancel();
            toastText = Toast.makeText(App.mApp, mgs, Toast.LENGTH_LONG);
        }
        toastText.setText(mgs);
        toastText.setGravity(Gravity.CENTER, 0, 0);
        toastText.show();
    }

    public static void showView(String mgs) {
        if (toastView == null) {
            toastView = Toast.makeText(App.mApp, "", Toast.LENGTH_LONG);
        } else {
            toastView.cancel();
            toastView = Toast.makeText(App.mApp, "", Toast.LENGTH_LONG);
        }
        View view = LayoutInflater.from(App.mApp).inflate(R.layout.toast_view, null);
        if (mgs != null) {
            ((TextView) view.findViewById(R.id.tv_toast)).setText(mgs);
        }
        toastView.setView(view);
        toastView.setGravity(Gravity.CENTER, 0, 0);
        toastView.show();
    }
}
