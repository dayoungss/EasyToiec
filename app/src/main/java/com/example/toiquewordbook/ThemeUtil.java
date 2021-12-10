package com.example.toiquewordbook;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeUtil {
    public static final String LIGHT_MODE = "light";
    public static final String DARK_MODE = "dark";
    public static final String DEFAULT_MODE = "default";

    private static final String TAG = "ThemeUtil";

    public static void applyTheme(String themeColor) {
        switch (themeColor) {
            case LIGHT_MODE :
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Log.d(TAG,"라이트 모드 적용되어 있음");
                break;
            case DARK_MODE :
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Log.d(TAG,"다크 모드 적용되어 있음");
                break;
            default:
                // 안드로이드 10 이상
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
                // 안드로이드 10 미만
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                }
                break;
        }
    }

    public static void modSave (Context context, String select_mod){
        SharedPreferences sp;
        sp = context.getSharedPreferences("mod",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("mod",select_mod);
        editor.commit();
    }

    public static String modLoad (Context context) {
        SharedPreferences sp;
        sp = context.getSharedPreferences("mod",context.MODE_PRIVATE);
        String load_mod = sp.getString("mod","light");
        return load_mod;
    }
}
