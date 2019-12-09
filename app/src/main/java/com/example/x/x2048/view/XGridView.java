package com.example.x.x2048.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.example.x.x2048.R;
import com.example.x.x2048.Utils;
import com.example.x.x2048.model.Grid;

import java.util.ArrayList;


public class XGridView extends FrameLayout {

    private float x1, x2, y1, y2;
    private int mLength, mPadding, mLP;
    private Paint bgPaint;
    private SparseIntArray mDrawMap = null;
    private View[] mViews;
    private Grid mGrid;
    private XGridView mXGridView;
    private boolean isAnimaFinish;

    public XGridView(Context context) {
        super(context);
        init();
    }

    public XGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public XGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public XGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int l = widthMeasureSpec & View.MEASURED_SIZE_MASK;
        mLength = (l - Utils.dip2px(getContext(), 40)) / 4;
        mLP = mLength + mPadding;
        //设置高度等于宽度
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    private void init() {
        bgPaint = new Paint();
        bgPaint.setColor(getResources().getColor(R.color.blank_block, getContext().getTheme()));

        mViews = new View[16];
        mGrid = Grid.getInstance();
        mPadding = Utils.dip2px(getContext(), 8);
        mXGridView = this;
        isAnimaFinish = true;

        mDrawMap = new SparseIntArray();
        mDrawMap.put(0, R.drawable.blank);
        mDrawMap.put(2, R.drawable.block_0002);
        mDrawMap.put(4, R.drawable.block_0004);
        mDrawMap.put(8, R.drawable.block_0008);
        mDrawMap.put(16, R.drawable.block_0016);
        mDrawMap.put(32, R.drawable.block_0032);
        mDrawMap.put(64, R.drawable.block_0064);
        mDrawMap.put(128, R.drawable.block_0128);
        mDrawMap.put(256, R.drawable.block_0256);
        mDrawMap.put(512, R.drawable.block_0512);
        mDrawMap.put(1024, R.drawable.block_1024);
        mDrawMap.put(2048, R.drawable.block_2048);
        mDrawMap.put(4096, R.drawable.block_4096);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int a = 0; a < 4; a++) {
            for (int b = 0; b < 4; b++) {
                canvas.drawRoundRect(mPadding + a * mLP, mPadding + b * mLP,
                        (a + 1) * mLP, (b + 1) * mLP, mPadding / 2, mPadding / 2, bgPaint);
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
                if (isAnimaFinish) {
                    isAnimaFinish = false;
                    Grid.getInstance().move(dir);
                    GridFlash(dir);
                    mOnFlashListener.onFlash(dir);
                }
                break;
        }
        return true;
    }

    private void GridFlash(final int dir) {

        SparseIntArray move = mGrid.getMoveList();

        AnimatorSet animatorSet = new AnimatorSet();
        final ArrayList<Animator> animators = new ArrayList<>();
        switch (dir) {
            case Grid.MOVE_LEFT:
            case Grid.MOVE_RIGHT:
                for (int i = 0, s = move.size(); i < s; i++) {
                    int c = move.keyAt(i);
                    int length = (move.get(c) % 4) * mLP + mPadding - mViews[c].getLeft();
                    ObjectAnimator animator = ObjectAnimator.ofFloat(mViews[c], "TranslationX", length);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(120);
                    animators.add(animator);
                }
                break;
            case Grid.MOVE_UP:
            case Grid.MOVE_DOWN:
                for (int i = 0, s = move.size(); i < s; i++) {
                    int c = move.keyAt(i);
                    int length = (move.get(c) / 4) * mLP + mPadding - mViews[c].getTop();
                    ObjectAnimator animator = ObjectAnimator.ofFloat(mViews[c], "TranslationY", length);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(120);
                    animators.add(animator);
                }
                break;
        }
        animatorSet.playTogether(animators);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                int[] trans = mGrid.getTrans();
                View[] views = new View[16];

                for (int i = 0; i < 16; i++) {
                    if (i != trans[i]) {
                        if (trans[i] == 16) {
                            views[i] = null;
                        } else {
                            views[i] = mViews[trans[i]];
                        }
                    } else {
                        views[i] = mViews[i];
                    }
                }

                mXGridView.removeAllViews();
                for (int i = 0; i < 16; i++) {
                    mViews[i] = views[i];
                    if (null != mViews[i]) {
                        mXGridView.addView(mViews[i]);
                    }
                }
                addBlock(mGrid.getNewblock(), mGrid.getNewValue());

                AnimatorSet scaleSet = new AnimatorSet();
                ArrayList<Animator> scaleList = new ArrayList<>();
                SparseIntArray newArray = mGrid.getNewList();
                for (int i = 0, s = newArray.size(); i < s; i++) {
                    int k = newArray.keyAt(i);
                    mViews[k].setBackgroundResource(mDrawMap.get(newArray.get(k)));
                    ObjectAnimator animatorX = ObjectAnimator.ofFloat(mViews[k], "ScaleX", (float) 0.95, (float) 1.12, (float) 1.0);
                    ObjectAnimator animatorY = ObjectAnimator.ofFloat(mViews[k], "ScaleY", (float) 0.95, (float) 1.12, (float) 1.0);
                    animatorX.setInterpolator(new AccelerateInterpolator());
                    animatorY.setInterpolator(new AccelerateInterpolator());
                    animatorX.setDuration(100);
                    animatorY.setDuration(100);
                    scaleList.add(animatorX);
                    scaleList.add(animatorY);
                }
                scaleSet.playTogether(scaleList);
                scaleSet.start();
                isAnimaFinish = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimaFinish = true;
            }
        });

        if (animators.isEmpty()) {
            isAnimaFinish = true;
            if (mGrid.isEffectiveMove()) {
                addBlock(mGrid.getNewblock(), mGrid.getNewValue());
            }
        }
    }

    private void addBlock(int postion, int drawId) {
        View view = new View(getContext());
        mViews[postion] = view;
        LayoutParams params = new FrameLayout.LayoutParams(mLength, mLength);
        params.setMargins(mPadding + (postion % 4) * mLP, mPadding + (postion / 4) * mLP, 0, 0);
        view.setLayoutParams(params);
        view.setBackgroundResource(mDrawMap.get(drawId));
        this.addView(view);
    }

    public void reStart() {
        mXGridView.removeAllViews();
        mViews = new View[16];
        mGrid.reStart();
    }

    public void back() {
        mXGridView.removeAllViews();
        mViews = new View[16];
        mGrid.back();
        int[] arr = mGrid.getIntArr();
        for (int i = 0; i < 16; i++) {
            if (arr[i] != 0) {
                addBlock(i, arr[i]);
            }
        }
    }

    public void setGrid() {
        int[] arr = mGrid.getIntArr();
        for (int i = 0; i < 16; i++) {
            if (arr[i] != 0) {
                addBlock(i, arr[i]);
            }
        }
    }

    public interface OnFlashListener {
        void onFlash(int direction);
    }

    private OnFlashListener mOnFlashListener;

    public void setOnFlashListener(OnFlashListener listener) {
        this.mOnFlashListener = listener;
    }
}
