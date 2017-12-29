package com.zyb.screenpaint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * Created by zhangyb on 2017/12/28.
 */

public class EditPaintLayout extends RelativeLayout implements View.OnClickListener, View.OnTouchListener,
        ToolsLayout.ToolsListener {

    private ToolsLayout toolsLayout; //悬浮工具view，可调整画笔粗细，选择画笔颜色等
    private PaintView paintView; //真正作画的view

    private ImageButton editDeleteButton; //关闭 edit view 按钮
    private ImageButton editUnDoButton; //撤销上一笔
    private ImageButton editReDoButton; //还原上一笔
    private ImageButton editPenButton; //画笔

    private Context context;
    private SharedPreferences sharedPreferences;

    public EditPaintLayout(Context context) {
        this(context, null);
    }

    public EditPaintLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditPaintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        View editLayout = LayoutInflater.from(context).inflate(R.layout.edit_layout, this, true);
        toolsLayout = editLayout.findViewById(R.id.edit_tools_layout);
        paintView = editLayout.findViewById(R.id.edit_paint_view);
        editDeleteButton = editLayout.findViewById(R.id.edit_delete_button);
        editUnDoButton = editLayout.findViewById(R.id.edit_undo_button);
        editReDoButton = editLayout.findViewById(R.id.edit_redo_button);
        editPenButton = editLayout.findViewById(R.id.edit_pen_button);
    }

    private void initData() {
        sharedPreferences = context.getSharedPreferences("user_info", 0);
        paintView.penColor = sharedPreferences.getInt("penColor", context.getResources().getColor(R.color.black));
        paintView.brushSize = sharedPreferences.getInt("penSize", 10);
        editPenButton.setBackgroundResource(sharedPreferences.getInt("penDrawable", R.drawable.paint_black));

        toolsLayout.setPaintPointSize(paintView.brushSize);
        toolsLayout.setSeekBarProgress(paintView.brushSize);
    }

    private void initListener() {
        editDeleteButton.setOnClickListener(this);
        editUnDoButton.setOnClickListener(this);
        editReDoButton.setOnClickListener(this);
        editPenButton.setOnClickListener(this);
        paintView.setOnTouchListener(this);
        toolsLayout.setOnTouchListener(this);
        toolsLayout.setToolsListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_delete_button:
                sharedPreferences.edit().putInt("penColor", paintView.penColor).apply();
                sharedPreferences.edit().putInt("penSize", paintView.brushSize).apply();
                if (editPenButton.getTag() != null) {
                    sharedPreferences.edit().putInt("penDrawable", ((int) editPenButton.getTag())).apply();
                }

                context.sendBroadcast(new Intent("super_finishPaintEditActivity"));
                break;

            case R.id.edit_undo_button:
                paintView.unDo();
                if (paintView.nodesIsEmpty()) {
                    editUnDoButton.setBackgroundResource(R.drawable.undo_no);
                }
                if (!paintView.reDosIsEmpty()) {
                    editReDoButton.setBackgroundResource(R.drawable.redo_drawable);
                }
                break;

            case R.id.edit_redo_button:
                paintView.reDo();
                if (paintView.reDosIsEmpty()) {
                    editReDoButton.setBackgroundResource(R.drawable.redo_no);
                }
                if (!paintView.nodesIsEmpty()) {
                    editUnDoButton.setBackgroundResource(R.drawable.undo_drawable);
                }
                break;

            case R.id.edit_pen_button:
                toolsLayout.setVisibility(toolsLayout.getVisibility() == VISIBLE ? GONE : VISIBLE);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.equals(toolsLayout)) {
            return true;
        }

        if (toolsLayout.getVisibility() == VISIBLE) {
            toolsLayout.setVisibility(GONE);
            return true;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ((PaintView) v).startPath(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                ((PaintView) v).movePath(x, y);
                break;

            case MotionEvent.ACTION_UP:
                ((PaintView) v).stopPath(x, y);

                if (!paintView.nodesIsEmpty()) {
                    editUnDoButton.setBackgroundResource(R.drawable.undo_drawable);
                }
                break;
        }
        return true;
    }

    @Override
    public void paintPenResChanged(int resId) {
        editPenButton.setTag(resId);
        editPenButton.setBackgroundResource(resId);
    }

    @Override
    public void paintPenPointSizeChanged(int size) {
        paintView.brushSize = size;
    }

    @Override
    public void paintPenColorChanged(int color) {
        paintView.penColor = color;
    }
}
