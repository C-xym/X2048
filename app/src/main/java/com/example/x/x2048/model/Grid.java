package com.example.x.x2048.model;

import android.content.SharedPreferences;
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.Arrays;

public class Grid {

    public static final int MOVE_UP = 0;
    public static final int MOVE_RIGHT = 1;
    public static final int MOVE_DOWN = 2;
    public static final int MOVE_LEFT = 3;

    private static Grid sIns;

    private ArrayList<Block> mBlockList;
    private SparseIntArray mMoveList;
    private SparseIntArray mNewList;

    private Block[] mBlocks;
    private int[] old = new int[16];
    private int[] trans = new int[16];

    private int blank;
    private int mNewblock;
    private int mNewValue;

    private boolean mBack;
    private boolean mEffectiveMove;
    private boolean mGameOver;


    public boolean isEffectiveMove() {
        return mEffectiveMove;
    }
    public int[] getTrans() {
        return trans;
    }

    public int getNewblock() {
        return mNewblock;
    }

    public int getNewValue() {
        return mNewValue;
    }


    public boolean isGameOver() {
        return mGameOver;
    }





    public int[] getIntArr() {
        int[] a = new int[16];
        for (int i = 0; i < 16; i++) {
            if (null == mBlocks[i]) {
                a[i] = 0;
            } else {
                a[i] = mBlocks[i].mValue;
            }
        }
        return a;
    }

    public SparseIntArray getMoveList() {
        return mMoveList;
    }

    public SparseIntArray getNewList() {
        return mNewList;
    }


    private Grid() {

        mBlockList = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            Block block = new Block(i, 0, 0);
            mBlockList.add(block);
        }
        mMoveList = new SparseIntArray();
        mNewList = new SparseIntArray();

