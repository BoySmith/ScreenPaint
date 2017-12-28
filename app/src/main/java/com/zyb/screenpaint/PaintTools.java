package com.zyb.screenpaint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

class PaintTools {
    private RelativeLayout editView; //作画编辑的view，全屏且是透明背景，画笔在它上面编辑画图；包括paintView、关闭按钮
    private PaintView paintView; //真正作画的view
    private ImageButton editDeleteButton; //关闭 edit view 按钮
    private ImageButton editUnDoButton; //撤销上一笔
    private ImageButton editReDoButton; //还原上一笔
    private ImageButton editChooseButton; //还原上一笔

    private View penFloatLayout = null; //画笔悬浮view，包括 penImageView、penGuideTextView；点击可显示 toolsFloatLayout
    private ImageView penFloatImageView; //画笔悬浮view
    private ImageView arrowImageView; //首次使用提示 的箭头
    private TextView penGuideTextView; //首次使用时的 指引提示

    private View toolsFloatLayout = null; //悬浮工具view，可调整画笔粗细，选择画笔颜色等
    private ImageView paintPointImageView; //工具栏中的表示画笔粗细的圆点，点击父layout可启动画图
    private SeekBar seekBar; //调整画笔粗细的进度条

    private int currentYPosition = -1; //记录 画笔悬浮view 的位置
    private float penViewTouchStartY = 0; //记录按下 画笔悬浮view 时的 y坐标

    private Context context;
    private Resources resources;

    private SharedPreferences sharedPreferences; //本地存储，主要记录是否是首次启动，画笔的粗细、颜色、画笔最后的位置
    private WindowManager windowManager = null;
    private LayoutParams windowManagerParams = null;

    PaintTools(Context c, Resources r) {
        context = c;
        resources = r;
    }

    void createView() {
        initWindowManagerParams();

        //画笔悬浮view
        penFloatLayout = LayoutInflater.from(context).inflate(R.layout.pen_float_view, null);
        penFloatImageView = penFloatLayout.findViewById(R.id.pen);
        arrowImageView = penFloatLayout.findViewById(R.id.arrow);
        penGuideTextView = penFloatLayout.findViewById(R.id.penGuide);

        //编辑绘图view
        editView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.edit_view_layout, null);

        //工具栏悬浮view
        toolsFloatLayout = editView.findViewById(R.id.edit_tools_layout);
        paintPointImageView = toolsFloatLayout.findViewById(R.id.paint_point_image_view);
        seekBar = toolsFloatLayout.findViewById(R.id.size);

        sharedPreferences = context.getSharedPreferences("user_info", 0);
        if (sharedPreferences.getBoolean("showGuide", true)) {
            //首次启动 显示指引UI
            arrowImageView.setVisibility(View.VISIBLE);
            penGuideTextView.setVisibility(View.VISIBLE);
            sharedPreferences.edit().putBoolean("showGuide", false).apply();
        }

        currentYPosition = sharedPreferences.getInt("penYPosition", -1);
        addPenFloatView();
        setPenFloatViewListener(); //设置悬浮画笔view 的监听事件

