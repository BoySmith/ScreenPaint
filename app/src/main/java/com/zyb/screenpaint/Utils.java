package com.zyb.screenpaint;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class Utils {
    public static String getSystemProperty(String propName) {
        IOException ex;
        Throwable th;
        BufferedReader br = null;
        try {
            BufferedReader br2 = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("getprop " + propName).getInputStream()), 1024);
            try {
                String line = br2.readLine();
                if (br2 != null) {
                    try {
                        br2.close();
                    } catch (IOException e) {
                        Log.e("getprop", "Exception while closing InputStream", e);
                    }
                }
                br = br2;
                return line;
            } catch (IOException e2) {
                ex = e2;
                br = br2;
                try {
                    Log.d("getprop", "Unable to read sysprop " + propName, ex);
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e3) {
                            Log.e("getprop", "Exception while closing InputStream", e3);
                        }
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e32) {
                            Log.e("getprop", "Exception while closing InputStream", e32);
                        }
                    }
                }
            }
        } catch (IOException e4) {
            ex = e4;
            Log.d("getprop", "Unable to read sysprop " + propName, ex);
            if (br != null) {
//                br.close();
            }
            return null;
        }
        return null;
    }

    public static boolean isMiuiInstalled(Context paramContext) {
        PackageManager localPackageManager = paramContext.getPackageManager();
        Intent localIntent = new Intent("android.intent.action.MAIN");
        localIntent.addCategory("android.intent.category.HOME");
        for (ResolveInfo resolveInfo : localPackageManager.queryIntentActivities(localIntent, PackageManager.MATCH_DEFAULT_ONLY)) {
            if (resolveInfo.activityInfo.packageName.equalsIgnoreCase("com.miui.home")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNewMz() {
        try {
            String flymeOs = getSystemProperty("ro.build.display.id").replace("Flyme OS ", "");
            flymeOs = flymeOs.substring(0, flymeOs.length() - 1);
            if (flymeOs == null) {
                return false;
            }
            String[] flymeOsNumStr = flymeOs.split("\\.");
            if (flymeOsNumStr == null || flymeOsNumStr.length < 3) {
                return false;
            }
            int num1 = Integer.parseInt(flymeOsNumStr[0]);
            if (num1 < 4) {
                return false;
            }
            if (num1 > 4) {
                return true;
            }
            int num2 = Integer.parseInt(flymeOsNumStr[1]);
            if (num2 < 5) {
                return false;
            }
            if (num2 > 5) {
                return true;
            }
            if (Integer.parseInt(flymeOsNumStr[2]) >= 7) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isEmui() {
        String emui = getSystemProperty("ro.build.version.emui");
        if (!TextUtils.isEmpty(emui)) {
            return emui.toLowerCase(Locale.getDefault()).contains("emotionui");
        }
        if (Build.MANUFACTURER.toLowerCase(Locale.getDefault()).contains("huawei")) {
            return true;
        }
        return false;
    }

    public static float getEmuiVersion() {
        float f = 0;
        if (isEmui()) {
            String emui = getSystemProperty("ro.build.version.emui");
            try {
                f = Float.parseFloat(emui.substring(emui.indexOf("_") + 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return f;
    }
}
