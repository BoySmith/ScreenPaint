package com.zyb.screenpaint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * Created by zhangyb on 2017/12/28.
 */

public class EditLayout extends RelativeLayout implements View.OnClickListener, View.OnTouchListener {

    private ToolsLayout toolsLayou; //悬浮工具view，可调整画笔粗细，选择画笔颜色等
    private PaintView paintView; //真正作画的view

    private ImageButton editDeleteButton; //关闭 edit view 按钮
    private ImageButton editUnDoButton; //撤销上一笔
    private ImageButton editReDoButton; //还原上一笔
    private ImageButton editChooseButton; //还原上一笔

    private Context context;
    private SharedPreferences sharedPreferences;

    public EditLayout(Context context) {
        this(context, null);
    }

    public EditLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        View editView = LayoutInflater.from(context).inflate(R.layout.edit_view_layout, this, true);
        toolsLayou = editView.findViewById(R.id.edit_tools_layout);
        paintView = new PaintView(context);

        editDeleteButton = editView.findViewById(R.id.edit_delete_button);
        editUnDoButton = editView.findViewById(R.id.edit_undo_button);
        editReDoButton = editView.findViewById(R.id.edit_redo_button);
        editChooseButton = editView.findViewById(R.id.edit_choose_button);
    }

    private void initData() {
        sharedPreferences = context.getSharedPreferences("user_info", 0);
        paintView.penColor = sharedPreferences.getInt("penColor", context.getResources().getColor(R.color.black));
        paintView.brushSize = sharedPreferences.getInt("penSize", 10);
    }

    private void initListener() {
        editDeleteButton.setOnClickListener(this);
        editUnDoButton.setOnClickListener(this);
        editReDoButton.setOnClickListener(this);
        editChooseButton.setOnClickListener(this);
        paintView.setOnTouchListener(this);
    }

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
                if (toolsLayou.getVisibility() == View.VISIBLE) {
                    toolsLayou.setVisibility(View.GONE);
                } else {
                    toolsLayou.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ((PaintView) v).startPath(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                ((PaintView) v).addPath(x, y);
                break;
            case MotionEvent.ACTION_UP:
                if (!paintView.nodesIsEmpty()) {
                    editUnDoButton.setBackgroundResource(R.drawable.undo_drawable);
                }
                break;
        }
        return true;
    }
}
