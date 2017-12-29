package com.zyb.screenpaint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class PaintView extends View {
    public int brushSize = 10;
    public int penColor;

    private int startPathX;
    private int startPathY;

    private ArrayList<Node> nodes = new ArrayList<>();
    private ArrayList<Node> reDos = new ArrayList<>();

    Context context;
    private Path path;

    private class Node {
        Paint Paint;
        Path Path;
        boolean isPoint;
        int pointX;
        int pointY;
    }

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.context = context;
        penColor = context.getResources().getColor(R.color.red);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node.isPoint) {
                canvas.drawPoint(node.pointX, node.pointY, node.Paint);
            } else {
                canvas.drawPath(node.Path, node.Paint);
            }
        }
    }

    public void startPath(int x, int y) {
        startPathX = x;
        startPathY = y;

        path = new Path();
        Node node = new Node();
        node.Paint = getPaint();
        node.Path = path;
        nodes.add(node);

        path.moveTo((float) x, (float) y);
    }

    public void movePath(int x, int y) {
        if (path == null) {
            return;
        }

        path.lineTo((float) x, (float) y);
        invalidate();
    }

    /**
     * 停止画笔，当画笔只是点击一下时，显示一个圆点
     */
    public void stopPath(int x, int y) {
        if (Math.abs(x - startPathX) < 5 && Math.abs(y - startPathY) < 5) {
            int len = nodes.size();
            if (len > 0) {
                Node node = nodes.get(len - 1);
                node.isPoint = true;
                node.pointX = x;
                node.pointY = y;
                invalidate();
            }
        }
    }

    public void ClearAll() {
        nodes = new ArrayList<>();
        invalidate();
    }

    public boolean nodesIsEmpty() {
        return nodes.isEmpty();
    }

    public boolean reDosIsEmpty() {
        return reDos.isEmpty();
    }

    /**
     * 撤销上一笔
     */
    public void unDo() {
        int len = nodes.size();
        if (len > 0) {
            Node node = nodes.get(len - 1);
            reDos.add(node);
            nodes.remove(node);
            invalidate();
        }
    }

    /**
     * 还原上一笔
     */
    public void reDo() {
        int len = reDos.size();
        if (len > 0) {
            Node node = reDos.get(len - 1);
            reDos.remove(node);
            nodes.add(node);
            invalidate();
        }
    }

    private Paint getPaint() {
        Paint mPaint = new Paint();

        mPaint.setDither(true);
        mPaint.setColor(penColor);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeJoin(Join.ROUND);
        mPaint.setStrokeCap(Cap.ROUND);
        mPaint.setStrokeWidth((float) brushSize);

        return mPaint;
    }
}
