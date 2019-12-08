package com.example.x.x2048.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.x.x2048.R;
import com.example.x.x2048.Utils;
import com.example.x.x2048.model.Grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class XGridView extends FrameLayout {

    private float x1, x2, y1, y2;
    private int mLength, mPadding;
    private Paint bgPaint;
    private ArrayList<View> mViewList = null;
    private HashMap<Integer, Integer> mDrawMap = null;
    private View[] mViews;
    private Grid mGrid;
    private XGridView mXGridView;

    private SparseIntArray mLeftArray, mTopArray;

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
        //设置高度等于宽度
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    private void init() {
        bgPaint = new Paint();
        bgPaint.setColor(getResources().getColor(R.color.blank_block, getContext().getTheme()));

        mViewList = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            View view = new View(getContext());
            view.setId(i);
            mViewList.add(view);
        }
        mViews = new View[16];

        mGrid = Grid.getInstance();

        mPadding = Utils.dip2px(getContext(), 8);

        mLeftArray = new SparseIntArray();
        mTopArray = new SparseIntArray();

        mDrawMap = new HashMap<>();
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

        mXGridView = this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

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
                GridFlash(dir);
                break;
        }
        return true;
    }

    private void GridFlash(final int dir) {

        final ArrayMap<Integer, Integer> move = mGrid.getMoveList();
        final Set<Integer> set = move.keySet();

        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        switch (dir) {
            case Grid.MOVE_LEFT:
            case Grid.MOVE_RIGHT:
                for (Integer c : set) {
                    int length = (move.get(c) % 4) * (mLength + mPadding) + mPadding - mViews[c].getLeft();
                    ObjectAnimator animator = ObjectAnimator.ofFloat(mViews[c], "TranslationX", length);
                    animators.add(animator);
                }
                break;
            case Grid.MOVE_UP:
            case Grid.MOVE_DOWN:
                for (Integer c : set) {
                    int length = (move.get(c) / 4) * (mLength + mPadding) + mPadding - mViews[c].getTop();
                    ObjectAnimator animator = ObjectAnimator.ofFloat(mViews[c], "TranslationY", length);
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
                StringBuilder s = new StringBuilder();
                s.append("test\n");
                for (int i = 0; i < 16; i++) {
                    s.append(trans[i]).append(" ");
                    if (i == 3 || i == 7 || i == 11) {
                        s.append("\n");
                    }
                }
                Log.i("myTest2", String.valueOf(s));

                View[] views = new View[16];


                for (int i = 0; i < 16; i++) {
                    if (i != trans[i]) {
                        views[i] = mViews[trans[i]];
                    } else {
                        views[i] = mViews[i];
                    }
                }

                int[] arr = mGrid.getArr();

                for (int i = 0; i < 16; i++) {
                    if (arr[i] == 0 && null != views[i]) {
//                        mXGridView.removeView(views[i]);
                        mViewList.add(views[i]);
                        views[i] = null;
                    }
                }

                if (views[mGrid.getNewblock()] != null) {
                    mXGridView.removeView(views[mGrid.getNewblock()]);
                    mViewList.add(views[mGrid.getNewblock()]);
                    views[mGrid.getNewblock()] = null;
                }

                mXGridView.removeAllViews();
                for (int i = 0; i < 16; i++) {
                    mViews[i] = views[i];
                    if (null != mViews[i]) {
                        mXGridView.addView(mViews[i]);
                    }
                }

                addBlock(mGrid.getNewblock(), mGrid.getNewType());

                for (int i = 0; i < 16; i++) {
                    if (arr[i] != 0) {
                        if (mViews[i] != null) {
                            mViews[i].setBackgroundResource(mDrawMap.get(arr[i]));
                        }
                    }
                }
                //animation new
            }
        });

        if (animators.isEmpty()) {
            addBlock(mGrid.getNewblock(), mGrid.getNewType());
        }
    }

    private void addBlock(int postion, int drawId) {
//         View view = mViewList.get(mViewList.size() - 1);
//        mViewList.remove(mViewList.size() - 1);
        View view = new View(getContext());
        if (null != mViews[postion]) {
            Log.i("myTest", " " + "error2");
        }
        mViews[postion] = view;
        int x = postion % 4;
        int y = postion / 4;
        mLeftArray.append(view.getId(), x);
        mTopArray.append(view.getId(), y);
        LayoutParams params = new FrameLayout.LayoutParams(mLength, mLength);
        params.setMargins(mPadding + x * (mPadding + mLength), mPadding + y * (mPadding + mLength), 0, 0);
        view.setLayoutParams(params);
        view.setBackgroundResource(mDrawMap.get(drawId));
        this.addView(view);
    }

    public interface OnFlashListener {
        void onFlash(int direction);
    }

    private OnFlashListener mOnFlashListener;

    public void setOnFlashListener(OnFlashListener listener) {
        this.mOnFlashListener = listener;
    }
}
