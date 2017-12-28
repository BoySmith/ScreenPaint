package com.zyb.screenpaint;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;

public class EditPaintActivity extends Activity {
    BroadcastReceiver receiver;
    boolean justFinish = false;

    EditPaintLayout editPaintLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.edit_layout);

        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                justFinish = true;
                finish();
            }
        };

        registerReceiver(receiver, new IntentFilter("super_finishPaintEditActivity"));

        editPaintLayout = findViewById(R.id.edit_paint_layout);
    }

    protected void onPause() {
        super.onPause();
        //编辑界面 onPause 时，要发送 退出编辑画图 的广播
        if (!justFinish) {
            sendBroadcast(new Intent("super_painter_exit_edit"));
            finish();
        }
    }

    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
