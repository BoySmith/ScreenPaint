package com.zyb.screenpaint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.view.View;

import java.util.ArrayList;

public class PaintView extends View {
    public int brushSize = 10;
    Context context;
    private ArrayList<Node> nodes = new ArrayList<>();
    private Path path;
    public int penColor;
    private ArrayList<Node> reDos = new ArrayList<>();

    private class Node {
        Paint Paint;
        Path Path;
        Point Point;
    }

    public PaintView(Context context) {
        super(context);
        this.context = context;
        penColor = context.getResources().getColor(R.color.red);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            canvas.drawPath(node.Path, node.Paint);
        }
    }

    public void startPath(int x, int y) {
        reDos = new ArrayList<>();

        path = new Path();
        path.moveTo((float) x, (float) y);

        Node node = new Node();
        node.Paint = getPaint();
        node.Path = path;
        node.Point = new Point(x, y);
        nodes.add(node);

        invalidate();
    }

    public Paint getPaint() {
        Paint mPaint = new Paint();

        mPaint.setDither(true);
        mPaint.setColor(penColor);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeJoin(Join.ROUND);
        mPaint.setStrokeCap(Cap.ROUND);
        mPaint.setStrokeWidth((float) brushSize);

        return mPaint;
    }

    public void addPath(int x, int y) {
        path.lineTo((float) x, (float) y);
        invalidate();
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
     * @return 画面中有笔数，返回true；否则返回false
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
     * @return 撤销的笔数大于0，返回true；否则返回false
     */
    public boolean reDo() {
        int len = reDos.size();
        if (len > 0) {
            Node node = reDos.get(len - 1);
            reDos.remove(node);
            nodes.add(node);
            invalidate();
        }
        return reDos.size() > 0;
    }
}
