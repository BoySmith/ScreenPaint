package com.zyb.screenpaint;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

public class PaintFloatWinService extends Service {

    private PenFloatViewManager penFloatViewManager;
    private MyBinder binder = new MyBinder();

    private BroadcastReceiver receiver;

    private class MyBinder extends Binder {
        PaintFloatWinService getService() {
            return PaintFloatWinService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        penFloatViewManager = new PenFloatViewManager(getApplicationContext());
        penFloatViewManager.createView();

        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (penFloatViewManager != null) {
                    penFloatViewManager.addPenFloatView();
                }
            }
        };
        //监听广播：当正在画图时，但EditPaintActivity onPause 时，要显示悬浮窗
        registerReceiver(receiver, new IntentFilter("super_painter_exit_edit"));
        //监听广播：当正在画图时，点击 x 按钮 ，要显示悬浮窗
        registerReceiver(receiver, new IntentFilter("super_finishPaintEditActivity"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
