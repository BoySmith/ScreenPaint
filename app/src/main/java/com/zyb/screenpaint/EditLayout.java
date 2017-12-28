package com.zyb.screenpaint;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

/**
 * Created by zhangyb on 2017/12/28.
 */

public class EditLayout extends RelativeLayout {

    private View toolsFloatLayout = null; //悬浮工具view，可调整画笔粗细，选择画笔颜色等
    private ImageView paintPointImageView; //工具栏中的表示画笔粗细的圆点，点击父layout可启动画图
    private SeekBar seekBar; //调整画笔粗细的进度条

    private Context context;

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
    }

    private void initView() {
        View editView = LayoutInflater.from(context).inflate(R.layout.edit_view_layout, this, true);
        toolsFloatLayout = editView.findViewById(R.id.edit_tools_layout);
        paintPointImageView = toolsFloatLayout.findViewById(R.id.paint_point_image_view);
        seekBar = toolsFloatLayout.findViewById(R.id.size);
    }

    private void initData() {

    }
}
