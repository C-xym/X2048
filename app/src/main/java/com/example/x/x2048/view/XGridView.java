package com.example.x.x2048.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridLayout;

import com.example.x.x2048.model.Grid;


public class XGridView extends GridLayout {

    private float x1, x2, y1, y2;

    public XGridView(Context context) {
        super(context);
    }

    public XGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public XGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置高度等于宽度
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                x2 = event.getX();
                y2 = event.getY();
                break;
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:

                int dir = 0;
                if (Math.abs(x1 - x2) > Math.abs(y1 - y2)) {
                    if (x1 > x2) {
                        dir = Grid.MOVE_LEFT;
                    } else {
                        dir = Grid.MOVE_RIGHT;
                    }
                } else {
                    if (y1 > y2) {
                        dir = Grid.MOVE_UP;
                    } else {
                        dir = Grid.MOVE_DOWN;
                    }
                }
                Grid.getInstance().move(dir);
                mOnFlashListener.onFlash(dir);
                break;
        }
        return true;
    }

    public interface OnFlashListener {
        void onFlash(int direction);
    }
    private OnFlashListener mOnFlashListener;
    public void setOnFlashListener(OnFlashListener listener) {
        this.mOnFlashListener = listener;
    }
}
