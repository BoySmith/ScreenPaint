package com.zyb.screenpaint;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zhangyb on 2017/12/28.
 */

public class ToolsLayout extends RelativeLayout implements View.OnClickListener {

    private ImageView paintPointImageView; //工具栏中的表示画笔粗细的圆点，点击父layout可启动画图
    private SeekBar seekBar; //调整画笔粗细的进度条

    private ImageButton blackButton;
    private ImageButton redButton;
    private ImageButton blueButton;
    private ImageButton lightBlueButton;
    private ImageButton greenButton;
    private ImageButton yellowButton;
    private ImageButton orangeButton;

    private Context context;

    public ToolsLayout(Context context) {
        this(context, null);
    }

    public ToolsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToolsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        View toolsView = LayoutInflater.from(context).inflate(R.layout.tools, this, true);
        paintPointImageView = toolsView.findViewById(R.id.paint_point_image_view);
        seekBar = toolsView.findViewById(R.id.size);

        blackButton = toolsView.findViewById(R.id.blackButton);
        redButton = toolsView.findViewById(R.id.redButton);
        blueButton = toolsView.findViewById(R.id.blueButton);
        lightBlueButton = toolsView.findViewById(R.id.lightBlueButton);
        greenButton = toolsView.findViewById(R.id.greenButton);
        yellowButton = toolsView.findViewById(R.id.yellowButton);
        orangeButton = toolsView.findViewById(R.id.orangeButton);
    }

    private void initData() {
        final ViewGroup.LayoutParams editParams = paintPointImageView.getLayoutParams();
        editParams.height = 10; //paintView.brushSize
        editParams.width = 10;
        paintPointImageView.setLayoutParams(editParams);

        seekBar.setProgress(10);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                paintView.brushSize = progress;
                EventBus.getDefault().post(1);

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

    private void initListener() {
        blackButton.setOnClickListener(this);
        redButton.setOnClickListener(this);
        blueButton.setOnClickListener(this);
        lightBlueButton.setOnClickListener(this);
        greenButton.setOnClickListener(this);
        yellowButton.setOnClickListener(this);
        orangeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int color;
        int paintDrawable;
        switch (v.getId()) {
            case R.id.blackButton:
                color = R.color.black;
                paintDrawable = R.drawable.paint_black;
                break;
            case R.id.redButton:
                color = R.color.red;
                paintDrawable = R.drawable.paint_red;
                break;
            case R.id.blueButton:
                color = R.color.blue;
                paintDrawable = R.drawable.paint_blue;
                break;
            case R.id.lightBlueButton:
                color = R.color.light_blue;
                paintDrawable = R.drawable.paint_light_blue;
                break;
            case R.id.greenButton:
                color = R.color.green;
                paintDrawable = R.drawable.paint_green;
                break;
            case R.id.yellowButton:
                color = R.color.yellow;
                paintDrawable = R.drawable.paint_yellow;
                break;
            case R.id.orangeButton:
                color = R.color.orange;
                paintDrawable = R.drawable.paint_orange;
                break;
            default:
                color = R.color.black;
                paintDrawable = R.drawable.paint_black;
        }
        setPaintPointColor(context.getResources().getColor(color));
//        editChooseButton.setBackgroundResource(paintDrawable);
    }

    private void setPaintPointColor(int color) {
//        paintView.penColor = color;
        GradientDrawable myGrad = (GradientDrawable) paintPointImageView.getBackground();
        myGrad.setColor(color);
    }

    private void setPaintPointSize(int size) {
        ViewGroup.LayoutParams paintPointParams = paintPointImageView.getLayoutParams();
        paintPointParams.height = size;
        paintPointParams.width = size;
        paintPointImageView.setLayoutParams(paintPointParams);
    }

    private void setSeekBarProgress(int progress) {
        seekBar.setProgress(progress);
    }

    static class Builder {
        private final Context context;

        private Integer seekBarProgress;
        private Integer paintPointColor;
        private Integer paintPointSize;

        Builder(Context context) {
            this.context = context;
        }

        Builder setSeekBarProgress(int progress) {
            seekBarProgress = progress;
            return this;
        }

        Builder setPaintPointColor(int color) {
            paintPointColor = color;
            return this;
        }

        Builder setPaintPointSize(int size) {
            paintPointSize = size;
            return this;
        }

        ToolsLayout create() {
            final ToolsLayout toolsLayout = new ToolsLayout(context);

            if (paintPointColor != null) {
                toolsLayout.setPaintPointColor(paintPointColor);
            }
            if (paintPointSize != null) {
                toolsLayout.setPaintPointSize(paintPointSize);
            }
            if (seekBarProgress != null) {
                toolsLayout.setSeekBarProgress(seekBarProgress);
            }

            return toolsLayout;
        }
    }
}
