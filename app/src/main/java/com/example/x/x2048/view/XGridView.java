package com.example.x.x2048.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.Toast;

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
            mViewList.add(new View(getContext()));
        }
        mViews = new View[16];

        mGrid = Grid.getInstance();

        mPadding = Utils.dip2px(getContext(), 8);

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

        ArrayList<Integer> remove = mGrid.getRemoveList();
        for (Integer c : remove) {
            removeBlock(c);
        }
        final ArrayMap<Integer, Integer> move = mGrid.getMoveList();
        final Set<Integer> set = move.keySet();
        Integer[] setArr = new Integer[set.size()];
        set.toArray(setArr);


        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        switch (dir) {
            case Grid.MOVE_LEFT:
            case Grid.MOVE_RIGHT:
                for (Integer c : set) {
                    int length = (move.get(c) - c) * (mLength + mPadding);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(mViews[c], "TranslationX", length);
                    animators.add(animator);
                }
                break;
            case Grid.MOVE_UP:
            case Grid.MOVE_DOWN:
                for (Integer c : set) {
                    int length = (move.get(c) / 4 - c / 4) * (mLength + mPadding);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(mViews[c], "TranslationY", length);
                    animators.add(animator);
                }
                break;
        }
        animatorSet.playTogether(animators);
        animatorSet.start();


        switch (dir) {
            case Grid.MOVE_LEFT:
            case Grid.MOVE_RIGHT:
                for (Integer c : set) {
                    int length = (move.get(c) - c) * (mLength + mPadding);
                    int left=mViews[c].getLeft()+length;
                    int right=mViews[c].getRight()+length;
                    mViews[c].setLeft(left);
                    mViews[c].setRight(right);

                    LayoutParams lp= (LayoutParams) mViews[c].getLayoutParams();
                    lp.leftMargin=left;
                    mViews[c].setLayoutParams(lp);
                }
                break;
            case Grid.MOVE_UP:
            case Grid.MOVE_DOWN:
                for (Integer c : set) {
                    int length = (move.get(c) / 4 - c / 4) * (mLength + mPadding);
                    int top=mViews[c].getTop()+length;
                    int bottom=mViews[c].getBottom()+length;
                    mViews[c].setTop(top);
                    mViews[c].setBottom(bottom);
                    LayoutParams lp= (LayoutParams) mViews[c].getLayoutParams();
                    lp.topMargin=top;
                    mViews[c].setLayoutParams(lp);
                }
                break;
        }

        for(int i=set.size()-1;i>=0;i--){
            transPosA2B(setArr[i],move.get(setArr[i]));
        }

        addBlock(mGrid.getNewblock(), mGrid.getNewType());

        ArrayMap<Integer, Integer> map = mGrid.getNewList();
        Set<Integer> set2 = map.keySet();
        for (Integer c : set2) {
            mViews[c].setBackgroundResource(mDrawMap.get(map.get(c)));
        }


//        StringBuilder a=new StringBuilder(),b=new StringBuilder(),c=new StringBuilder(),d= new StringBuilder();
//        for(int i=0;i<4;i++){
//            if(null==mViews[i]){
//                a.append("0 ");
//            }
//            else {
//                a.append("1 ");
//            }
//        }
//        for(int i=4;i<8;i++){
//            if(null==mViews[i]){
//                b.append("0 ");
//            }
//            else {
//                b.append("1 ");
//            }
//        }
//        for(int i=8;i<12;i++){
//            if(null==mViews[i]){
//                c.append("0 ");
//            }
//            else {
//                c.append("1 ");
//            }
//        }
//        for(int i=12;i<16;i++){
//            if(null==mViews[i]){
//                d.append("0 ");
//            }
//            else {
//                d.append("1 ");
//            }
//        }
//
//        Log.i("myTest"," "+mViewList.size());
//        Log.i("myTest"," "+a);
//        Log.i("myTest"," "+b);
//        Log.i("myTest"," "+c);
//        Log.i("myTest"," "+d);
    }

    private void removeBlock(int postion) {
        mViewList.add(mViews[postion]);
        this.removeView(mViews[postion]);
        mViews[postion] = null;
    }

    private void transPosA2B(int a, int b) {
        if(null!=mViews[b]){
            Log.i("myTest"," "+"error");
        }
        mViews[b] = mViews[a];
        mViews[a] = null;
    }

    private void addBlock(int postion, int drawId) {
        View view = mViewList.get(mViewList.size() - 1);
        mViewList.remove(mViewList.size() - 1);
        if(null!=mViews[postion]){
            Log.i("myTest"," "+"error2");
        }
        mViews[postion] = view;
        int x = postion % 4;
        int y = postion / 4;
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
