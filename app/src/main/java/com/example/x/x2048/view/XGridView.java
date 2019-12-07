package com.example.x.x2048.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

import com.example.x.x2048.model.Grid;


public class XGridView extends GridLayout {

    private float x1, x2, y1, y2;
    private int mLength, mPadding;

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
        int l = widthMeasureSpec & View.MEASURED_SIZE_MASK;
        mLength = (l - Utils.dip2px(getContext(), 40)) / 4;
        heightMeasureSpec = widthMeasureSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPadding = Utils.dip2px(getContext(), 8);
        for (int a = 0; a < 4; a++) {
            for (int b = 0; b < 4; b++) {
                canvas.drawRoundRect(mPadding + a * (mLength + mPadding), mPadding + b * (mLength + mPadding),
                        (a + 1) * (mLength + mPadding), (b + 1) * (mLength + mPadding), mPadding / 2, mPadding / 2, bgPaint);
            }
        }
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
