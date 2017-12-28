package com.zyb.screenpaint;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

public class PaintFloatWinService extends Service {
    private PaintTools paintTools;
    private MyBinder binder = new MyBinder();

    private BroadcastReceiver receiver;

    public class MyBinder extends Binder {
        PaintFloatWinService getService() {
            return PaintFloatWinService.this;
        }
    }

    public void onCreate() {
        super.onCreate();
        if (paintTools == null) {
            paintTools = new PaintTools(getApplicationContext(), getResources());
            paintTools.createView();
        }

        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("super_painter_exit_edit")) {
                    if (paintTools != null) {
                        paintTools.exitEditState();
                    }
                }
            }
        };
        //监听广播：当正在画图时，但此时在其他应用中 这时按 home 键，或有其他窗口弹出时，要退出画图编辑
        registerReceiver(receiver, new IntentFilter("super_painter_exit_edit"));
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public IBinder onBind(Intent arg0) {
        return binder;
    }
}
