package com.zyb.screenpaint;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class VerticalSeekBar extends AppCompatSeekBar {
    private static final String TAG = "VerticalSeekBar";
    private boolean mIsDragging;
    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    interface OnSeekBarChangeListener {
        void onProgressChanged(VerticalSeekBar verticalSeekBar, int i, boolean z);

        void onStartTrackingTouch(VerticalSeekBar verticalSeekBar);

        void onStopTrackingTouch(VerticalSeekBar verticalSeekBar);
    }

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas canvas) {
        canvas.rotate(-90);
        canvas.translate(-getHeight(), 0);
        super.onDraw(canvas);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                onStartTrackingTouch();
                trackTouchEvent(event);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsDragging) {
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                }
                setPressed(false);
                break;

            case MotionEvent.ACTION_MOVE:
                setPressed(true);
                if (!mIsDragging) {
                    onStartTrackingTouch();
                }
                trackTouchEvent(event);
                break;
        }
        return true;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }

    private void onStartTrackingTouch() {
        mIsDragging = true;
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    private void onStopTrackingTouch() {
        mIsDragging = false;
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    private void trackTouchEvent(MotionEvent event) {
        float scale;
        int y = (int) event.getY();
        int height = getHeight();
        int paddingRight = getPaddingRight();
        int available = (height - paddingRight) - getPaddingLeft();
        if (y < paddingRight) {
            scale = 1;
        } else if (y > height - paddingRight) {
            scale = 0;
        } else {
            scale = ((float) ((available - y) + paddingRight)) / ((float) available);
        }

        float progress = scale * ((float) getMax());
        setProgress((int) progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);

        if (this.mOnSeekBarChangeListener != null) {
            this.mOnSeekBarChangeListener.onProgressChanged(this, (int) progress, true);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
