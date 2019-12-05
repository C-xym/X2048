package com.example.x.x2048.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;

import com.example.x.x2048.R;
import com.example.x.x2048.Utils;
import com.example.x.x2048.model.Grid;


public class XGridView extends GridLayout {

    private float x1, x2, y1, y2;
    private int length;
    private Paint bgPaint;

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
        length = widthMeasureSpec & View.MEASURED_SIZE_MASK;
        //设置高度等于宽度
        setMeasuredDimension(widthMeasureSpec, length);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == bgPaint) {
            bgPaint = new Paint();
            bgPaint.setColor(getResources().getColor(R.color.blank_block, getContext().getTheme()));
        }
        int eight=Utils.dip2px(getContext(),8);
        int L = (length - Utils.dip2px(getContext(),40)) / 4 + eight;
        for (int a = 0; a < 4; a++) {
            for (int b = 0; b < 4; b++) {
                canvas.drawRoundRect(eight + a * L, eight + b * L, L + a * L, L + b * L, eight/2, eight/2, bgPaint);
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
