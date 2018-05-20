package com.game.Utils;

import android.content.Context;
import android.widget.Toast;

public enum ToastUtil {
    ;
    private static Toast toast = null;

    public static void makeText(Context context, CharSequence text, int duration) {
        if (toast != null) {
            toast.setText(text);
        } else {
            toast = Toast.makeText(context, text, duration);
        }
        toast.show();
    }
}