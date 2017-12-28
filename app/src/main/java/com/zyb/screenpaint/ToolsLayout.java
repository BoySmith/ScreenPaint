package com.zyb.screenpaint;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by zhangyb on 2017/12/28.
 */

public class ToolsLayout extends RelativeLayout {

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
    }

    private void initView() {
        View toolsView = LayoutInflater.from(context).inflate(R.layout.tools, this, true);
    }

    private void initData() {

    }
}
