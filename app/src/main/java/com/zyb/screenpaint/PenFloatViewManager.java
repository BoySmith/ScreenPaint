package com.zyb.screenpaint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

class PenFloatViewManager {
    private View penFloatLayout = null; //画笔悬浮view，包括 penImageView、penGuideTextView；点击可显示 toolsFloatLayout
    private ImageView penFloatImageView; //画笔悬浮view
    private ImageView arrowImageView; //首次使用提示 的箭头
    private TextView penGuideTextView; //首次使用时的 指引提示

    private int currentYPosition = -1; //记录 画笔悬浮view 的位置
    private float penViewTouchStartY = 0; //记录按下 画笔悬浮view 时的 y坐标

    private Context context;

    private SharedPreferences sharedPreferences; //本地存储，主要记录是否是首次启动，画笔的粗细、颜色、画笔最后的位置
    private WindowManager windowManager = null;
    private LayoutParams windowManagerParams = null;

    PenFloatViewManager(Context context) {
        this.context = context;
    }

    void createView() {
        initWindowManagerParams();

        //画笔悬浮view
        penFloatLayout = LayoutInflater.from(context).inflate(R.layout.pen_float_view, null);
        penFloatImageView = penFloatLayout.findViewById(R.id.pen);
        arrowImageView = penFloatLayout.findViewById(R.id.arrow);
        penGuideTextView = penFloatLayout.findViewById(R.id.penGuide);

        //首次启动指引
        sharedPreferences = context.getSharedPreferences("user_info", 0);
        if (sharedPreferences.getBoolean("showGuide", true)) {
            //首次启动 显示指引UI
            arrowImageView.setVisibility(View.VISIBLE);
            penGuideTextView.setVisibility(View.VISIBLE);
            sharedPreferences.edit().putBoolean("showGuide", false).apply();
        }

        currentYPosition = sharedPreferences.getInt("penYPosition", -1);
        addPenFloatView();

        penFloatImageView.setOnClickListener(penFloatClickListener);
        penFloatImageView.setOnTouchListener(penFloatTouchListener); //设置悬浮画笔view 的监听事件
    }

    private void initWindowManagerParams() {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManagerParams = new LayoutParams();

        //在小米7.0系统上测试还存在问题，所以在 else 上改为 TYPE_SYSTEM_ALERT 后问题解决。
        //todo 可能存在适配问题
        if (!getShouldToast(context) || VERSION.SDK_INT < 19) {
            windowManagerParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            windowManagerParams.type = LayoutParams.TYPE_SYSTEM_ALERT; // LayoutParams.TYPE_TOAST
        }

        windowManagerParams.format = PixelFormat.TRANSPARENT;
        windowManagerParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
    }

    /**
     * 添加画笔悬浮view到界面，并设置 touch监听
     */
    void addPenFloatView() {
        windowManagerParams.width = LayoutParams.WRAP_CONTENT;
        windowManagerParams.height = LayoutParams.WRAP_CONTENT;

        if (currentYPosition == -1) {
            windowManagerParams.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
            windowManagerParams.y = 0;
        } else {
            windowManagerParams.gravity = Gravity.TOP | Gravity.START;
            windowManagerParams.y = currentYPosition;
        }
        windowManagerParams.x = 0;

        windowManager.addView(penFloatLayout, windowManagerParams);
    }

    private OnClickListener penFloatClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            windowManager.removeView(penFloatLayout);

            Intent intent = new Intent(context, EditPaintActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            //点击 画笔悬浮view 之后，就可以把指引的UI去掉了
            arrowImageView.setVisibility(View.GONE);
            penGuideTextView.setVisibility(View.GONE);
        }
    };

    private OnTouchListener penFloatTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    penViewTouchStartY = event.getRawY();
                    return false;

                // 抬起后的返回值 决定是否 响应 onClick
                case MotionEvent.ACTION_UP:
                    if (Math.abs(event.getRawY() - penViewTouchStartY) > 5) {
                        sharedPreferences.edit().putInt("penYPosition", currentYPosition).apply();
                        return true;
                    }
                    return false;

                case MotionEvent.ACTION_MOVE:
                    currentYPosition = (int) (event.getRawY() - ((float) (penFloatImageView.getHeight() / 2)));
                    updatePenFloatLayoutPosition();
                    return true;
            }
            return false;
        }
    };

    private void updatePenFloatLayoutPosition() {
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        windowManagerParams.gravity = Gravity.TOP | Gravity.START;
        windowManagerParams.x = 0;
        if (currentYPosition >= screenHeight) {
            windowManagerParams.y = screenHeight - penFloatImageView.getHeight();
        } else {
            windowManagerParams.y = currentYPosition;
        }
        windowManager.updateViewLayout(penFloatLayout, windowManagerParams);
    }

    /********************** 下面是暂时未用到的 *************************/

    private static Boolean getShouldToast(Context paramContext) {
        String manufac = null;
        try {
            manufac = Build.MANUFACTURER.toLowerCase(Locale.getDefault());
        } catch (Exception ignored) {
        }

        Log.i("zyb", "manufac : " + manufac);

        if (manufac == null || (!manufac.contains("huawei") && !manufac.contains("vivo") && !manufac.contains
                ("xiaomi") && !manufac.contains("meizu") && !manufac.contains("oppo"))) {
            return false;
        }
        if (manufac.contains("xiaomi") && Utils.isMiuiInstalled(paramContext)) {
            return true;
        }
        if (manufac.contains("meizu") && Utils.isNewMz()) {
            return true;
        }
        if (manufac.contains("huawei")) {
            return ((double) Utils.getEmuiVersion()) > 3;

        } else if (manufac.contains("vivo")) {
            return true;

        } else if (manufac.contains("oppo")) {
            String colorOsVersion = Utils.getSystemProperty("ro.build.version.opporom");
            return colorOsVersion == null || !colorOsVersion.contains("V1.");
        }
        return false;
    }
}