        mBlocks = new Block[16];
        blank = 16;
        mBack = false;
        mGameOver = false;
        mEffectiveMove = false;
    }

    public static Grid getInstance() {
        if (sIns == null) {
            sIns = new Grid();
        }
        return sIns;
    }

    public void load(SharedPreferences preferences) {
        for (int i = 0; i < 16; i++) {
            int x = preferences.getInt(String.valueOf(i), 0);
            if (x != 0) {
                Block block = mBlockList.get(mBlockList.size() - 1);
                mBlockList.remove(mBlockList.size() - 1);
                block.set(i, x);
                mBlocks[i] = block;
            }
        }
        blank = preferences.getInt("blank", 16);
        mBack = false;
    }

    public void save(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        int[] arr = getIntArr();
        for (int i = 0; i < 16; i++) {
            editor.putInt(String.valueOf(i), arr[i]);
        }
        editor.putInt("blank", blank);
        editor.apply();
    }

    public void back() {
        if (mBack) {
            for (int i = 0; i < 16; i++) {
                if (old[i] == 0) {
                    if (null != mBlocks[i]) {
                        mBlockList.add(mBlocks[i]);
                        mBlocks[i] = null;
                    }
                } else {
                    if (null == mBlocks[i]) {
                        Block block = mBlockList.get(mBlockList.size() - 1);
                        mBlockList.remove(mBlockList.size() - 1);
                        mBlocks[i] = block;
                    }
                    mBlocks[i].set(i, old[i]);
                }
            }
            mBack = false;
            mGameOver = false;
            int b = 0;
            for (int i = 0; i < 16; i++) {
                if (old[i] == 0) b++;
            }
            blank = b;
        }
    }

    public void reStart() {
        mBlockList.clear();
        for (int i = 0; i < 16; i++) {
            Block block = new Block(i, 0, 0);
            mBlockList.add(block);
        }
        mBlocks = new Block[16];
        blank = 16;
        mNewList.clear();
        mMoveList.clear();
        mBack = false;
        mGameOver = false;
    }

    public void move(int orientation) {

        mEffectiveMove = false;
        if (blank == 16) {
            genNewBlock();
            mBack = true;
            mEffectiveMove = true;
            return;
        }
        mMoveList.clear();
        mNewList.clear();

        mGameOver = gameOver();
        if (mGameOver) {
            return;
        }

        int[] temp = getIntArr();

        for (int i = 0; i < 16; i++) {
            trans[i] = i;
        }

        switch (orientation) {
            case MOVE_UP:
                moveUp();
                break;
            case MOVE_RIGHT:
                moveRight();
                break;
            case MOVE_DOWN:
                moveDown();
                break;
            case MOVE_LEFT:
                moveLeft();
                break;
            default:
                break;
        }

        if (mMoveList.size()>0) {
            genNewBlock();
            mBack = true;
            mEffectiveMove = true;
            if (!Arrays.equals(temp, getIntArr())) {
                old = temp;
            }
        }
    }

    //.mValue
    private boolean gameOver() {
        if (blank != 0) return false;
        for (int i = 1; i < 15; i += 4) {
            if (mBlocks[i].mValue == mBlocks[i - 1].mValue || mBlocks[i].mValue == mBlocks[i + 1].mValue || mBlocks[i + 1].mValue == mBlocks[i + 2].mValue)
                return false;
        }

        for (int i = 4; i < 8; i++) {
            if (mBlocks[i].mValue == mBlocks[i - 4].mValue || mBlocks[i].mValue == mBlocks[i + 4].mValue || mBlocks[i + 4].mValue == mBlocks[i + 8].mValue)
                return false;
        }

        return true;
    }


    private void moveUp() {

        boolean[] booleans = new boolean[16];//false
        for (int a = 4; a < 16; a++) {
            int i = a;
            if (mBlocks[i] == null) continue;
            while (i - 4 >= 0 && mBlocks[i - 4] == null) {
                i = i - 4;
            }
            if (i - 4 >= 0 && mBlocks[i - 4].mValue == mBlocks[a].mValue && !booleans[i - 4]) {
                moveAdd(a,i,booleans,-4);
            } else {
                if (a != i) {
                    mBlocks[i] = mBlocks[a];
                    mBlocks[a] = null;
                    mMoveList.put(a, i);
                    trans[i] = a;
                }
            }
        }
    }

    private void moveDown() {
        boolean[] booleans = new boolean[16];//false
        for (int a = 11; a >= 0; a--) {
            int i = a;
            if (mBlocks[i] == null) continue;
            while (i + 4 < 16 && mBlocks[i + 4] == null) {
                i = i + 4;
            }
            if (i + 4 < 16 && mBlocks[i + 4].mValue == mBlocks[a].mValue && !booleans[i + 4]) {
                moveAdd(a,i,booleans,4);
            } else {
                if (a != i) {
                    mBlocks[i] = mBlocks[a];
                    mBlocks[a] = null;
                    mMoveList.put(a, i);
                    trans[i] = a;
                }
            }
        }
    }

    private void moveLeft() {
        boolean[] booleans = new boolean[16];//false
        for (int a = 1; a < 16; a++) {
            int i = a;
            if (mBlocks[i] == null) continue;
            while (i % 4 - 1 >= 0 && mBlocks[i - 1] == null) {
                i = i - 1;
            }
            if (i % 4 - 1 >= 0 && mBlocks[i - 1].mValue == mBlocks[a].mValue && !booleans[i - 1]) {
                moveAdd(a,i,booleans,-1);
            } else {
                if (a != i) {
                    mBlocks[i] = mBlocks[a];
                    mBlocks[a] = null;
                    mMoveList.put(a, i);
                    trans[i] = a;
                }
            }
        }
    }

    private void moveRight() {
        boolean[] booleans = new boolean[16];//false
        for (int a = 14; a >= 0; a--) {
            int i = a;
            if (mBlocks[i] == null) continue;
            while (i % 4 + 1 < 4 && mBlocks[i + 1] == null) {
                i = i + 1;
            }
            if (i % 4 + 1 < 4 && mBlocks[i + 1].mValue == mBlocks[a].mValue && !booleans[i + 1]) {
                moveAdd(a,i,booleans,1);
            } else {
                if (a != i) {
                    mBlocks[i] = mBlocks[a];
                    mBlocks[a] = null;
                    mMoveList.put(a, i);
                    trans[i] = a;
                }
            }
        }
    }

    private void moveAdd(int a,int i,boolean[] booleans,int x){
        mBlockList.add(mBlocks[a]);
        mBlocks[a] = null;
        mBlocks[i + x].mValue *= 2;
        booleans[i + x] = true;
        mMoveList.put(a, i + x);
        trans[i + x] = a;
        mNewList.put(i + x, mBlocks[i + x].mValue);
        blank++;
    }


    private void genNewBlock() {
        if (blank == 0) return;
        int n = (int) (Math.random() * 100) % blank;
        int i = 0;
        while (n >= 0) {
            if (mBlocks[i] == null) {
                --n;
            }
            ++i;
        }
        int f = (int) (Math.random() * 10);
        mNewblock = i - 1;
        Block block = mBlockList.get(mBlockList.size() - 1);
        mBlockList.remove(mBlockList.size() - 1);
        if (f < 2) {
            block.set(i - 1, 4);
            mBlocks[i - 1] = block;
            mNewList.put(i - 1, 4);
            mNewValue = 4;
        } else {
            block.set(i - 1, 2);
            mBlocks[i - 1] = block;
            mNewList.put(i - 1, 2);
            mNewValue = 2;
        }
        blank--;
    }
}
