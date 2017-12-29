package com.zyb.screenpaint;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int REQUEST_OVERLAY_PERMISSION = 1;
    private static final int REQUEST_ALERT_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("zyb", "MainActivity onCreate");
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(this)) {
                if (hasAlertPermission()) {
                    startFloatService();
                } else {
                    requestAlertPermission();
                }

            } else {
                //若没有权限，提示获取.
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                Toast.makeText(this, "需要取得权限以使用悬浮窗", Toast.LENGTH_SHORT).show();
                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
            }
        } else {
            startFloatService();
        }
    }

    private boolean hasAlertPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAlertPermission() {
        Log.i("zyb", "requestAlertPermission");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                REQUEST_ALERT_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OVERLAY_PERMISSION && Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "权限授予成功！", Toast.LENGTH_SHORT).show();
                if (hasAlertPermission()) {
                    startFloatService();
                } else {
                    requestAlertPermission();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (requestCode == REQUEST_ALERT_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startFloatService();
            } else {
                //目前在6.0以上，手动申请权限会返回失败，在这里先不管，仍然开启服务。
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                startFloatService();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startFloatService() {
        Intent intent = new Intent(this, PaintFloatWinService.class);
        startService(intent);
        finish();
    }
}