        initButton();
        initPaintView(); //初始化 画笔绘画的view
        initSeekBar(); //初始化 调整画笔粗细的进度条
    }

    private void initButton() {
        //三个颜色view
        ImageView red = toolsFloatLayout.findViewById(R.id.colorRed);
        ImageView orange = toolsFloatLayout.findViewById(R.id.colorOrange);
        ImageView black = toolsFloatLayout.findViewById(R.id.colorBlack);
        ImageView lightBlue = toolsFloatLayout.findViewById(R.id.colorLightBlue);
        ImageView yellow = toolsFloatLayout.findViewById(R.id.colorYellow);
        ImageView green = toolsFloatLayout.findViewById(R.id.colorGreen);
        ImageView blue = toolsFloatLayout.findViewById(R.id.colorBlue);
        red.setOnClickListener(colorImageClick);
        green.setOnClickListener(colorImageClick);
        blue.setOnClickListener(colorImageClick);
        orange.setOnClickListener(colorImageClick);
        black.setOnClickListener(colorImageClick);
        yellow.setOnClickListener(colorImageClick);
        lightBlue.setOnClickListener(colorImageClick);

        initEditButton();
    }

    private void initEditButton() {
        editDeleteButton = editView.findViewById(R.id.edit_delete_button);
        editUnDoButton = editView.findViewById(R.id.edit_undo_button);
        editReDoButton = editView.findViewById(R.id.edit_redo_button);
        editChooseButton = editView.findViewById(R.id.edit_choose_button);
        editDeleteButton.setOnClickListener(editButtonClick);
        editUnDoButton.setOnClickListener(editButtonClick);
        editReDoButton.setOnClickListener(editButtonClick);
        editChooseButton.setOnClickListener(editButtonClick);
    }

    private OnClickListener editButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.edit_delete_button:
                    exitEditState();
                    context.sendBroadcast(new Intent("super_finishPaintEditActivity"));
                    break;

                case R.id.edit_undo_button:
                    paintView.unDo();
                    if (paintView.nodesIsEmpty()) {
                        editUnDoButton.setBackgroundResource(R.drawable.undo_no);
                    }
                    editReDoButton.setBackgroundResource(R.drawable.redo_drawable);
                    break;

                case R.id.edit_redo_button:
                    paintView.reDo();
                    if (paintView.reDosIsEmpty()) {
                        editReDoButton.setBackgroundResource(R.drawable.redo_no);
                    }
                    editUnDoButton.setBackgroundResource(R.drawable.undo_drawable);
                    break;

                case R.id.edit_choose_button:
                    if (toolsFloatLayout.getVisibility() == View.VISIBLE) {
                        toolsFloatLayout.setVisibility(View.GONE);
                    } else {
                        toolsFloatLayout.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

    private OnClickListener colorImageClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int color;
            int paintDrawable;
            switch (v.getId()) {
                case R.id.colorBlack:
                    color = R.color.black;
                    paintDrawable = R.drawable.paint_black;
                    break;
                case R.id.colorRed:
                    color = R.color.red;
                    paintDrawable = R.drawable.paint_red;
                    break;
                case R.id.colorBlue:
                    color = R.color.blue;
                    paintDrawable = R.drawable.paint_blue;
                    break;
                case R.id.colorLightBlue:
                    color = R.color.light_blue;
                    paintDrawable = R.drawable.paint_light_blue;
                    break;
                case R.id.colorGreen:
                    color = R.color.green;
                    paintDrawable = R.drawable.paint_green;
                    break;
                case R.id.colorYellow:
                    color = R.color.yellow;
                    paintDrawable = R.drawable.paint_yellow;
                    break;
                case R.id.colorOrange:
                    color = R.color.orange;
                    paintDrawable = R.drawable.paint_orange;
                    break;
                default:
                    color = R.color.black;
                    paintDrawable = R.drawable.paint_black;
            }
            setPaintViewColor(resources.getColor(color));
            editChooseButton.setBackgroundResource(paintDrawable);
            sharedPreferences.edit().putInt("paintDrawable", paintDrawable).apply();
        }
    };

    private void setPaintViewColor(int color) {
        paintView.penColor = color;
        GradientDrawable myGrad = (GradientDrawable) paintPointImageView.getBackground();
        myGrad.setColor(paintView.penColor);
    }

    private void initPaintView() {
        paintView = new PaintView(context);
        paintView.penColor = sharedPreferences.getInt("penColor", resources.getColor(R.color.black));
        paintView.brushSize = sharedPreferences.getInt("penSize", 10);
        paintView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((PaintView) v).startPath(x, y);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    ((PaintView) v).addPath(x, y);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!paintView.nodesIsEmpty()) {
                        editUnDoButton.setBackgroundResource(R.drawable.undo_drawable);
                    }
                }
                return true;
            }
        });
    }

    private void initSeekBar() {
        final ViewGroup.LayoutParams editParams = paintPointImageView.getLayoutParams();
        editParams.height = paintView.brushSize;
        editParams.width = paintView.brushSize;
        paintPointImageView.setLayoutParams(editParams);

        seekBar.setProgress(paintView.brushSize);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                paintView.brushSize = progress;

                editParams.height = progress;
                editParams.width = progress;
                paintPointImageView.setLayoutParams(editParams);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
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
        windowManagerParams.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        windowManagerParams.width = LayoutParams.WRAP_CONTENT;
        windowManagerParams.height = LayoutParams.WRAP_CONTENT;
    }

    private void setPenFloatViewListener() {
        penFloatImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                windowRemoveView(penFloatLayout);
                addEditView();

                //点击 画笔悬浮view 之后，就可以把指引的UI去掉了
                arrowImageView.setVisibility(View.GONE);
                penGuideTextView.setVisibility(View.GONE);
            }
        });

        penFloatImageView.setOnTouchListener(new OnTouchListener() {
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
        });
    }

    private void updatePenFloatLayoutPosition() {
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        windowManagerParams.gravity = Gravity.TOP | Gravity.LEFT;
        windowManagerParams.x = 0;
        if (currentYPosition >= screenHeight) {
            windowManagerParams.y = screenHeight - penFloatImageView.getHeight();
        } else {
            windowManagerParams.y = currentYPosition;
        }
        windowManager.updateViewLayout(penFloatLayout, windowManagerParams);
    }

    private void windowAddView(View view, ViewGroup.LayoutParams params) {
        if (view.getParent() != null) {
            windowManager.removeView(view);
        }
        windowManager.addView(view, params);
    }

    private void windowRemoveView(View view) {
        if (view.getParent() != null) {
            windowManager.removeView(view);
        }
    }

    /**
     * 添加画笔悬浮view到界面，并设置 touch监听
     */
    private void addPenFloatView() {
        windowManagerParams.width = LayoutParams.WRAP_CONTENT;
        windowManagerParams.height = LayoutParams.WRAP_CONTENT;

        if (currentYPosition == -1) {
            windowManagerParams.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
            windowManagerParams.y = 0;
        } else {
            windowManagerParams.gravity = Gravity.TOP | Gravity.LEFT;
            windowManagerParams.y = currentYPosition;
        }
        windowManagerParams.x = 0;

        windowAddView(penFloatLayout, windowManagerParams);
    }

    /**
     * 添加编辑作画的view
     */
    private void addEditView() {
        windowManagerParams.gravity = Gravity.TOP | Gravity.LEFT; //51
        windowManagerParams.width = LayoutParams.MATCH_PARENT;
        windowManagerParams.height = LayoutParams.MATCH_PARENT;
        windowAddView(editView, windowManagerParams);

        paintView.ClearAll();
        editView.removeAllViews();

        editView.addView(paintView);
        editView.addView(editDeleteButton);
        editUnDoButton.setBackgroundResource(R.drawable.undo_no);
        editView.addView(editUnDoButton);
        editReDoButton.setBackgroundResource(R.drawable.redo_no);
        editView.addView(editReDoButton);

        editChooseButton.setBackgroundResource(sharedPreferences.getInt("paintDrawable", R.drawable.paint_black));
        editView.addView(editChooseButton);

        setPaintViewColor(paintView.penColor);
        toolsFloatLayout.setVisibility(View.GONE);
        editView.addView(toolsFloatLayout);
    }

    void exitEditState() {
        saveEditPaintInfo();

        editView.removeAllViews();
        windowRemoveView(editView);

        addPenFloatView();
    }


    /********************** 下面是暂时未用到的 *************************/
    public void destroy() {
        windowRemoveView(penFloatLayout);
    }

    private void saveEditPaintInfo() {
        sharedPreferences.edit().putInt("penColor", getColor()).apply();
        sharedPreferences.edit().putInt("penSize", getSize()).apply();
    }

    public int getColor() {
        if (paintView != null) {
            return paintView.penColor;
        }
        return resources.getColor(R.color.black);
    }

    private int getSize() {
        if (paintView != null) {
            return paintView.brushSize;
        }
        return 10;
    }

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
